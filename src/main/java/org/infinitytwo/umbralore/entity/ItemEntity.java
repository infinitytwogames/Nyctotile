package org.infinitytwo.umbralore.entity;

import org.infinitytwo.umbralore.Window;
import org.infinitytwo.umbralore.data.Inventory;
import org.infinitytwo.umbralore.data.buffer.NFloatBuffer;
import org.infinitytwo.umbralore.model.Model;
import org.infinitytwo.umbralore.model.builder.ModelBuilder;
import org.infinitytwo.umbralore.registry.ModelRegistry;
import org.infinitytwo.umbralore.world.GridMap;

public class ItemEntity extends Entity {
    private static final int index;

    static {
        Model model = new Model();
        NFloatBuffer buffer = new NFloatBuffer();
        ModelBuilder b = new ModelBuilder(0,0,0,0.5f,0.5f,0.5f);
        b.cube(buffer,new float[]{0,0,1,1});

        model.setVertices(buffer.getBuffer());
        index = ModelRegistry.register(model);

        buffer.cleanup();
    }

    protected ItemEntity(GridMap map, Window window) {
        super("item", map, window, new Inventory(1));

        setModelIndex(index);
    }
}
