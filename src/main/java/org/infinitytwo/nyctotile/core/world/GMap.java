package org.infinitytwo.nyctotile.core.world;

import org.infinitytwo.nyctotile.core.data.BlockType;
import org.infinitytwo.nyctotile.core.data.world.Block;
import org.infinitytwo.nyctotile.core.data.world.ChunkData;
import org.infinitytwo.nyctotile.core.data.world.ChunkPos;
import org.infinitytwo.nyctotile.core.data.RaycastResult;
import org.infinitytwo.nyctotile.core.data.io.BlockDataReader;
import org.infinitytwo.nyctotile.core.exception.IllegalChunkAccessException;
import org.infinitytwo.nyctotile.core.exception.IllegalDataTypeException;
import org.infinitytwo.nyctotile.core.manager.LightingEngine;
import org.infinitytwo.nyctotile.core.registry.BlockRegistry;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static org.infinitytwo.nyctotile.core.data.world.ChunkData.*;

public abstract class GMap {
    protected LightingEngine lightingEngine = new LightingEngine(this);
    
    public static Vector2i convertToChunkPosition(Vector3i block) {
        return new Vector2i(
                Math.floorDiv(block.x, SIZE_X),
                Math.floorDiv(block.z, SIZE_Z)
        );
    }
    
    public static Vector2i convertToChunkPosition(int x, int z) {
        return new Vector2i(
                Math.floorDiv(x, SIZE_X),
                Math.floorDiv(z, SIZE_Z)
        );
    }
    
    public static Vector3i convertToLocalChunk(int worldX, int worldY, int worldZ) {
        int localX = Math.floorMod(worldX, SIZE_X);
        int localZ = Math.floorMod(worldZ, SIZE_Z);
        return new Vector3i(localX, worldY, localZ);
    }
    
    public static Vector2i worldToChunk(int worldX, int worldZ) {
        int chunkX = Math.floorDiv(worldX, SIZE_X);
        int chunkZ = Math.floorDiv(worldZ, SIZE_Z);
        return new Vector2i(chunkX, chunkZ);
    }
    
    public static ChunkPos worldToChunkPos(int worldX, int worldZ) {
        int chunkX = Math.floorDiv(worldX, SIZE_X);
        int chunkZ = Math.floorDiv(worldZ, SIZE_Z);
        return new ChunkPos(chunkX, chunkZ);
    }
    
    public static Vector3i convertToWorldPosition(Vector2i chunk, Vector3i localChunkPos) {
        return localChunkPos.add((chunk.x * SIZE_X),0,(chunk.y * SIZE_Z));
    }
    
    public static Vector3i convertToWorldPosition(int wx, int wy, int wz, int x, int y, int z) {
        return new Vector3i(wx,wy,wz).add((x * SIZE_X),(y * SIZE_Y),(z * SIZE_Z));
    }
    
    public static Vector3i convertToWorldPosition(Vector2i position, int x, int y, int z) {
        return convertToWorldPosition(x,y,z,position.x,y,position.y);
    }
    
    public static Vector3i convertToLocalChunk(Vector3i blockPos) {
        return convertToLocalChunk(blockPos.x, blockPos.y, blockPos.z);
    }
    
    public void removeBlock(Vector3i pos) throws IllegalChunkAccessException {
        removeBlock(pos.x,pos.y,pos.z);
    }
    
    public Block getTopBlock(int x, int z) {
        int y = getTopBlockPosition(x,z);
        return getBlock(x,y,z);
    }
    
    public int getTopBlockPosition(int x, int z) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk != null) {
            for (int y = 0; y < ChunkData.SIZE_Y; y++) {
                if (chunk.getBlockId(x, y, z) > 0) {
                    return y;
                }
            }
        }
        return 0;
    }
    
    public Block getBlock(int x, int y, int z) throws IllegalChunkAccessException {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        Vector3i pos = convertToLocalChunk(x, y, z);
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        
        int id = chunk.getBlockId(pos);
        if (id <= 0) return null;
        
        Block block = new Block(getBlockRegistry().get(id));
        block.setPosition(x,y,z);
        block.setLight(chunk.getLight(pos));
        return block;
    }
    
    public void removeBlock(int x, int y, int z) throws IllegalChunkAccessException {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        Vector3i pos = convertToLocalChunk(x, y, z);
        chunk.setBlock(pos,0);
    }
    
    public void setBlock(Block block) throws IllegalChunkAccessException {
        Vector3i pos = block.getPosition();
        ChunkData chunk = getChunk(worldToChunkPos(pos.x,pos.z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        Vector3i localChunk = convertToLocalChunk(pos);
        chunk.setBlock(localChunk,getBlockRegistry().getId(block.getId()));
        
        if (block.getType().getLightSource().level() > 0) lightingEngine.addLightSource(block.getPosition(),block.getLight());
        lightingEngine.update();
    }
    
    public Object getData(Vector3i pos, BlockDataReader reader, String name) throws IllegalChunkAccessException, IllegalDataTypeException, NullPointerException {
        ChunkData chunk = getChunk(worldToChunkPos(pos.x, pos.z));
        BlockRegistry registry = getBlockRegistry();
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        Vector3i position = convertToLocalChunk(pos);
        
        BlockDataReader.Data data = reader.getData(
                registry.getId(registry.get(chunk.getBlockId(position)).getId()),
                chunk.getData(position),
                name
        );
        if (data == null) throw new IllegalArgumentException("Cannot get a value from the field: \""+name+"\"");
        
        return data.value;
    }
    
    public void setData(Vector3i pos, byte[] data) throws IllegalChunkAccessException {
        setData(pos.x,pos.y, pos.z, data);
    }
    
    public byte[] getData(Vector3i pos) throws IllegalChunkAccessException {
        return getData(pos.x,pos.y, pos.z);
    }
    
    public byte[] getData(int x, int y, int z)  {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        return chunk.getData(convertToLocalChunk(x,y,z));
    }
    
    public void setData(int x, int y, int z, byte[] data) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setData(convertToLocalChunk(x,y,z),data);
    }
    
    public void setLight(int x, int y, int z, int r, int g, int b, int level) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setLight(convertToLocalChunk(x,y,z),r,g,b,level);
    }
    
    public void setRed(int x, int y, int z, int red) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setRed(convertToLocalChunk(x,y,z),red);
    }
    
    public void setGreen(int x, int y, int z, int green) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setGreen(convertToLocalChunk(x,y,z),green);
    }
    
    public void setBlue(int x, int y, int z, int blue) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setBlue(convertToLocalChunk(x,y,z),blue);
    }
    
    public void setLightLevel(int x, int y, int z, int level) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        chunk.setLightLevel(convertToLocalChunk(x,y,z),level);
    }
    
    public int getRed(int x, int y, int z) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        return chunk.getRed(convertToLocalChunk(x,y,z));
    }
    
    public int getGreen(int x, int y, int z) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        return chunk.getGreen(convertToLocalChunk(x,y,z));
    }
    
    public int getBlue(int x, int y, int z) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        return chunk.getBlue(convertToLocalChunk(x,y,z));
    }
    
    public int getLightLevel(int x, int y, int z) {
        ChunkData chunk = getChunk(worldToChunkPos(x,z));
        
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        return chunk.getLightLevel(convertToLocalChunk(x,y,z));
    }
    
    public static List<ChunkPos> getSurroundingChunks(ChunkPos center, int radius) {
        List<ChunkPos> result = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                result.add(new ChunkPos(center.x() + dx, center.z() + dz));
            }
        }
        return result;
    }
    
    public BlockType getBlockType(Vector3i pos) {
        ChunkData chunk = getChunk(worldToChunkPos(pos.x, pos.z));
        if (chunk == null) throw new IllegalChunkAccessException("Cannot access a non-existing chunk.");
        
        return getBlockRegistry().get(chunk.getBlockId(convertToLocalChunk(pos)));
    }
    
    public RaycastResult raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        Vector3f dir = new Vector3f(direction).normalize(); // Normalized direction
        final float EPSILON = 1e-6f;
        
        Vector3f pos = new Vector3f(origin);
        Vector3i blockPos = new Vector3i((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
        Vector3f step = new Vector3f(Math.signum(dir.x), Math.signum(dir.y), Math.signum(dir.z));
        
        Vector3f tDelta = new Vector3f(
                Math.abs(dir.x) > EPSILON? Math.abs(1.0f / dir.x) : Float.POSITIVE_INFINITY,
                Math.abs(dir.y) > EPSILON? Math.abs(1.0f / dir.y) : Float.POSITIVE_INFINITY,
                Math.abs(dir.z) > EPSILON? Math.abs(1.0f / dir.z) : Float.POSITIVE_INFINITY
        );
        
        Vector3f tMax = new Vector3f(
                dir.x > 0? ((blockPos.x + 1) - pos.x) * tDelta.x : (pos.x - blockPos.x) * tDelta.x,
                dir.y > 0? ((blockPos.y + 1) - pos.y) * tDelta.y : (pos.y - blockPos.y) * tDelta.y,
                dir.z > 0? ((blockPos.z + 1) - pos.z) * tDelta.z : (pos.z - blockPos.z) * tDelta.z
        );
        
        float distance = 0.0f;
        Vector3i normal = new Vector3i();
        
        while (distance <= maxDistance) {
            Block block = getBlock(blockPos.x, blockPos.y, blockPos.z);
            
            if (block != null) {
                return new RaycastResult(new Vector3i(blockPos), new Vector3i(normal));
            }
            
            if (tMax.x < tMax.y && tMax.x < tMax.z) {
                blockPos.x += (int) step.x;
                distance = tMax.x;
                tMax.x += tDelta.x;
                normal.set((int) -step.x, 0, 0);
            } else if (tMax.y < tMax.z) {
                blockPos.y += (int) step.y;
                distance = tMax.y;
                tMax.y += tDelta.y;
                normal.set(0, (int) -step.y, 0);
            } else {
                blockPos.z += (int) step.z;
                distance = tMax.z;
                tMax.z += tDelta.z;
                normal.set(0, 0, (int) -step.z);
            }
        }
        
        return null;
    }
    
    public boolean isBlockLoaded(int x, int y, int z) {
        return getChunk(worldToChunkPos(x,z)) != null;
    }
    
    public boolean isTransparent(int x, int y, int z) {
        Block block = getBlock(x, y, z);
        if (block == null) return true;
        return block.isInvisible();
    }
    
    public List<ChunkPos> getMissingSurroundingChunks(int x, int z, int radius) {
        List<ChunkPos> result = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos p = new ChunkPos(x + dx, z + dz);
                if (getChunk(p) != null) result.add(p);
            }
        }
        return result;
    }
    
    public ChunkData getChunk(Vector2i pos) {
        return getChunk(new ChunkPos(pos));
    }
    
    public abstract BlockRegistry getBlockRegistry();
    public abstract ChunkData getChunk(ChunkPos pos);
    public abstract List<ChunkData> getAllChunks();
}
