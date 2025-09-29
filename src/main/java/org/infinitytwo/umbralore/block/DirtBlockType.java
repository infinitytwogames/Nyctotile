package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.model.builder.ModelBuilder;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;

import java.util.ArrayList;

public class DirtBlockType extends BlockType {
    public DirtBlockType(int index) {
        super("soil",false,"dirt",index);
    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, ArrayList<Float> b) {
        ModelBuilder.standardVerticesList(map,x,y,z,atlas.getUVCoords(textureIndex),b);
    }
}
