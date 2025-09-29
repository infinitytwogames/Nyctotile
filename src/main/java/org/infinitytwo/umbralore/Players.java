package org.infinitytwo.umbralore;

import org.infinitytwo.umbralore.entity.Player;

import java.net.InetAddress;
import java.util.*;

public final class Players {
    private static final List<Player> players = Collections.synchronizedList(new ArrayList<>());

    public static Player getPlayerByAddress(InetAddress address) {
        for (Player player : players) {
            if (player.address.equals(address)) return player;
        }
        return null;
    }

    public static Player getPlayerById(UUID id) {
        for (Player player : players) {
            if (Objects.equals(player.id, id)) return player;
        }
        return null;
    }

    public static void join(Player player) {
        players.add(player);
    }
}
