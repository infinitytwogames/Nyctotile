package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;

public class ServerBlockType extends BlockType {
    public ServerBlockType(String material, boolean b, String name) {
        super(material, b, name, 0);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer) {

    }
}
