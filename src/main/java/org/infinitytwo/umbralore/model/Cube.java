package org.infinitytwo.umbralore.model;

/**
 * This class is a building unit of a Model.
 * <br><br>
 * <strong>[ Disclaimer! ]</strong><br>
 * This class is <strong>NOT</strong> used in rendering.
 * It's used in collision testing only!
 */
public class Cube {
    private final float minX, minY, minZ;
    private final float maxX, maxY, maxZ;

    public Cube(float minX, float minY, float minZ,
                float maxX, float maxY, float maxZ
    ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    // Convenience: full cube
    public static Cube standard() {
        return new Cube(0, 0, 0, 1, 1, 1);
    }

    // Convenience: slab (half height)
    public static Cube slab() {
        return new Cube(0, 0, 0, 1, 0.5f, 1);
    }
}
