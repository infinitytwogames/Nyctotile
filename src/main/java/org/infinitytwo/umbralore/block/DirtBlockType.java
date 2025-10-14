package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.model.builder.CubeModelBuilder;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;

public class DirtBlockType extends BlockType {
    public DirtBlockType(int index) {
        super("soil",false,"dirt",index);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, NFloatBuffer b) {
        CubeModelBuilder.standardVerticesList(map,x,y,z,atlas.getUVCoords(textureIndex),b);
    }
}
