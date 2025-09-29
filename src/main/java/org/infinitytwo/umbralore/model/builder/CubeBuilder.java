package org.infinitytwo.umbralore.model.builder;

import org.infinitytwo.umbralore.model.Cube;

public class CubeBuilder implements Builder<Cube> {
    private float minX = 0, minY = 0, minZ = 0;
    private float maxX = 1, maxY = 1, maxZ = 1;

    public CubeBuilder minX(float minX) {
        this.minX = minX;
        return this;
    }

    public CubeBuilder minY(float minY) {
        this.minY = minY;
        return this;
    }
    public CubeBuilder minZ(float minZ) {
        this.minZ = minZ;
        return this;
    }
    public CubeBuilder maxX(float maxX) {
        this.maxX = maxX;
        return this;
    }
    public CubeBuilder maxY(float maxY) {
        this.maxY = maxY;
        return this;
    }

    public CubeBuilder maxZ(float maxZ) {
        this.maxZ = maxZ;
        return this;
    }

    @Override
    public Cube build() {
        Cube cube = new Cube(minX,minY,minZ,maxX,maxY,maxZ);
        return cube;
    }

    public CubeBuilder setDefault() {
        minX = 0;
        minY = 0;
        minZ = 0;
        maxX = 1;
        maxY = 1;
        maxZ = 1;
        return this;
    }
}
