package org.infinitytwo.umbralore.core.data;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerData {
    public final InetAddress address;

    public final String name;
    public final UUID id;
    public final String token;

    public final boolean authenticated;

    public PlayerData(InetAddress address, int port, String name, UUID id, String token, boolean authenticated) {
        this.address = address;
        this.name = name;
        this.id = id;
        this.token = token;
        this.authenticated = authenticated;
    }

    public static PlayerData shell(String name) {
        return new PlayerData(null, 0, name,UUID.randomUUID(),"",false);
    }
}
