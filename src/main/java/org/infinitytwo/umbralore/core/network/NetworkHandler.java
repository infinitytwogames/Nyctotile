package org.infinitytwo.umbralore.core.network;

import org.infinitytwo.umbralore.core.event.SubscribeEvent;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.network.PacketReceived;
import org.infinitytwo.umbralore.core.network.NetworkThread.Packet;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;


public class NetworkHandler extends Thread {
    @FunctionalInterface
    public interface CommandProcessor {
        void process(Packet packets, NetworkThread thread);
    }
    
    protected final EventBus eventBus;
    protected final PacketAssembly assembly;
    protected float delta;
    private long lastFrameTime;
    protected float blinkTimer;
    
    // --- TIMEOUT CONSTANTS ---
    protected final int TICK_RATE_MS = 50; // The sleep time (e.g., 50ms)
    protected final float CHECK_INTERVAL_SECONDS = 1.0f; // How often to check timers (1 second)
    // Ticks per check interval (1000ms / 50ms = 20 ticks)
    protected final int CHECK_INTERVAL_TICKS = (int) (CHECK_INTERVAL_SECONDS * 1000 / TICK_RATE_MS);
    
    // The timeout value in CHECK_INTERVAL_CYCLES (3 seconds / 1 second per cycle = 3 cycles).
    protected final int SENDER_TIMEOUT_CYCLES = 3;
    protected final int MAX_RESEND_ATTEMPTS = 5; // Maximum times a critical packet will be re-sent
    
    // Map of Packet ID to Time remaining until resend (Sender side)
    protected final Map<Integer, Integer> packets = new ConcurrentHashMap<>();
    // Map of Packet ID to Time remaining until resend request (Receiver side)
    protected final Map<Integer, Integer> receivedPackets = new ConcurrentHashMap<>();
    
    // --- NEW MAP TO TRACK RESEND COUNTS ---
    protected final Map<Integer, Integer> resendAttempts = new ConcurrentHashMap<>();
    
    protected final NetworkThread networkThread;
    protected final CommandProcessor processor;
    
    public NetworkHandler(EventBus bus, NetworkThread network, CommandProcessor processor) {
        eventBus = bus;
        this.processor = processor;
        eventBus.register(this);
        assembly = new PacketAssembly();
        networkThread = network;
        
        setName("Server Network Handler");
    }
    
    @SubscribeEvent
    public void onMessageReceived(PacketReceived e) {
        System.out.println("Received Packet and attached to PacketAssembly: "+e.packet.toString());
        assembly.addPacket(e.packet.id(), e.packet);
        
        // Only start a timer for INCOMING reliable packets (e.g., those needing assembly)
        // If e.packet.total() > 1, it requires assembly and is reliable.
        if (e.packet.total() > 1 /* || other reliability checks */) {
            
            // Set a timer for the receiver.
            int receiverTimeoutCycles = 1;
            
            // putIfAbsent ensures we only start the timer once for the first fragment
            receivedPackets.putIfAbsent(e.packet.id(), receiverTimeoutCycles * CHECK_INTERVAL_TICKS);
        }
    }
    
    @Deprecated
    public void registerCommand(NetworkCommand cmd) {
        // This method should be removed if unused.
    }
    
    @Override
    public void run() {
        while (!isInterrupted()) {
            long now = System.nanoTime();
            delta = (now - lastFrameTime) / 1_000_000_000.0f; // Convert to seconds
            lastFrameTime = now;
            
            synchronize();
            update();
            
            try {
                // Use the defined constant for tick rate
                Thread.sleep(TICK_RATE_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public void cleanup() {
        assembly.dropAll();
        interrupt();
    }
    
    private void update() {
        blinkTimer += delta;
        
        // We only check and perform actions when the timer interval hits 1 second (or CHECK_INTERVAL_TICKS)
        if (blinkTimer >= CHECK_INTERVAL_SECONDS) {
            blinkTimer = 0;
            
            // --- 1. SENDER RELIABILITY (Outgoing Packets waiting for ACK) ---
            // Decrement by the number of ticks that passed (CHECK_INTERVAL_TICKS)
            packets.replaceAll((id, t) -> t - CHECK_INTERVAL_TICKS);
            
            Map<Integer, List<Packet>> sentPackets = networkThread.getPacketsSent();
            
            packets.entrySet().removeIf(entry -> {
                int id = entry.getKey();
                // If the packet ID is no longer being tracked by NetworkThread (i.e., ACK received), remove it.
                if (!sentPackets.containsKey(id)) {
                    resendAttempts.remove(id); // Clean up attempts map
                    return true;
                }
                
                // If timer has expired, check attempts
                if (entry.getValue() <= 0) {
                    
                    // --- ATTEMPTS CHECK ---
                    int attempts = resendAttempts.compute(id, (k, v) -> v == null ? 1 : v + 1);
                    
                    if (attempts > MAX_RESEND_ATTEMPTS) {
                        System.err.println("CRITICAL FAILURE: Packet ID " + id + " failed after " + MAX_RESEND_ATTEMPTS + " attempts without ACK. Dropping packet.");
                        networkThread.getPacketsSent().remove(id); // Stop tracking and sending
                        resendAttempts.remove(id);
                        return true; // Remove entry from packets map (stop tracking)
                    }
                    
                    // --- FIX APPLIED: Reset timer and DO NOT RESEND ---
                    System.out.println("Sender Timeout: Waiting for receiver request for ID " + id + " (Attempt " + attempts + "/" + MAX_RESEND_ATTEMPTS + ")");
                    
                    // Reset the timer to the full timeout value to check again later
                    packets.put(id, SENDER_TIMEOUT_CYCLES * CHECK_INTERVAL_TICKS);
                    
                    // Crucial: The entry must NOT be removed yet.
                    return false; // Timer still running (but reset)
                }
                return false; // Timer still running
            });
            
            
            // --- 2. RECEIVER RELIABILITY (Incoming Packets needing Assembly) ---
            // Decrement by the number of ticks that passed (CHECK_INTERVAL_TICKS)
            receivedPackets.replaceAll((id, t) -> t - CHECK_INTERVAL_TICKS);
            
            receivedPackets.entrySet().removeIf((entry) -> {
                if (entry.getValue() <= 0) {
                    int id = entry.getKey();
                    // CRITICAL FIX: Only check for missing packets if assembly still exists
                    if (!assembly.exists(id)) return true;
                    
                    PacketResendData data = assembly.getMissingPackets(id);
                    
                    // If data is null, it means all indexes are present (assembly is complete or ready to be processed)
                    if (data == null) {
                        // The packet is complete, but the timer ran out. Process now, but keep checking until synchronize
                        // actually processes it to avoid a resend loop on complete packets.
                        // However, since we are only processing complete packets in synchronize(),
                        // we can let it go here if the only logic is resending missing data.
                        return true;
                    }
                    
                    // --- ATTEMPTS CHECK FOR RECEIVER ---
                    int attempts = resendAttempts.compute(id, (k, v) -> v == null ? 1 : v + 1);
                    
                    if (attempts > MAX_RESEND_ATTEMPTS) {
                        System.err.println("CRITICAL FAILURE: Incoming packet ID " + id + " is incomplete after " + MAX_RESEND_ATTEMPTS + " request attempts. Discarding.");
                        assembly.discard(id); // Remove incomplete assembly
                        resendAttempts.remove(id);
                        // NOTE: Consider firing an event here to disconnect the peer.
                        return true; // Remove entry from receivedPackets map (stop tracking)
                    }
                    
                    NetworkThread.Packet firstPacket = assembly.getFirstPacket(id);
                    InetAddress address = assembly.getAddress(id);
                    
                    if (address != null && firstPacket != null) {
                        // Request missing fragments from the sender (sends NACK)
                        System.out.println("Receiver Timeout: Requesting missing fragments for ID " + id + " (Attempt " + attempts + "/" + MAX_RESEND_ATTEMPTS + ")");
                        networkThread.sendRequest(id, data, address, firstPacket.port());
                        
                        // Reset timer to wait for requested fragments (using the sender timeout for consistency)
                        entry.setValue(SENDER_TIMEOUT_CYCLES * CHECK_INTERVAL_TICKS);
                        return false; // Do not remove entry; keep monitoring assembly
                    }
                    resendAttempts.remove(id); // Clean up
                    return true; // Cannot send NACK if address/port info is missing, remove tracking
                } else return false;
            });
        }
    }
    
    private void synchronize() {
        // --- 1. Receiver ACK/Confirmation ---
        List<Integer> packetsReady = assembly.getFilledPacketsId();
        
        for (int id : packetsReady) {
            InetAddress address = assembly.getAddress(id);
            // CRITICAL FIX: Check for null on both address and firstPacket before use
            NetworkThread.Packet firstPacket = assembly.getFirstPacket(id);
            
            if (address != null && firstPacket != null) {
                // Send confirmation (ACK) back to the sender
                networkThread.sendConfirmation(id, address, firstPacket.port());
                
                // Immediately stop tracking and clean up attempts map for this incoming packet
                receivedPackets.remove(id);
                resendAttempts.remove(id);
                
                handle(assembly.getData(id));
                // assembly.discard(id) is called inside assembly.getData(id)
            } else {
                // If we can't get address/port, we can't send an ACK. Discard the assembly.
                assembly.discard(id);
            }
        }
        
        // --- 2. Sender Timeout Tracking Initialization ---
        Map<Integer, List<NetworkThread.Packet>> sentPackets = networkThread.getPacketsSent();
        
        for (int id : sentPackets.keySet()) {
            // Start the timer for any newly sent reliable packet
            // If it already exists, don't overwrite the current countdown
            // Use the full timeout value for the sender
            packets.putIfAbsent(id, SENDER_TIMEOUT_CYCLES * CHECK_INTERVAL_TICKS);
            // Initialize attempt count (resendAttempts map is implicitly set/updated in 'update()')
        }
        
        // Cleanup map from any packets that have been ACKed since last update()
        packets.entrySet().removeIf(entry -> !sentPackets.containsKey(entry.getKey()));
        // Note: resendAttempts cleanup is handled either when ACK is received/checked
        // in update() or when the packet is successfully processed in synchronize().
    }
    
    public byte[] handle(Packet packets) {
        try {
            processor.process(packets,networkThread);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    public NetworkThread.Packet getData(int id) throws MissingResourceException, NullPointerException {
        // The packet should only be requested for data *after* all fragments are assembled.
        return assembly.getData(id);
    }
}