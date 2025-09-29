package org.infinitytwo.umbralore.context;

import java.net.InetAddress;
import org.infinitytwo.umbralore.constants.LogicalSide;
import org.infinitytwo.umbralore.constants.PhysicalSide;
import org.infinitytwo.umbralore.network.NetworkThread;

public final class NetworkContext extends Context {
    private InetAddress address;
    private byte[] data;
    private byte type;
    private NetworkThread networkThread;
    private int port;

    private NetworkContext(LogicalSide logicalSide, PhysicalSide physicalSide) {
        super(logicalSide, physicalSide);
    }

    public static NetworkContext of(InetAddress address, int port, byte[] data, byte type, PhysicalSide physicalSide, NetworkThread thread) {
        NetworkContext c = new NetworkContext(thread.logicalSide,physicalSide);
        c.data = data;
        c.address = address;
        c.type = type;
        c.networkThread = thread;
        c.port = port;
        return c;
    }

    public InetAddress getAddress() {
        return address;
    }

    public byte[] getData() {
        return data;
    }

    public byte getType() {
        return type;
    }

    public NetworkThread getNetworkThread() {
        return networkThread;
    }

    public int getPort() {
        return port;
    }
}
