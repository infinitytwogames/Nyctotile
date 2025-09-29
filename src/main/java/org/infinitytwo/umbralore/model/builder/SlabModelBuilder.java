package org.infinitytwo.umbralore.model.builder;

import org.infinitytwo.umbralore.model.Model;
import org.infinitytwo.umbralore.world.GridMap;

import java.util.ArrayList;
import java.util.List;

public class SlabModelBuilder {
    private static final float minY = 0f,
    maxY = 0.5f,
    minX = 0f,
    maxX = 1f,
    minZ = 0f,
    maxZ = 1f;

    public static Model buildModel() {
        ModelBuilder builder = new ModelBuilder();
        CubeBuilder cubeBuilder = new CubeBuilder();

        cubeBuilder.setDefault().maxY(0.5f);
        return builder.insertCube(cubeBuilder.build()).build();
    }

    private static float[] addFace(Model.Face face) {
        switch (face) {
            case NORTH -> {
                return new float[] {
                        minX, minY, maxZ, 0f, 0f, 1f,
                        maxX, minY, maxZ, 1f, 0f, 1f,
                        maxX, maxY, maxZ, 1f, 1f, 1f,
                        minX, maxY, maxZ, 0f, 1f, 1f,
                };
            }
            case SOUTH -> {
                return new float[] {
                        maxX, minY, minZ, 0f, 0f, 0.8f,
                        minX, minY, minZ, 1f, 0f, 0.8f,
                        minX, maxY, minZ, 1f, 1f, 0.8f,
                        maxX, maxY, minZ, 0f, 1f, 0.8f,
                };
            }
            case WEST -> {
                return new float[] {
                        minX, minY, minZ, 0f, 0f, 0.9f,
                        minX, minY, maxZ, 1f, 0f, 0.9f,
                        minX, maxY, maxZ, 1f, 1f, 0.9f,
                        minX, maxY, minZ, 0f, 1f, 0.9f,
                };
            }
            case EAST -> {
                return new float[] {
                        maxX, minY, maxZ, 0f, 0f, 0.9f,
                        maxX, minY, minZ, 1f, 0f, 0.9f,
                        maxX, maxY, minZ, 1f, 1f, 0.9f,
                        maxX, maxY, maxZ, 0f, 1f, 0.9f,
                };
            }
            case UP -> {
                return new float[] {
                        minX, maxY, maxZ, 0f, 0f, 1f,
                        maxX, maxY, maxZ, 1f, 0f, 1f,
                        maxX, maxY, minZ, 1f, 1f, 1f,
                        minX, maxY, minZ, 0f, 1f, 1f,
                };
            }
            case DOWN -> {
                return new float[] {
                        minX, minY, minZ, 0f, 0f, 0.7f,
                        maxX, minY, minZ, 1f, 0f, 0.7f,
                        maxX, minY, maxZ, 1f, 1f, 0.7f,
                        minX, minY, maxZ, 0f, 1f, 0.7f
                };
            }
            default -> {
                return new float[]{};
            }
        }
    }

    public static float[] buildSlabVertices(GridMap map, int x, int y, int z) {
        List<Float> vertices = new ArrayList<>();

        if (!map.isTransparent(x, y - 1, z)) for (float f : addFace(Model.Face.DOWN)) vertices.add(f);
        if (!map.isTransparent(x + 1, y, z)) for (float f : addFace(Model.Face.EAST)) vertices.add(f);
        if (!map.isTransparent(x - 1, y, z)) for (float f : addFace(Model.Face.WEST)) vertices.add(f);
        if (!map.isTransparent(x, y, z + 1)) for (float f : addFace(Model.Face.SOUTH)) vertices.add(f);
        if (!map.isTransparent(x, y, z - 1)) for (float f : addFace(Model.Face.NORTH)) vertices.add(f);

        // Convert List<Float> to float[]
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }

        return vertexArray;
    }
}
