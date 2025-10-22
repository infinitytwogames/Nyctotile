package org.infinitytwo.umbralore.registry;

import org.infinitytwo.umbralore.entity.Entity;
import org.infinitytwo.umbralore.model.TextureAtlas;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityRegistry {
    private final Map<Integer, Entity> intToEntities = new ConcurrentHashMap<>();
    private static final EntityRegistry registry = new EntityRegistry();
    private final TextureAtlas atlas = new TextureAtlas(50,50);

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public static EntityRegistry getRegistry() {
        return registry;
    }

    public int register(Entity entity) {
        int index = intToEntities.size();
        intToEntities.put(index,entity);
        return index;
    }

    public Entity get(int index) {
        return intToEntities.get(index);
    }

    private EntityRegistry() {}
}
