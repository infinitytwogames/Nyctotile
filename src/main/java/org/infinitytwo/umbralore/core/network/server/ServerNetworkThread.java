package org.infinitytwo.umbralore.core.network.server;

import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.network.PacketReceived;
import org.infinitytwo.umbralore.core.manager.Players;
import org.infinitytwo.umbralore.core.constants.LogicalSide;
import org.infinitytwo.umbralore.core.data.PlayerData;
import org.infinitytwo.umbralore.core.network.NetworkThread;
import org.infinitytwo.umbralore.core.security.Authentication;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.infinitytwo.umbralore.core.constants.PacketType.*;

public final class ServerNetworkThread extends NetworkThread {
    
    private static final byte[] e = new byte[ 0 ];
    private final SecureRandom secureRandom = new SecureRandom();
    
    private PrivateKey serverPrivateKey;
    private PublicKey serverPublicKey;
    private final Map<String, SecretKey> clientAesKeys = new ConcurrentHashMap<>();
    
    private boolean onlineMode = true;
    private final Map<String, Integer> initialReliablePacketIds = new ConcurrentHashMap<>();
    
    public ServerNetworkThread(EventBus eventBus, int port) {
        super(LogicalSide.SERVER, eventBus, port);
        setName("Server Network Thread");
        generateRSAKeyPair();
    }
    
    // Helper to create a unique identifier for a client session (IP:Port)
    private String getClientKey(InetAddress address, int port) {
        return address.getHostAddress() + ":" + port;
    }
    
    private void generateRSAKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            serverPrivateKey = pair.getPrivate();
            serverPublicKey = pair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }
    
    @Override
    protected byte[] encrypt(Packet packet) {
        SecretKey aesKey = clientAesKeys.get(getClientKey(packet.address(), packet.port()));
        
        if (aesKey == null) {
            // WARNING: Encryption is only skipped if the client key is null,
            // but the base class Packet.toBytes() logic handles the 'No Encryption' flag.
            return packet.toBytes();
        }
        
        try {
            System.out.println("Encrypting packet ID: " + packet.id());
            byte[] plain = packet.toBytes();
            byte[] iv = new byte[ 12 ];
            
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] cipherText = cipher.doFinal(plain);
            
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
            buffer.put(iv);
            buffer.put(cipherText);
            
            return buffer.array();
        } catch (Exception e) {
            System.err.println("AES Encryption failed during transmission:");
            e.printStackTrace();
            return packet.toBytes();
        }
    }
    
    @Override
    protected byte[] decrypt(byte[] data, java.net.InetAddress address, int port) {
        String clientKey = getClientKey(address, port);
        SecretKey aesKey = clientAesKeys.get(clientKey);
        
        if (aesKey == null) {
            // If AES key is missing, decryption should fail and return empty array.
            return e;
        }
        
        try {
            // Decryption expects IV (12) + Ciphertext + Tag (16)
            byte[] iv = new byte[ 12 ];
            System.arraycopy(data, 0, iv, 0, 12);
            
            byte[] cipherText = new byte[ data.length - 12 ];
            System.arraycopy(data, 12, cipherText, 0, data.length - 12);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
            
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            System.err.println("AES Decryption failed (Bad Tag/Corruption) for client " + clientKey + ":");
            e.printStackTrace();
            return ServerNetworkThread.e;
        }
    }
    
    @Override
    protected boolean preProcessPacket(Packet packet) {
        
        // --- STAGE 0 & 1: HANDSHAKE ---
        String clientKey = getClientKey(packet.address(), packet.port());
        SecretKey clientKeyCheck = clientAesKeys.get(clientKey);
        
        if (clientKeyCheck == null) {
            return handleHandshake(packet);
        }
        
        // --- STAGE 2: AUTHENTICATED/ENCRYPTED TRAFFIC ---
        
        // 2. Handle Authentication
        if (packet.type() == AUTHENTICATION.getType()) {
            authenticate(packet);
            // CRITICAL FIX: Return false to prevent NetworkThread from sending a redundant implicit ACK.
            // The explicit ACK is handled inside authenticate().
            return false;
        }
        
        // 3. Resolve Player using Address/Port - Only check for non-authentication packets
        PlayerData playerData = Players.getPlayerByAddress(packet.address(), packet.port());
        
        if (playerData == null) {
            System.err.println("Traffic from unauthenticated client: " + clientKey + ". Dropping.");
            return false;
            
        } else {
            // Player is authenticated and in the Players map.
            
            if (packet.type() == COMMAND.getType()) {
                eventBus.post(new PacketReceived(packet, packet.address()));
                String cmd = new String(packet.payload(), StandardCharsets.UTF_8);
                if (cmd.startsWith("echo ")) {
                    String[] args = cmd.split(" ");
                    StringBuilder r = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        r.append(args[ i ]);
                    }
                    // Send the response back, encrypted.
                    send(r.toString(), packet.address(), packet.port(), true, true);
                }
                
                // CRITICAL FIX: Explicitly ACK the COMMAND packet and return false.
                // This ensures exactly ONE ACK is sent, preventing the loop/timeout.
                sendConfirmation(packet);
                return false;
            } else {}
        }
        
        
        return true;
    }
    
    private boolean handleHandshake(Packet packet) {
        if (packet.type() == UNENCRYPTED.getType()) {
            String command = new String(packet.payload(), UTF_8).trim();
            
            if (command.equals("publicKey")) {
                System.out.println("Client " + getClientKey(packet.address(), packet.port()) + " requested public key. Sending...");
                
                // Store the Initial Connection Request ID
                initialReliablePacketIds.put(getClientKey(packet.address(), packet.port()), packet.id());
                
                send(serverPublicKey.getEncoded(), packet.address(), packet.port(), EXCHANGE.getType(), false, false);
                return false;
            } else if (command.equals("ping")) {
                pong(packet.address(), packet.port());
                return false;
            }
            
        } else if (packet.type() == EXCHANGE.getType()) {
            try {
                System.out.println("Handling encrypted AES key from client " + getClientKey(packet.address(), packet.port()) + " (RSA decrypt)...");
                
                Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                rsa.init(Cipher.DECRYPT_MODE, serverPrivateKey);
                byte[] aesKeyBytes = rsa.doFinal(packet.payload());
                
                String clientKey = getClientKey(packet.address(), packet.port());
                clientAesKeys.put(clientKey, new javax.crypto.spec.SecretKeySpec(aesKeyBytes, "AES"));
                
                System.out.println("AES key established for client " + clientKey + ". Sending confirmation ACK.");
                
                // CRITICAL FIX: ACK the AES key packet
                sendConfirmation(packet.id(), packet.address(), packet.port());
                
                // Acknowledge the Stored Initial Connection Request ID
                Integer initialId = initialReliablePacketIds.remove(clientKey);
                if (initialId != null) {
                    System.out.println("ACKing initial connection request: " + initialId);
                    sendConfirmation(initialId, packet.address(), packet.port());
                }
                
                return false;
            } catch (GeneralSecurityException e) {
                System.err.println("RSA Decryption of AES key failed. Rejecting connection.");
                sendFailure(packet.id(), "Key exchange failed.", packet.address(), packet.port());
            }
            return false;
        }
        
        System.err.println("Received unhandled or premature packet (Type: " + packet.type() + ") from " + getClientKey(packet.address(), packet.port()) + " before AES key set.");
        return false;
    }
    
    private void authenticate(Packet packet) {
        try {
            byte[] payload = packet.payload();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
            DataInputStream inStream = new DataInputStream(inputStream);
            
            UUID playerId;
            String tokenId = "";
            
            if (onlineMode) {
                
                // Read Token
                int sizeTokenId = inStream.readInt();
                byte[] byteTokenId = new byte[sizeTokenId];
                inStream.readFully(byteTokenId);
                tokenId = new String(byteTokenId, UTF_8);
                
                System.out.println("Verifying Firebase token...");
                Authentication.FirebaseTokenPayload p = Authentication.verify(tokenId);
                
                playerId = UUID.fromString(p.uid());
                
            } else {
                System.out.println("Authentication skipped: Online mode is disabled (Offline).");
                playerId = UUID.randomUUID();
            }
            
            // 3. Final steps after successful authentication
            Players.join(new PlayerData(
                    packet.address(),
                    packet.port(),
                    "Player_" + playerId.toString().substring(0, 4),
                    playerId,
                    tokenId,
                    !tokenId.isEmpty()
            ));
            
            // Create the CONNECTION packet payload
            byte[] uuidPayload = ByteBuffer.allocate(16)
                    .putLong(playerId.getMostSignificantBits())
                    .putLong(playerId.getLeastSignificantBits())
                    .array();
            
            System.out.println("Sending connection confirmation with UUID: " + playerId);
            // Send reliable, encrypted CONNECTION packet
            send(packet.id(), uuidPayload, packet.address(), packet.port(), CONNECTION.getType(), true, true);
            
            // CRITICAL FIX: Send final ACK for the client's AUTHENTICATION packet.
            sendConfirmation(packet.id(), packet.address(), packet.port());
            System.out.println("Connection completed. Final ACK sent for ID: " + packet.id());
            
        } catch (Exception e) {
            System.err.println("Authentication processing error:");
            e.printStackTrace();
            sendFailure(packet.id(), "Authentication internal error.", packet.address(), packet.port());
        }
    }
    
    public static UUID bytesToUUID(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("UUID must be 16 bytes");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(buffer.getLong(), buffer.getLong());
        System.out.println("Converted bytes to UUID: " + uuid);
        return uuid;
    }
    
    public void offlineMode(boolean offline) {
        onlineMode = !offline;
    }
}