package org.infinitytwo.nyctotile.core.world;

import org.infinitytwo.nyctotile.core.Window;
import org.infinitytwo.nyctotile.core.data.world.ChunkData;
import org.infinitytwo.nyctotile.core.data.world.ChunkPos;
import org.infinitytwo.nyctotile.core.manager.World;
import org.infinitytwo.nyctotile.core.registry.BlockRegistry;
import org.infinitytwo.nyctotile.core.renderer.Camera;
import org.infinitytwo.nyctotile.core.renderer.Chunk;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.infinitytwo.nyctotile.core.data.world.ChunkData.SIZE_X;
import static org.infinitytwo.nyctotile.core.data.world.ChunkData.SIZE_Y;
import static org.infinitytwo.nyctotile.core.renderer.Chunk.SIZE_Z;

public class GridMap extends GMap {
    protected final ConcurrentHashMap<ChunkPos, Chunk> chunks = new ConcurrentHashMap<>();
    protected final BlockRegistry registry;
    
    public GridMap(BlockRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public BlockRegistry getBlockRegistry() {
        return registry;
    }
    
    @Override
    public Chunk getChunk(ChunkPos pos) {
        return chunks.get(pos);
    }
    
    public void addChunk(Chunk chunk) {
        ChunkPos pos = new ChunkPos(chunk.getPosition().x, chunk.getPosition().y);
        chunks.put(pos, chunk);
        
        for (ChunkPos neighbour : getSurroundingChunks(pos, 1)) {
            if (chunks.containsKey(neighbour)) chunks.get(neighbour).rebuild();
        }
        
        for (int x = 0; x < SIZE_X; x++) {
            for (int z = 0; z < SIZE_Z; z++) {
                int y = getTopBlockPosition(x, z);
                chunk.setLight(x, y, z, World.getAmbience());
            }
        }
    }
    
    public void rebuildChunk(int x, int z) {
        ChunkPos pos = new ChunkPos(x, z);
        
        if (chunks.containsKey(pos)) {
            chunks.get(pos).dirty();
        }
    }
    
    public void draw(Camera camera, Window window, int view) {
        Vector2i chunkP = convertToChunkPosition((int) camera.getPosition().x, (int) camera.getPosition().z);
        ChunkPos pos = new ChunkPos(chunkP.x, chunkP.y);
        List<ChunkPos> r = getSurroundingChunks(pos, view);
        
        for (ChunkPos search : r) {
            if (chunks.containsKey(search)) {
                Chunk chunk = chunks.get(search);
                chunk.draw(camera, window);
            }
        }
    }
    
    public List<ChunkPos> getMissingSurroundingChunks(ChunkPos center, int radius) {
        return getMissingSurroundingChunks(center.x(), center.z(), radius);
    }
    
    @Override
    public List<ChunkData> getAllChunks() {
        return new ArrayList<>(chunks.values());
    }
    
    public void rebuildChunk(Vector2i pos) {
        rebuildChunk(pos.x, pos.y);
    }
}