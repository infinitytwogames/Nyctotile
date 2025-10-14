package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.data.AABB;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Block {
    protected Vector3i position = new Vector3i();
    private final BlockType type;

    public Block(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return type;
    }

    public boolean isInvisible() {
        return type.isInvisible();
    }

    public String getMaterial() {
        return type.getMaterial();
    }

    public String getName() {
        return type.getName();
    }

    public String getId() {
        return type.getId();
    }

    public boolean isCollidable() {
        return type.isCollidable();
    }

    public AABB[] getBoundingBoxes() {
        return type.getBoundingBoxes();
    }

    public Block setPosition(int x, int y, int z) {
        this.position = new Vector3i(x,y,z);
        return this;
    }

    public void setPosition(Vector3f position) {
        position.set(position);
    }

    public Vector3i getPosition() {
        return position;
    }
}
