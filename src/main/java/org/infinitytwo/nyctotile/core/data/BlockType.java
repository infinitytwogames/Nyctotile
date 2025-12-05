package org.infinitytwo.nyctotile.core.data;

import org.infinitytwo.nyctotile.core.data.world.AABB;
import org.infinitytwo.nyctotile.core.data.buffer.NFloatBuffer;
import org.infinitytwo.nyctotile.core.model.TextureAtlas;
import org.infinitytwo.nyctotile.core.registry.BlockRegistry;
import org.infinitytwo.nyctotile.core.registry.Registerable;
import org.infinitytwo.nyctotile.core.world.GridMap;
import org.joml.Vector3i;

public abstract class BlockType implements Registerable {
    protected int textureIndex;
    protected AABB[] hitboxes = {new AABB(0, 0, 0, 1, 1, 1)};
    protected final String material;
    protected final boolean invisible;
    protected final String id;
    protected boolean collidable = true;
    protected float friction = 1;
    protected Light lightSource = new Light();

    public BlockType(String material, boolean invisible, String name, int textureIndex) {
        this.textureIndex = textureIndex;
        this.material = material;
        this.invisible = invisible;
        this.id = name;
    }

    public boolean isTransparent() {
        return invisible;
    }

    public AABB[] getBoundingBox() {
        return hitboxes;
    }

    public String getMaterial() {
        return material;
    }

    public String getName() {
        return id;
    }

    public String getId() {
        return id;
    }

    public static BlockType standard(String material, String name) {
        return new BlockType(material, false, name, 0) {
            @Override
            public void buildModel(GridMap gridMap, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer, RGBA light) {
            }
        };
    }

    public void buildModel(GridMap map, Vector3i pos, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer, RGBA light) {
        buildModel(map, pos.x, pos.y, pos.z, atlas, registry, buffer, light);
    }

    public boolean isCollidable() {
        return collidable;
    }

    public AABB[] getBoundingBoxes() {
        return hitboxes;
    }

    public abstract void buildModel(GridMap gridMap, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer, RGBA light);

    public float getFriction() {
        return friction;
    }
    
    public Light getLightSource() {
        return lightSource;
    }
}
