package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.core.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.core.model.TextureAtlas;
import org.infinitytwo.umbralore.core.model.builder.CubeModelBuilder;
import org.infinitytwo.umbralore.core.registry.BlockRegistry;
import org.infinitytwo.umbralore.core.world.GridMap;

public class StoneBlockType extends BlockType {
    public StoneBlockType(int index) {
        super("stone",false,"stone",index);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer) {
        CubeModelBuilder.standardVerticesList(map,x,y,z, atlas.getUVCoords(textureIndex), buffer);
    }
}
