package org.infinitytwo.umbralore.model.builder;

import org.infinitytwo.umbralore.model.Cube;
import org.infinitytwo.umbralore.model.Model;
import org.infinitytwo.umbralore.world.GridMap;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelBuilder implements Builder<Model> {
    private final Model model = new Model();

    private static void addFace(Model.Face face, int x, int y, int z, float[] uv, ArrayList<Float> vertices) {
        float u0 = uv[0]; // uMin
        float v0 = uv[1]; // vMin
        float u1 = uv[2]; // uMax
        float v1 = uv[3]; // vMax

        switch (face) {
            case NORTH -> { // -Z
                // tri 1
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.9f);

                // tri 2
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.9f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.9f);
            }
            case SOUTH -> { // +Z
                // tri 1
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.7f);
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.7f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.7f);

                // tri 2
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.7f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.7f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.7f);
            }
            case WEST -> { // -X
                // tri 1
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.8f);
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.8f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.8f);

                // tri 2
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.7f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.7f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.7f);
            }
            case EAST -> { // +X
                // tri 1
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.7f);
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.7f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.7f);

                // tri 2
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.7f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.7f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.7f);
            }
            case UP -> { // +Y
                // tri 1
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.9f);

                // tri 2
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.9f);
                vertices.add(1f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.9f);
                vertices.add(0f+x); vertices.add(1f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.9f);
            }
            case DOWN -> { // -Y
                // tri 1
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.5f);
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u1); vertices.add(v0); vertices.add(0.5f);
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.5f);

                // tri 2
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(1f+z); vertices.add(u0); vertices.add(v0); vertices.add(0.5f);
                vertices.add(1f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u1); vertices.add(v1); vertices.add(0.5f);
                vertices.add(0f+x); vertices.add(0f+y); vertices.add(0f+z); vertices.add(u0); vertices.add(v1); vertices.add(0.5f);
            }
        }
    }

    public static void standardVerticesList(GridMap map, int x, int y, int z, float[] uvs, ArrayList<Float> vertices) {
        if (map.isTransparent(x, y+1, z)) addFace(Model.Face.UP, x, y, z, uvs,vertices);
        if (map.isTransparent(x, y-1, z)) addFace(Model.Face.DOWN, x, y, z, uvs,vertices);
        if (map.isTransparent(x+1, y, z)) addFace(Model.Face.EAST, x, y, z, uvs,vertices);
        if (map.isTransparent(x-1, y, z)) addFace(Model.Face.WEST, x, y, z, uvs,vertices);
        if (map.isTransparent(x, y, z+1)) addFace(Model.Face.SOUTH, x, y, z, uvs,vertices);
        if (map.isTransparent(x, y, z-1)) addFace(Model.Face.NORTH, x, y, z, uvs,vertices);
    }

    public static void fullCube(int x, int y, int z, float[] uvs, ArrayList<Float> vertices) {
        addFace(Model.Face.UP, x, y, z, uvs,vertices);
        addFace(Model.Face.DOWN, x, y, z, uvs,vertices);
        addFace(Model.Face.EAST, x, y, z, uvs,vertices);
        addFace(Model.Face.WEST, x, y, z, uvs,vertices);
        addFace(Model.Face.SOUTH, x, y, z, uvs,vertices);
        addFace(Model.Face.NORTH, x, y, z, uvs,vertices);
    }

    public ModelBuilder insertCube(Cube cube) {
        model.addCube(cube);
        return this;
    }

    @Override
    public Model build() {
        return model;
    }
}
