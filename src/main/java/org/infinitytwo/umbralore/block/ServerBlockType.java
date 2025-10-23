package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.core.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.core.model.TextureAtlas;
import org.infinitytwo.umbralore.core.registry.BlockRegistry;
import org.infinitytwo.umbralore.core.world.GridMap;

public class ServerBlockType extends BlockType {
    public ServerBlockType(String material, boolean b, String name) {
        super(material, b, name, 0);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer) {

    }
}
