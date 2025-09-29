package org.infinitytwo.umbralore.event.network;

import org.infinitytwo.umbralore.event.Event;
import org.infinitytwo.umbralore.network.NetworkThread;

import java.net.InetAddress;

public class UntrustedClientDetected extends Event {
    public final InetAddress address;
    public final NetworkThread.Packet msg;
    public final String reason;

    public UntrustedClientDetected(InetAddress address, NetworkThread.Packet msg, String reason) {
        this.address = address;
        this.msg = msg;
        this.reason = reason;
    }
}
