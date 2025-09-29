package org.infinitytwo.umbralore.registry;

import org.infinitytwo.umbralore.block.BlockType;
import org.infinitytwo.umbralore.exception.NoSuchElementInRegistryException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class ResourceManager {
    private static final Map<Integer, BlockType> blockTypes = new ConcurrentHashMap<>();

    public static BlockType fromId(int id) throws NoSuchElementInRegistryException {
        BlockType type = blockTypes.getOrDefault(id, null);
        if (type == null) throw new NoSuchElementInRegistryException("BlockType \""+id+"\" was not found in registry.");
        return type;
    }

    public static void register(int id, BlockType type) {
        blockTypes.put(id, type);
    }

    public static void register(BlockRegistry registry) {
        for (int id : registry.getIds()) {
            blockTypes.put(id, registry.get(id));
        }
    }
}
