package org.infinitytwo.umbralore.core.network;

import org.infinitytwo.umbralore.core.constants.LogicalSide;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.network.NetworkFailure;
import org.infinitytwo.umbralore.core.event.network.PacketReceived;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.infinitytwo.umbralore.core.constants.PacketType.*;

public abstract class NetworkThread extends Thread {
    public final LogicalSide logicalSide;
    protected static final int MAX_HEADER_SIZE = (2 * Integer.BYTES) + (2 * Short.BYTES) + 2;
    protected final int port;
    protected final EventBus eventBus;
    protected DatagramSocket socket;
    // Tracks reliable packets *we* sent, waiting for ACK
    protected Map<Integer, List<Packet>> packetSent = new ConcurrentHashMap<>();
    protected volatile boolean close;

    // Protocol Header Size: ID (4) + Nonce (4) + Index (2) + Total (2) + Type (1) = 13 bytes
    public static final int PROTOCOL_HEADER_SIZE = (Integer.BYTES * 2) + (Short.BYTES * 2) + 1;

    // Max size of a UDP datagram packet we will send (1024 is a safe default)
    public static final int MAX_DATAGRAM_SIZE = 1024;

    // Max payload we can send after accounting for the encryption flag (1 byte) and header (13 bytes)
    public static final int MAX_PAYLOAD_SIZE = MAX_DATAGRAM_SIZE - 1 - PROTOCOL_HEADER_SIZE;

    public static List<byte[]> splitBytes(byte[] data, int maxChunkSize) {
        List<byte[]> chunks = new ArrayList<>();
        int length = data.length;

        for (int i = 0; i < length; i += maxChunkSize) {
            int end = Math.min(length, i + maxChunkSize);
            byte[] chunk = Arrays.copyOfRange(data, i, end);
            chunks.add(chunk);
        }

        return chunks;
    }

    public NetworkThread(LogicalSide side, EventBus eventBus, int port) {
        this.logicalSide = side;
        this.eventBus = eventBus;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println(eventBus.getProcess()+" is running.");
        try {
            this.socket = new DatagramSocket(port);

            // Use a buffer size that can safely handle the full MTU (1500)
            byte[] buffer = new byte[1500];

            while (!close) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());
                Packet parsedPacket = read(receivedData, packet.getAddress(), packet.getPort());

                System.out.println("Received: "+parsedPacket.toString());

                // preProcessPacket handles decryption/validation/auth state
                // evaluate handles internal protocol packets (ACK/NACK/PING)
                if (preProcessPacket(parsedPacket) && evaluate(parsedPacket)) {
                    eventBus.post(new PacketReceived(parsedPacket, packet.getAddress()));
                }
            }
        } catch (Exception e) {
            if (!close) {
                eventBus.post(new NetworkFailure(e));
            }
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private boolean evaluate(Packet packet) {
        int id = packet.id;

        // FIXED: Removed the erroneous sentPackets/nonceDuplicate check here

        if (packet.type == CMD_BYTE_DATA.getType()) {
            // Data fragment - passes to event bus for PacketAssembly
        } else if (packet.type == NACK.getType()) {
            byte[] payload = packet.payload;
            ByteBuffer buffer = ByteBuffer.allocate(payload.length);
            buffer.put(payload);
            buffer.position(0);
            
            short length = buffer.getShort();
            for (int i = 0; i < length; i++) {
                short index = buffer.getShort();
                send(packetSent.get(packet.id).get(index),false);
            }
            
            // NACKs are internal protocol and shouldn't hit the event bus
            return false;
        } else if (packet.type == ACK.getType()) {
            // RECEIVED confirmation that our critical packet was successful
            packetSent.remove(packet.id); // Correctly removes the tracking entry
            
            // ACKs are internal protocol and shouldn't hit the event bus
            return false;
        } else if (packet.type == COMMAND.getType()) {
            byte[] payload = packet.payload;

            String command = new String(payload, StandardCharsets.UTF_8);
            if (command.split(" ")[0].equals("ping")) {
                pong(packet.address, packet.port);
            }
            // Internal commands shouldn't hit the event bus.
            return false;
        }

        return true;
    }

    public void pong(InetAddress address, int clientPort) {
        send("pong", address, clientPort, UNENCRYPTED.getType(), false, false);
    }

    public void send(Packet packet, boolean isCritical) {
        // FIXED: Ensures correct parameters are passed
        send(packet.payload, packet.address, packet.port, packet.type, isCritical, true);
    }

    public void shutdown() {
        close = true;
        if (socket != null) {
            socket.close();
        }
        interrupt();
    }

    public void send(@NotNull String msg, InetAddress clientAddress, int clientPort, boolean isCritical, boolean encrypted) {
        send(msg.getBytes(StandardCharsets.UTF_8), clientAddress, clientPort, COMMAND.getType(), isCritical, encrypted);
    }

    public void send(byte[] bytes, InetAddress address, int clientPort, byte type, boolean isCritical, boolean encrypted) {
        send(ThreadLocalRandom.current().nextInt(), bytes, address, clientPort, type, isCritical, encrypted);
    }

    public void send(String msg, InetAddress address, int clientPort, byte type, boolean isCritical, boolean encrypted) {
        send(ThreadLocalRandom.current().nextInt(), msg.getBytes(StandardCharsets.UTF_8), address, clientPort, type, isCritical, encrypted);
    }

    public void send(int id, byte[] bytes, InetAddress clientAddress, int clientPort, byte type, boolean isCritical, boolean isEncrypted) {
        // FIXED: Use MAX_PAYLOAD_SIZE to ensure final datagram size is within MAX_DATAGRAM_SIZE
        List<byte[]> splitPayloads = splitBytes(bytes, MAX_PAYLOAD_SIZE);
        if (splitPayloads.isEmpty()) return;

        List<Packet> packets = isCritical ? new ArrayList<>() : null;

        for (int i = 0; i < splitPayloads.size(); i++) {
            byte[] payload = splitPayloads.get(i);
            int nonce = ThreadLocalRandom.current().nextInt();

            // Use PROTOCOL_HEADER_SIZE
            ByteBuffer buffer = ByteBuffer.allocate(PROTOCOL_HEADER_SIZE + payload.length);
            buffer.putInt(id);                              // ID (int)
            buffer.putInt(nonce);                           // Nonce (int)
            buffer.putShort((short) i);                     // Index (short)
            buffer.putShort((short) splitPayloads.size());  // Max (Total) (short)
            buffer.put(type);                               // Type (byte)
            buffer.put(payload);

            byte[] fullPacket = buffer.array();

            // Construct the internal Packet object
            Packet sendPacket = new Packet(id, nonce, (short) i, (short) splitPayloads.size(), type, payload, clientAddress, clientPort);
            if (isCritical) packets.add(sendPacket);
            System.out.println(sendPacket);

            byte[] finalPayload;

            // --- Encryption Logic ---
            if (isEncrypted) {
                byte[] encrypted = encrypt(sendPacket);
                finalPayload = ByteBuffer.allocate(1 + encrypted.length)
                        .put((byte) 1) // encryption flag
                        .put(encrypted)
                        .array();
            } else {
                finalPayload = ByteBuffer.allocate(1 + fullPacket.length)
                        .put((byte) 0) // no encryption
                        .put(fullPacket)
                        .array();
            }

            DatagramPacket datagramPacket = new DatagramPacket(finalPayload, finalPayload.length, clientAddress, clientPort);

            try {
                socket.send(datagramPacket);
            } catch (IOException e) {
                eventBus.post(new NetworkFailure(e));
            }
        }

        if (isCritical) {
            packetSent.put(id, packets);
        }
    }

    public Packet read(byte[] packet, InetAddress address, int port) throws IOException {
        if (packet == null || packet.length < 1) {
            throw new IOException("Packet too short or null.");
        }

        byte encryptionFlag = packet[0];
        byte[] decrypted = Arrays.copyOfRange(packet, 1, packet.length);

        if (encryptionFlag == 1) {
            System.out.println("Decrypting");
            decrypted = decrypt(decrypted,address,port);

            // FIXED: Throw IOException on failure instead of RuntimeException
            if (decrypted.length < PROTOCOL_HEADER_SIZE) {
                throw new IOException("Decryption failed or resulting packet is too small.");
            }
        } else System.out.println("No Encryption");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(decrypted);
        DataInputStream inStream = new DataInputStream(inputStream);

        int id = inStream.readInt();
        int nonce = inStream.readInt();
        short index = inStream.readShort();
        short total = inStream.readShort();
        byte type = inStream.readByte();

        // Read the remaining payload
        int payloadLength = decrypted.length - PROTOCOL_HEADER_SIZE;
        byte[] payload = new byte[payloadLength];
        inStream.readFully(payload);

        return new Packet(id, nonce, index, total, type, payload, address, port);
    }

    public int getPort() {
        return port;
    }

    public Map<Integer, List<Packet>> getPacketsSent() {
        return packetSent;
    }

    public void removePacketSent(int id) {
        packetSent.remove(id);
    }

    public boolean isClosed() {
        return close;
    }

    /**
     * Sends an ACK for a fully assembled packet (ID).
     * This method is usually called by the main thread after PacketAssembly confirms
     * all fragments have been received.
     * * NOTE: removePacketSent was removed from here because this ACK confirms a
     * received packet, not a sent one. The recipient handles tracking removal.
     * @param id The packet ID to acknowledge.
     * @param address The remote address.
     * @param clientPort The remote port.
     */
    public void sendConfirmation(int id, InetAddress address, int clientPort) {
        send(id, new byte[]{0,0,0,0}, address, clientPort, ACK.getType(), false, false);
    }
    
    public void sendConfirmation(Packet packet) {
        sendConfirmation(packet.id,packet.address,packet.port);
    }

    // FIXED: Renamed port parameter to clientPort for clarity
    public void sendRequest(int id, PacketResendData data, InetAddress address, int clientPort) {
        ByteBuffer buffer = ByteBuffer.allocate(data.index().length * Short.BYTES);

        buffer.putShort((short) data.index().length);
        for (short index : data.index()) {
            buffer.putShort(index);
        }

        byte[] payload = new byte[buffer.position()];
        buffer.flip();
        buffer.get(payload);

        send(id, payload, address, clientPort, NACK.getType(), false, false);
    }

    public void ping(InetAddress address, int clientPort) {
        send("ping",address, clientPort, UNENCRYPTED.getType(), false,false);
    }

    public void sendFailure(int id, String msg, InetAddress address, int clientPort) {
        send(id,msg.getBytes(StandardCharsets.UTF_8), address, clientPort, FAILURE.getType(), false, false);
    }

    public record Packet(int id, int nonce, short index, short total, byte type, byte[] payload, InetAddress address, int port) {
        public byte[] toBytes() {
            ByteBuffer b = ByteBuffer.allocate(MAX_HEADER_SIZE + payload.length);

            b.putInt(id);
            b.putInt(nonce);
            b.putShort(index);
            b.putShort(total);
            b.put(type);
            b.put(payload);

            byte[] packet = new byte[b.position()];
            b.flip();
            b.get(packet);

            return packet;
        }

        @NotNull
        @Override
        public String toString() {
            return "Packet(id: "+id+", Nonce: "+nonce+", Packet: "+(index+1)+"/"+total+", "+type+", Address: "+address.toString()+", Port: "+port;
        }
    }

    protected abstract byte[] encrypt(Packet packet);
    protected abstract byte[] decrypt(byte[] data, java.net.InetAddress address, int port);
    protected abstract boolean preProcessPacket(Packet packet);
}