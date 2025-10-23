package org.infinitytwo.umbralore.core.network;

import org.infinitytwo.umbralore.core.constants.LogicalSide;
import org.infinitytwo.umbralore.core.event.bus.LocalEventBus;
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
    protected final int port;
    protected final LocalEventBus eventBus;
    protected DatagramSocket socket;
    protected Map<Integer, List<Packet>> packetSent = new ConcurrentHashMap<>();
    protected volatile boolean close;
    public static final int MAX_HEADER_SIZE = (Integer.BYTES * 2) + (Short.BYTES * 2) + 1;
    public static final int MAX_UDP_SIZE = 1024;
    public final Map<Integer, Integer> nonceDuplicate = new ConcurrentHashMap<>();

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

    public NetworkThread(LogicalSide side, LocalEventBus eventBus, int port) {
        this.logicalSide = side;
        this.eventBus = eventBus;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.socket = new DatagramSocket(port);
            byte[] buffer = new byte[1520];

            while (!close) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());
                Packet parsedPacket = read(receivedData, packet.getAddress(), packet.getPort());
                if (preProcessPacket(parsedPacket) && evaluate(parsedPacket)) eventBus.post(new PacketReceived(parsedPacket, packet.getAddress()));
            }
        } catch (Exception e) {
            if (!close) eventBus.post(new NetworkFailure(e));
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private boolean evaluate(Packet packet) {
        int id = packet.id;
        int nonce = packet.nonce;

        List<Packet> sentPackets = packetSent.get(id);
        if (sentPackets != null) {
            boolean duplicateFound = sentPackets.stream().anyMatch(p -> p.nonce == nonce);
            if (duplicateFound) {
                nonceDuplicate.merge(id, 1, Integer::sum);
                if (nonceDuplicate.get(id) >= 10) {
                    send(id, new byte[0], packet.address, packet.port, NACK.getType(), false, false);
                    nonceDuplicate.remove(id);
                } else sendConfirmation(id,packet.address, packet.port); // LOL!
                return false;
            }
        }

        if (packetSent.containsKey(id) &&
                packet.type == CMD_BYTE_DATA.getType()
        ) {
            sendConfirmation(id,packet.address, packet.port);
        } else if (packet.type == NACK.getType()) { // Resend Code
            short index = packet.index;
            List<Packet> list = packetSent.get(id);
            if (list != null && index >= 0 && index < list.size()) {
                send(list.get(index), true);
            }

            return false;
        } else if (packet.type == ACK.getType()) { // Received Successfully

            if (!packetSent.containsKey(id)) return true;

            packetSent.remove(id);
        } else if (packet.type == COMMAND.getType()) {
            byte[] payload = packet.payload;

            String command = new String(payload, StandardCharsets.UTF_8);
            if (command.split(" ")[0].equals("ping")) {
                pong(packet.address, packet.port);
            }
            return false;
        }
        return true;
    }

    public void pong(InetAddress address, int port) {
        send("pong", address, port, UNENCRYPTED.getType(), false, false);
    }

    public void send(Packet packet, boolean isCritical) {
        send(packet.payload, packet.address, packet.port, packet.type, isCritical,true);
    }

    public void shutdown() {
        close = true;
        if (socket != null) {
            socket.close();
        }
        interrupt();
    }

    public void send(@NotNull String msg, InetAddress clientAddress, int port, boolean isCritical, boolean encrypted) {
        send(msg.getBytes(StandardCharsets.UTF_8), clientAddress, port, COMMAND.getType(), isCritical, encrypted);
    }

    public void send(byte[] bytes, InetAddress address, int port, byte type, boolean isCritical, boolean encrypted) {
        send(ThreadLocalRandom.current().nextInt(), bytes, address, port, type, isCritical, encrypted);
    }

    public void send(String msg, InetAddress address, int port, byte type, boolean isCritical, boolean encrypted) {
        send(ThreadLocalRandom.current().nextInt(), msg.getBytes(StandardCharsets.UTF_8), address, port, type, isCritical, encrypted);
    }

    public void send(int id, byte[] bytes, InetAddress clientAddress, int port, byte type, boolean isCritical, boolean isEncrypted) {
        List<byte[]> splitPayloads = splitBytes(bytes, MAX_UDP_SIZE);
        if (splitPayloads.isEmpty()) return;

        List<Packet> packets = isCritical ? new ArrayList<>() : null;

        for (int i = 0; i < splitPayloads.size(); i++) {
            byte[] payload = splitPayloads.get(i);
            int nonce = ThreadLocalRandom.current().nextInt();

            ByteBuffer buffer = ByteBuffer.allocate(MAX_HEADER_SIZE + payload.length);
            buffer.putInt(id);                              // ID
            buffer.putInt(nonce);                           // Nonce
            buffer.putShort((short) i);                     // Index
            buffer.putShort((short) splitPayloads.size());  // Max
            buffer.put(type);                               // Type
            buffer.put(payload);

            System.out.println("Payload length: " + payload.length);
            System.out.println("Payload bytes: " + Arrays.toString(payload));
            System.out.println(new String(payload,StandardCharsets.UTF_8));

            byte[] fullPacket = buffer.array();

            // Construct the internal Packet object
            Packet sendPacket = new Packet(id, nonce, (short) i, (short) splitPayloads.size(), type, payload, clientAddress, port);
            if (isCritical) packets.add(sendPacket);

            byte[] finalPayload;

            if (type != UNENCRYPTED.getType() || !isEncrypted) {
                byte[] encrypted = encrypt(sendPacket); // Encrypt full packet contents
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

            DatagramPacket datagramPacket = new DatagramPacket(finalPayload, finalPayload.length, clientAddress, port); //<------ "port" refers to this.port 💀

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
        byte[] decrypted = packet;

        if (encryptionFlag == 1) {
            decrypted = decrypt(Arrays.copyOfRange(packet, 1, packet.length));
            if (decrypted.length < MAX_HEADER_SIZE) {
                throw new IOException("Decrypted packet is invalid or too small.");
            }
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(decrypted);
        DataInputStream inStream = new DataInputStream(inputStream);

        int id = inStream.readInt();
        int nonce = inStream.readInt();
        short index = inStream.readShort();
        short total = inStream.readShort();
        byte type = inStream.readByte();

        // Read the remaining payload
        int payloadLength = decrypted.length - MAX_HEADER_SIZE;
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

    public void sendConfirmation(int id, InetAddress address, int port) {
        removePacketSent(id);
        send(id, new byte[0], address, port, (byte) 2, false, false);
    }

    public void sendRequest(int id, PacketResendData data, InetAddress address, int port) {
        ByteBuffer buffer = ByteBuffer.allocate(data.index().length * Short.BYTES);

        buffer.putShort((short) data.index().length);
        for (short index : data.index()) {
            buffer.putShort(index);
        }

        byte[] payload = new byte[buffer.position()];
        buffer.flip();
        buffer.get(payload);

        send(id, payload, address, port,(byte) 3, false, false);
    }

    public void ping(InetAddress address, int port) {
        send("ping",address, port, UNENCRYPTED.getType(), false,false);
    }

    public void sendFailure(int id, String msg, InetAddress address, int port) {
        send(id,msg.getBytes(StandardCharsets.UTF_8), address, port, FAILURE.getType(), false, false);
    }

    public record Packet(int id, int nonce, short index, short total, byte type, byte[] payload, InetAddress address, int port) {
        public byte[] toBytes() {
            ByteBuffer b = ByteBuffer.allocate(NetworkThread.MAX_HEADER_SIZE + payload.length);

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
    }

    protected abstract byte[] encrypt(Packet packet);
    protected abstract byte[] decrypt(byte[] data);
    protected abstract boolean preProcessPacket(Packet packet);
}
