package org.infinitytwo.umbralore.world.dimension;

import org.infinitytwo.umbralore.context.ClientContext;
import org.infinitytwo.umbralore.context.ServerContext;
import org.infinitytwo.umbralore.data.PlayerData;
import org.infinitytwo.umbralore.world.ServerGridMap;
import org.infinitytwo.umbralore.world.ServerProcedureGridMap;
import org.infinitytwo.umbralore.world.generation.Biome;
import org.infinitytwo.umbralore.world.generation.NoiseGenerationSettings;

import java.util.Collections;
import java.util.List;

public abstract class Dimension {
    protected final NoiseGenerationSettings settings;
    protected final String name;
    protected final String id;
    protected final ServerProcedureGridMap world;
    protected final List<PlayerData> playerData;
    protected final Biome[] biomes;

    public Dimension(String name, String id, NoiseGenerationSettings settings, ServerProcedureGridMap world, List<PlayerData> playerData) {
        this.name = name;
        this.id = id;
        this.settings = settings;
        this.world = world;
        this.playerData = playerData;
        this.biomes = settings.biomes;
    }

    public NoiseGenerationSettings getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ServerProcedureGridMap getWorld() {
        return world;
    }

    public List<PlayerData> getPlayers() {
        return Collections.unmodifiableList(playerData);
    }

    @Override
    public String toString() {
        return "Dimension[" + name + " | id=" + id + "]";
    }

    public abstract void generate(ServerGridMap.ChunkPos chunk);
    public abstract void playerEntered(ServerContext context, PlayerData playerData);
    public abstract void playerLeave(PlayerData playerData);
//    public abstract SpawnLocation playerDied(Player player);
    public abstract void tick(ServerContext context);
    public abstract void draw(ClientContext context);

    public void cleanup() {
        world.cleanup();
    }
}
