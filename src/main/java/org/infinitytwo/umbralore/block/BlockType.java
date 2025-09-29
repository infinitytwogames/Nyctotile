package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.model.Model;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockType {
    public int textureIndex;
    protected Model model;
    public List<Float> vertex;
    protected final String material;
    protected final boolean invisible;
    protected final String id;

    public BlockType(String material, boolean invisible, String name, int textureIndex) {
        this.textureIndex = textureIndex;
        this.material = material;
        this.invisible = invisible;
        this.id = name;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public abstract void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, ArrayList<Float> buffer);

    public Model getModel() {
        return model;
    }

    public String getMaterial() { return material; }

    public String getName() {
        return id;
    }

    public String getId() {
        return id;
    }

    public static BlockType standard(String material, String name) {
        return new BlockType(material, false, name,0) {
            @Override
            public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, ArrayList<Float> b) {}
        };
    }

    public void buildModel(GridMap gridMap, Vector3i worldPos, TextureAtlas atlas, BlockRegistry registry, ArrayList<Float> b) {
        buildModel(gridMap,worldPos.x, worldPos.y, worldPos.z, atlas, registry, b);
    }

    public static void addQuad(List<Float> verts,
                        float x1, float y1, float z1, float u1, float v1,
                        float x2, float y2, float z2, float u2, float v2,
                        float x3, float y3, float z3, float u3, float v3,
                        float x4, float y4, float z4, float u4, float v4,
                        float brightness) {
        // Triangle 1
        verts.add(x1); verts.add(y1); verts.add(z1);
        verts.add(u1); verts.add(v1);
        verts.add(brightness);

        verts.add(x2); verts.add(y2); verts.add(z2);
        verts.add(u2); verts.add(v2);
        verts.add(brightness);

        verts.add(x3); verts.add(y3); verts.add(z3);
        verts.add(u3); verts.add(v3);
        verts.add(brightness);

        // Triangle 2
        verts.add(x1); verts.add(y1); verts.add(z1);
        verts.add(u1); verts.add(v1);
        verts.add(brightness);

        verts.add(x3); verts.add(y3); verts.add(z3);
        verts.add(u3); verts.add(v3);
        verts.add(brightness);

        verts.add(x4); verts.add(y4); verts.add(z4);
        verts.add(u4); verts.add(v4);
        verts.add(brightness);
    }
}
