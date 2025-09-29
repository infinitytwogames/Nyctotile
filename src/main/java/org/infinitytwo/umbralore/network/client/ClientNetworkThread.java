package org.infinitytwo.umbralore.network.client;

import org.infinitytwo.umbralore.constants.LogicalSide;
import org.infinitytwo.umbralore.event.bus.LocalEventBus;
import org.infinitytwo.umbralore.event.network.NetworkFailure;
import org.infinitytwo.umbralore.logging.Logger;
import org.infinitytwo.umbralore.network.NetworkThread;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static org.infinitytwo.umbralore.constants.PacketType.*;

public final class ClientNetworkThread extends NetworkThread {
    private final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private final InetAddress host;
    private final int serverPort;

    private PublicKey serverPublicKey;   // Interestingly, it's used once
    private PrivateKey clientPrivateKey; // Interestingly, it's used once
    private PublicKey clientPublicKey;
    private boolean authenticated = false;
    private String tokenId = "";
    private int authenticationId;
    private SecretKey aesKey;
    private byte[] uuid;
    private Logger logger = new Logger("Client Network");

    public ClientNetworkThread(LocalEventBus eventBus, int port, String host, int serverPort) throws UnknownHostException {
        super(LogicalSide.CLIENT, eventBus, port);
        this.host = InetAddress.getByName(host);
        this.serverPort = serverPort;

        setName("Client Network Thread");
    }

    public void initEncryption(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;

        try {
            // Generate RSA key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.clientPublicKey = pair.getPublic();
            this.clientPrivateKey = pair.getPrivate();

            // Generate AES key (256-bit)
            KeyGenerator aesGen = KeyGenerator.getInstance("AES");
            aesGen.init(256);
            this.aesKey = aesGen.generateKey();

            // Send AES key encrypted with RSA
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
            send(encryptedAesKey, host, serverPort, EXCHANGE.getType(), true, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected byte[] encrypt(Packet packet) {
        if (aesKey == null) return packet.toBytes(); // not encrypted yet

        try {
            byte[] plain = packet.toBytes();
            byte[] iv = new byte[12];
            ThreadLocalRandom.current().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] cipherText = cipher.doFinal(plain);

            ByteBuffer buffer = ByteBuffer.allocate(1 + iv.length + cipherText.length);
            buffer.put((byte) 1);  // encryption flag
            buffer.put(iv);
            buffer.put(cipherText);
            return buffer.array();

        } catch (Exception e) {
            e.printStackTrace();
            return packet.toBytes();
        }
    }

    @Override
    protected byte[] decrypt(byte[] data) {
        if (aesKey == null || data[0] != 1) return data; // not encrypted

        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.get(); // Skip flag

            byte[] iv = new byte[12];
            buffer.get(iv);

            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
            return cipher.doFinal(cipherText);

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    @Override
    protected boolean preProcessPacket(Packet packet) {
        logger.info(packet.id(),"Received!");

        if (packet.type() == UNENCRYPTED.getType()) {
            String cmd = new String(packet.payload(), StandardCharsets.UTF_8).split(" ")[0];
            if (cmd.equals("pong") &&
                    !authenticated
            ) {
                logger.info("Received! Authenticating...");
                authenticate();
            } else if (cmd.equals("publicKey")) {
                logger.info("Public Key requested! sending...");
                byte[] keyBytes = clientPublicKey.getEncoded();
                send(packet.id(), keyBytes, host, EXCHANGE.getType(), true, false);
            }
        } else if (packet.id() == authenticationId &&
                    packet.type() == ACK.getType()
        ) {
            authenticated = true;
        } else if (packet.type() == CONNECTION.getType()) {
            authenticated = true;
            uuid = packet.payload();
            verifyConnection();
            ping();
        } else if (packet.type() == EXCHANGE.getType()) {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(packet.payload());
            KeyFactory keyFactory;
            try {
                keyFactory = KeyFactory.getInstance("RSA");
                initEncryption(keyFactory.generatePublic(spec));
            } catch (NoSuchAlgorithmException ignored) {
            } catch (InvalidKeySpecException e) {
                eventBus.post(new NetworkFailure(e));
            }
        }
        return true;
    }

    private void send(int id, byte[] keyBytes, InetAddress host, byte type, boolean critical, boolean encrypted) {
        send(id,keyBytes,host,serverPort,type,critical,encrypted);
    }

    private void ping() {
        super.ping(host,serverPort);
    }

    private void verifyConnection() {
        logger.info("Verifying Connection...");

        if (serverPublicKey != null || uuid.length == 0) {
            logger.error("Connection was unsuccessful!");
        }

        logger.info("Server's Public Key is",(serverPublicKey != null ? "not":""),"received");
        logger.info("Session UUID is", uuid.length == 0 ? "not" : "", "received");
    }

    private void send(int id, byte[] keyBytes, InetAddress host, byte type, boolean b) {
        send(id,keyBytes,host,serverPort,type,b,true);
    }

    public void connect(String tokenId) {
        ping(host, serverPort);
        this.tokenId = tokenId;
        authenticationId = ThreadLocalRandom.current().nextInt();
    }

    public void connect() {
        ping(host,serverPort);
        authenticationId = ThreadLocalRandom.current().nextInt();
    }

    private void authenticate() {
        byte[] msg = tokenId.getBytes(StandardCharsets.UTF_8);

        ByteBuffer b = ByteBuffer.allocate(Integer.BYTES + msg.length);
        b.put(msg);
        byte[] payload = b.array();

        send(authenticationId,payload,host,serverPort,AUTHENTICATION.getType(),true,false);
        send("publicKey",host,serverPort,UNENCRYPTED.getType(),true,false);
    }

    @Override
    public void send(int id, byte[] bytes, InetAddress clientAddress, int port, byte type, boolean isCritical, boolean e) {
        if (uuid == null) {
            if (e || type == AUTHENTICATION.getType()) uuid = new byte[16];
            else uuid = new byte[0];
//            throw new IllegalStateException("UUID not assigned yet. Wait for CONNECTION packet.");
        }
        ByteBuffer buffer = ByteBuffer.allocate(uuid.length + bytes.length);
        buffer.put(uuid);
        buffer.put(bytes);
        super.send(id, buffer.array(), clientAddress, port, type, isCritical, e);
    }

    public void send(String cmd, boolean b, boolean b1) {
        send(cmd,host,serverPort,b,b1);
    }
}
