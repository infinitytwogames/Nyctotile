package org.infinitytwo.umbralore.core.data;

import org.infinitytwo.umbralore.core.world.dimension.Dimension;

@Deprecated
public class World {
    private static Dimension dimension;

    public static Dimension getCurrentDimension() {
        return dimension;
    }

    public static void setCurrentDimension(Dimension dimension) {
        World.dimension = dimension;
    }
}
