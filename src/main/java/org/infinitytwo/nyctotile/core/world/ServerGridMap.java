package org.infinitytwo.nyctotile.core.world;

import org.infinitytwo.nyctotile.core.data.world.ChunkData;
import org.infinitytwo.nyctotile.core.data.world.ChunkPos;
import org.infinitytwo.nyctotile.core.registry.BlockRegistry;
import org.joml.Vector2i;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGridMap extends GMap {
    protected final ConcurrentHashMap<ChunkPos, ChunkData> chunks = new ConcurrentHashMap<>();
    protected final BlockRegistry registry;
    
    public ServerGridMap(BlockRegistry registry) {
        this.registry = registry;
    }
    
    public void addChunk(ChunkData chunk) {
        chunks.put(new ChunkPos(chunk.getPosition().x, chunk.getPosition().y), chunk);
    }
    
    public Collection<ChunkData> getChunks() {
        return Collections.unmodifiableCollection(chunks.values());
    }
    
    public ChunkData getChunk(int x, int y) {
        return getChunk(new Vector2i(x, y));
    }
    
    @Override
    public BlockRegistry getBlockRegistry() {
        return registry;
    }
    
    @Override
    public ChunkData getChunk(ChunkPos pos) {
        return chunks.get(pos);
    }
    
    @Override
    public List<ChunkData> getAllChunks() {
        return chunks.values().stream().toList();
    }
}
