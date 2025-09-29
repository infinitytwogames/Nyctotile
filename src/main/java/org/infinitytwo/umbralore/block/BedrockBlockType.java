package org.infinitytwo.umbralore.block;

import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.model.builder.ModelBuilder;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.GridMap;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class BedrockBlockType extends BlockType {
    public BedrockBlockType(int index) {
        super("bedrock", false, "bedrock",index);


    }

    @Override
    public void buildModel(GridMap map, int x, int y, int z, TextureAtlas atlas, BlockRegistry registry, ArrayList<Float> b) {
        ModelBuilder.standardVerticesList(map,x,y,z,atlas.getUVCoords(textureIndex),b);
    }
}
