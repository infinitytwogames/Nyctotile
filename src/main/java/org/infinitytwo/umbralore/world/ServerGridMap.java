package org.infinitytwo.umbralore.world;

import org.infinitytwo.umbralore.block.Block;
import org.infinitytwo.umbralore.block.BlockType;
import org.infinitytwo.umbralore.data.ChunkData;
import org.infinitytwo.umbralore.exception.IllegalChunkAccessExecption;
import org.infinitytwo.umbralore.exception.IllegalDataTypeException;
import org.infinitytwo.umbralore.registry.BlockDataReader;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.infinitytwo.umbralore.data.ChunkData.SIZE_X;
import static org.infinitytwo.umbralore.data.ChunkData.SIZE_Z;

public class ServerGridMap {
    protected final ConcurrentHashMap<ChunkPos, ChunkData> chunks = new ConcurrentHashMap<>();
    protected boolean isReady = false;
    protected final BlockRegistry registry;

    public ServerGridMap(BlockRegistry registry) {
        this.registry = registry;
    }

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

    public Block getBlock(int x, int y, int z) {
        Vector2i p = convertToChunkPosition(x,z);
        ChunkPos pos = new ChunkPos(p.x, p.y);

        if (chunks.containsKey(pos)) {
            int id = chunks.get(pos).getBlockId(x,y,z);
            if (id != 0) return new Block(registry.get(id));
        } else return null;
        return null;
    }

    public void removeBlock(int x, int y, int z) throws IllegalChunkAccessExecption {
        Vector2i p = convertToChunkPosition(x,z);
        ChunkPos pos = new ChunkPos(p.x, p.y);

        if (chunks.containsKey(pos)) {
            chunks.get(pos).setBlock(convertToLocalChunk(x,y,z), (short) 0);
        } else throw new IllegalChunkAccessExecption("Cannot modify a non-existing chunk. "+p);
    }

    public void removeBlock(Vector3i pos) throws IllegalChunkAccessExecption {
        removeBlock(pos.x,pos.y, pos.z);
    }

    public void addChunk(ChunkData chunk) {
        chunks.put(new ChunkPos(chunk.getPosition().x,chunk.getPosition().y),chunk);
    }

    public List<ChunkPos> getSurroundingChunks(ChunkPos center, int radius) {
        List<ChunkPos> result = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                result.add(new ChunkPos(center.x + dx, center.z + dz));
            }
        }
        return result;
    }

    public void insertData(Vector3i pos, byte[] data) throws IllegalChunkAccessExecption {
        chunks.get(worldToChunkPos(pos.x, pos.z)).setData(pos, data);
    }

    public byte[] getData(Vector3i pos) throws IllegalChunkAccessExecption {
        return chunks.get(worldToChunkPos(pos.x, pos.z)).getData(pos);
    }

    public Object getData(Vector3i pos, BlockDataReader reader, String name) throws IllegalChunkAccessExecption, IllegalDataTypeException {
        ChunkData chunk = chunks.get(worldToChunkPos(pos.x, pos.z));
        return reader.getData(chunk.getBlockId(pos.x,pos.y,pos.z), chunk.getData(pos), name);
    }

    public BlockType getBlockType(Vector3i pos) {
        return getBlock(pos.x,pos.y,pos.z).getType();
    }

    public boolean isReady() {
        return isReady;
    }

    public RaycastResult raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        Vector3f dir = new Vector3f(direction).normalize(); // Normalized direction
        final float EPSILON = 1e-6f;

        Vector3f pos = new Vector3f(origin);
        Vector3i blockPos = new Vector3i((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
        Vector3f step = new Vector3f(Math.signum(dir.x), Math.signum(dir.y), Math.signum(dir.z));

        Vector3f tDelta = new Vector3f(
                Math.abs(dir.x) > EPSILON ? Math.abs(1.0f / dir.x) : Float.POSITIVE_INFINITY,
                Math.abs(dir.y) > EPSILON ? Math.abs(1.0f / dir.y) : Float.POSITIVE_INFINITY,
                Math.abs(dir.z) > EPSILON ? Math.abs(1.0f / dir.z) : Float.POSITIVE_INFINITY
        );

        Vector3f tMax = new Vector3f(
                dir.x > 0 ? ((blockPos.x + 1) - pos.x) * tDelta.x : (pos.x - blockPos.x) * tDelta.x,
                dir.y > 0 ? ((blockPos.y + 1) - pos.y) * tDelta.y : (pos.y - blockPos.y) * tDelta.y,
                dir.z > 0 ? ((blockPos.z + 1) - pos.z) * tDelta.z : (pos.z - blockPos.z) * tDelta.z
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

    public void setBlock(Block block) throws IllegalChunkAccessExecption {
        Vector3i blockPos = block.getPosition();
        Vector2i p = convertToChunkPosition(blockPos);
        ChunkPos pos = new ChunkPos(p.x, p.y);

        if (chunks.containsKey(pos)) {
            chunks.get(pos).setBlock(convertToLocalChunk(blockPos), registry.getId(block.getType().getId()));
        } else throw new IllegalChunkAccessExecption("Cannot modify a non-existing chunk. "+p);
    }

    private Vector3i convertToLocalChunk(Vector3i blockPos) {
        return convertToLocalChunk(blockPos.x, blockPos.y, blockPos.z);
    }

    public ChunkData getChunk(Vector2i pos) throws IllegalChunkAccessExecption {
        ChunkData data = chunks.get(new ChunkPos(pos.x, pos.y));
        if (data == null) throw new IllegalChunkAccessExecption("Cannot access a non-existing chunk at (" + pos.x +", "+pos.y+")");
        return data;
    }

    public record RaycastResult(Vector3i blockPos, Vector3i hitNormal){}
    public record ChunkPos(int x, int z) { }
}
