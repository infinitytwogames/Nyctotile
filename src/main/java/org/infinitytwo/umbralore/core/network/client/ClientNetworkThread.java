package org.infinitytwo.umbralore.core.network.client;

import org.infinitytwo.umbralore.core.constants.LogicalSide;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.network.PacketReceived;
import org.infinitytwo.umbralore.core.network.NetworkHandler;
import org.infinitytwo.umbralore.core.network.NetworkThread;
import org.infinitytwo.umbralore.core.security.Authentication;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.infinitytwo.umbralore.core.constants.PacketType.*;

public final class ClientNetworkThread extends NetworkThread {
    public enum ConnectionState {
        DISCONNECTED,
        HANDSHAKE_START,
        AWAITING_KEY,
        KEY_ESTABLISHED,
        AWAITING_AUTH_ACK,
        CONNECTED
    }
    
    // ClientNetworkThread.java (Add to fields)
    private volatile ConnectionState state = ConnectionState.DISCONNECTED;
    private final SecureRandom secureRandom = new SecureRandom();
    private PublicKey serverPublicKey;
    private SecretKey aesKey;
    
    private InetAddress serverAddress;
    private int serverPort;
    private final NetworkHandler handler;
    private int initialHandshakePacketId;
    
    public ClientNetworkThread(EventBus eventBus, int port, String host, int serverPort) throws UnknownHostException {
        super(LogicalSide.CLIENT, eventBus, port);
        setName("Client Network Thread");
        
        serverAddress = InetAddress.getByName(host);
        this.serverPort = serverPort;
        handler = new NetworkHandler(eventBus,this,((packets, thread) ->
                System.out.println("Successfully Received All packets of: "+packets.id()+ "With size: "+packets.payload().length+" bytes")
        )); // Why isn't it printing?
        
        if (handler.getState() == Thread.State.NEW) {
            handler.start();
        }
    }
    
    public void connect() {
        connect(serverAddress, serverPort);
    }
    
    /**
     * Initiates the connection handshake with the server.
     * @param host The hostname or IP address of the server.
     * @param port The port of the server.
     */
    public void connect(InetAddress host, int port) {
        this.serverAddress = host;
        this.serverPort = port;
        
        System.out.println("Starting handshake: Requesting Server Public Key.");
        
        byte[] payload = "publicKey".getBytes(UTF_8);
        
        int packetId = ThreadLocalRandom.current().nextInt();
        this.initialHandshakePacketId = packetId; // Store the ID
        
        send(packetId, payload, serverAddress, serverPort, UNENCRYPTED.getType(), true, false);
    }
    
    // --- ENCRYPTION LOGIC: Confirmed to match corrected server output (no redundant byte) ---
    @Override
    protected byte[] encrypt(Packet packet) {
        if (aesKey == null) {
            // RSA Encrypt the AES Key bytes during key exchange
            if (packet.type() == EXCHANGE.getType() && serverPublicKey != null) {
                try {
                    return rsaEncrypt(packet.payload());
                } catch (GeneralSecurityException e) {
                    System.err.println("RSA Encryption failed during key exchange.");
                    return packet.toBytes();
                }
            }
            // All other non-encrypted traffic (e.g., handshake ping)
            return packet.toBytes();
        }
        
        try {
            System.out.println("Encrypting packet ID: " + packet.id());
            byte[] plain = packet.toBytes();
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] cipherText = cipher.doFinal(plain);
            
            // --- CONFIRMED FIX: Buffer correctly contains only IV (12) + CipherText + Tag (16) ---
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
            buffer.put(iv);
            buffer.put(cipherText);
            
            return buffer.array();
        } catch (Exception e) {
            System.err.println("AES Encryption failed:");
            e.printStackTrace();
            return packet.toBytes();
        }
    }
    
    
    public void ping() {
        ping(serverAddress, serverPort);
    }
    
    private byte[] rsaEncrypt(byte[] aesKeyBytes) throws GeneralSecurityException {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        return rsa.doFinal(aesKeyBytes);
    }
    
    // --- DECRYPTION LOGIC: Remains valid for single-client AES key ---
    @Override
    protected byte[] decrypt(byte[] data, InetAddress address, int p) {
        if (aesKey == null) {
            // During handshake, return the raw data (expected to be public key)
            return data;
        }
        
        try {
            // Decryption expects IV (12) + Ciphertext + Tag (16)
            byte[] iv = new byte[12];
            System.arraycopy(data, 0, iv, 0, 12);
            
            byte[] cipherText = new byte[data.length - 12];
            System.arraycopy(data, 12, cipherText, 0, data.length - 12);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
            
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            System.err.println("AES Decryption failed:");
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    @Override
    protected boolean preProcessPacket(Packet packet) {
        // --- STAGE 0 & 1: HANDSHAKE ---
        if (aesKey == null) {
            return handleHandshake(packet);
        }
        
        // --- STAGE 2: AUTHENTICATED/ENCRYPTED TRAFFIC ---
        
        if (packet.type() == CONNECTION.getType()) {
            System.out.println("Connection successful! Server sent connection ACK.");
            this.state = ConnectionState.CONNECTED; // Final state
            
            System.out.println("Client is connected. Sending test command...");
            // Assuming COMMAND is PacketType 1 (based on the log 'Packet: 1/1, 1')
            send("echo Hello Secure World!", serverAddress, serverPort, COMMAND.getType(), true, true);
            return false;
        }
        
        if (state == ConnectionState.CONNECTED) eventBus.post(new PacketReceived(packet,packet.address())); // Turns out, this causes "Traffic from unauthenticated client: 127.0.0.1:5555. Dropping."
        return true;
    }
    
    private boolean handleHandshake(Packet packet) {
        // Public Key Received
        if (packet.type() == EXCHANGE.getType() && serverPublicKey == null) {
            
            // --- FIX 2: Clear the packet from the reliable queue (Initial Request) ---
            // Clears the initial request (ID: 1859381700 in this log)
            removePacketSent(packet.id());
            
            System.out.println("Received server public key.");
            try {
                this.serverPublicKey = Authentication.decodePublicKey(packet.payload());
                
                SecretKey generatedAesKey = Authentication.generateAESKey();
                
                this.aesKey = generatedAesKey;
                
                byte[] encryptedKey = rsaEncrypt(generatedAesKey.getEncoded());
                
                int newPacketId = ThreadLocalRandom.current().nextInt();
                // Send the encrypted AES key back to the server. Must be RELIABLE.
                send(newPacketId, encryptedKey, serverAddress, serverPort, EXCHANGE.getType(), true, false);
                state = ConnectionState.KEY_ESTABLISHED;
                
                System.out.println("Sent encrypted AES key to server. Awaiting ACK.");
                
            } catch (GeneralSecurityException e) {
                System.err.println("Error processing public key or generating/encrypting AES key.");
                e.printStackTrace();
            }
            return false;
        }
        
        // ACK from Server for AES Key Exchange OR Initial Connection
        else if (packet.type() == ACK.getType() && this.state == ConnectionState.KEY_ESTABLISHED) {
            
            // --- CRITICAL FIX 4: Explicitly remove the ACKed packet from the client's sent queue. ---
            // This ensures the reliable packet tracking is immediately updated, regardless of which ACK it is.
            removePacketSent(packet.id());
            
            if (packet.id() == initialHandshakePacketId) {
                System.out.println("Server acknowledged final handshake step (Initial ID: " + initialHandshakePacketId + "). Sending authentication...");
                
                sendAuthentication(packet.address(), packet.port());
                this.state = ConnectionState.AWAITING_AUTH_ACK;
                
            } else {
                // This is the ACK for the AES key exchange packet. We are now waiting for the final initial packet ACK.
                System.out.println("Received ACK for AES Key Exchange. Tracking complete for ID " + packet.id() + ". Waiting for final ACK.");
            }
            
            return false;
        }
        
        // Drop unhandled packets during handshake
        return false;
    }
    
    
    public void sendAuthentication() {
        sendAuthentication(serverAddress, serverPort);
    }
    
    // Helper method to trigger the first encrypted packet
    public void sendAuthentication(InetAddress serverAddress, int serverPort) {
        int authPacketId = ThreadLocalRandom.current().nextInt();
        
        String dummyToken = "FAKE_FIREBASE_TOKEN_FOR_OFFLINE_TESTING";
        byte[] tokenBytes = dummyToken.getBytes(UTF_8);
        
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + tokenBytes.length);
        buffer.putInt(tokenBytes.length);
        buffer.put(tokenBytes);
        
        byte[] authPayload = buffer.array();
        
        // Send the authentication packet. It must be RELIABLE (true) and ENCRYPTED (true).
        send(authPacketId, authPayload, serverAddress, serverPort, AUTHENTICATION.getType(), true, true);
    }
    
    public void send(String msg, boolean critical, boolean encrypted) {
        send(msg, serverAddress, serverPort, critical, encrypted);
    }
}