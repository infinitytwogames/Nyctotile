package org.infinitytwo.umbralore.core.network;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketAssembly {
    private final Map<Integer, List<NetworkThread.Packet>> packets = new ConcurrentHashMap<>();
    public static final int MAX_UDP_SIZE = 1024;
    public static final int MAX_HEADER_SIZE = Integer.BYTES + (Short.BYTES * 3);

    public void addPacket(int packetId, NetworkThread.Packet data) {
        packets.computeIfAbsent(packetId, i -> new ArrayList<>()).add(data);
    }

    public NetworkThread.Packet getData(int packetId) throws MissingResourceException, NullPointerException {
        if (!packets.containsKey(packetId)) {
            throw new NullPointerException("The packet id does not exist in the registry.");
        }

        List<NetworkThread.Packet> dataList = packets.get(packetId);
        Map<Integer, byte[]> payloads = new TreeMap<>();
        int totalSize = 0;
        int expectedChunks = -1;

        for (NetworkThread.Packet packet : dataList) {
            if (payloads.containsKey((int) packet.index())) continue;
            short index = packet.index();
            short max = packet.total();

            if (expectedChunks == -1) expectedChunks = max;

            byte[] payload = packet.payload();

            payloads.put((int) index, payload);
            totalSize += payload.length;
        }

        // Check if we received all chunks
        if (payloads.size() < expectedChunks) {
            throw new MissingResourceException("Packet ID " + packetId + " is incomplete.",
                    PacketAssembly.class.getName(), String.valueOf(packetId));
        }

        // Reconstruct final payload
        byte[] finalPayload = new byte[totalSize];
        int offset = 0;
        for (byte[] payload : payloads.values()) {
            System.arraycopy(payload, 0, finalPayload, offset, payload.length);
            offset += payload.length;
        }

        NetworkThread.Packet packet = dataList.get(0);
        NetworkThread.Packet newPacket = new NetworkThread.Packet(packetId, packet.nonce(), (short) 0, (short) 0,packet.type(),finalPayload,packet.address(),packet.port());

        discard(packetId);

        return newPacket;
    }

    public void discard(int packetId) {
        packets.remove(packetId);
    }

    public List<Integer> getFilledPacketsId() {
        List<Integer> ids = new ArrayList<>();

        for (Map.Entry<Integer, List<NetworkThread.Packet>> entry : packets.entrySet()) {
            List<NetworkThread.Packet> packetL = entry.getValue();

            if (packetL == null || packetL.isEmpty()) continue;

            short total = packetL.get(0).total();
            if (packetL.size() >= total) {
                ids.add(entry.getKey());
            }
        }

        return ids;
    }

    public void dropAll() {
        packets.clear();
    }

    public PacketResendData getMissingPackets(int id) {
        if (!packets.containsKey(id)) throw new NullPointerException("The packet id does not exist in the registry.");
        List<NetworkThread.Packet> receivedPackets = packets.get(id);
        Set<Short> receivedIndexes = new HashSet<>();

        for (NetworkThread.Packet packet : receivedPackets) {
            receivedIndexes.add(packet.index());
        }

        int total = receivedPackets.get(0).total();
        List<Short> missingIndexes = new ArrayList<>();

        for (short i = 0; i < total; i++) {
            if (!receivedIndexes.contains(i)) {
                missingIndexes.add(i);
            }
        }

        PacketResendData data = null;
        if (!missingIndexes.isEmpty()) {
            short[] d = new short[missingIndexes.size()];
            for (int j = 0; j < d.length; j++) {
                d[j] = missingIndexes.get(j);
            }
            data = new PacketResendData(id,d);
        }

        return data;
    }

    public InetAddress getAddress(int id) {
        if (packets.containsKey(id) && !packets.get(id).isEmpty()) return packets.get(id).get(0).address();
        else return null;
    }

    public boolean exists(int id) {
        return packets.containsKey(id) && !packets.get(id).isEmpty();
    }

    public NetworkThread.Packet getFirstPacket(int id) {
        List<NetworkThread.Packet> p = packets.get(id);
        if (p == null) return null;
        return p.get(0);
    }
}
