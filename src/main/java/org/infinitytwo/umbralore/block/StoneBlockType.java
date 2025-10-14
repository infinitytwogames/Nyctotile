package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.model.builder.CubeModelBuilder;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;

public class StoneBlockType extends BlockType {
    public StoneBlockType(int index) {
        super("stone",false,"stone",index);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer buffer) {
        CubeModelBuilder.standardVerticesList(map,x,y,z, atlas.getUVCoords(textureIndex), buffer);
    }
}
