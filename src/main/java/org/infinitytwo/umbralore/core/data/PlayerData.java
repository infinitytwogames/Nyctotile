package org.infinitytwo.umbralore.core.data;

import com.esotericsoftware.kryonet.Connection;

import java.net.InetAddress;
import java.util.UUID;

public record PlayerData(String name, String id, String token, boolean authenticated, Connection connection) {
    
    public static PlayerData shell(String name) {
        return new PlayerData(name, "", "", false, null);
    }
}
