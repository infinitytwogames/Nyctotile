package org.infinitytwo.umbralore.event.network;

import org.infinitytwo.umbralore.event.Event;
import org.infinitytwo.umbralore.network.NetworkThread;

import java.net.InetAddress;

public class PacketReceived extends Event {
    public final NetworkThread.Packet packet;
    public final InetAddress address;

    public PacketReceived(NetworkThread.Packet message, InetAddress address) {
        this.packet = message;
        this.address = address;
    }
}
