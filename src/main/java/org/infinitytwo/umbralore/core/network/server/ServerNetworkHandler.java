package org.infinitytwo.umbralore.core.network.server;

import org.infinitytwo.umbralore.core.ServerThread;
import org.infinitytwo.umbralore.core.event.bus.LocalEventBus;
import org.infinitytwo.umbralore.core.network.NetworkHandler;

import java.net.InetAddress;

public class ServerNetworkHandler extends NetworkHandler {
    public ServerNetworkHandler(LocalEventBus eventBus, ServerNetworkThread networkThread, ServerThread serverThread) {
        super(eventBus,networkThread);
    }

    @Override
    public boolean send(String msg, InetAddress address, int port) {
        return false;
    }
}
