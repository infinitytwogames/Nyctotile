package org.infinitytwo.umbralore.entity;

import org.joml.Vector2i;

import java.net.InetAddress;
import java.util.UUID;

public class Player {
    public final InetAddress address;
    public Vector2i position;

    public final String name;
    public final UUID id;
    public final String token;

    public final boolean authenticated;

    public Player(InetAddress address, String name, UUID id, String token, Vector2i position, boolean authenticated) {
        this.address = address;
        this.name = name;
        this.id = id;
        this.token = token;
        this.position = position;
        this.authenticated = authenticated;
    }
}
