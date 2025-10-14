package org.infinitytwo.umbralore.model;

import org.joml.Vector3i;

import java.util.ArrayList;

public class Model {
    protected ArrayList<Float> vertices = new ArrayList<>(), normals = new ArrayList<>();

    public void vertex(float x, float y, float z) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
    }

    public float[] toArray() {
        float[] arr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) arr[i] = vertices.get(i);
        return arr;
    }

    public void uv(float x, float y, float brightness) {
        vertices.add(x); vertices.add(y);
        vertices.add(brightness);
    }

    public void normal(float x, float y, float z) {
        normals.add(x);normals.add(y);normals.add(z);
    }

    public ArrayList<Float> getVertices() {
        return vertices;
    }

    public void getVertices(ArrayList<Float> buffer) {
        buffer.addAll(getVertices());
    }

    public void getVertices(TextureAtlas atlas, int textureCoords, Vector3i pos,  ArrayList<Float> data) {
        getVertices(atlas.getUVCoords(textureCoords),pos,data);
    }

    public void getVertices(float[] uvs, Vector3i pos, ArrayList<Float> data) {
        getVertices(uvs,pos.x, pos.y, pos.z, data);
    }

    public ArrayList<Float> getVertices(TextureAtlas atlas, int texture, Vector3i pos) {
        ArrayList<Float> f = new ArrayList<>();
        getVertices(atlas,texture,pos,f);
        return f;
    }

    public void getVertices(float[] uvs, int x, int y, int z, ArrayList<Float> data) {
        // [ x, y, z, u, v, brightness ]
        int vertexCount = vertices.size() / 6;
        for (int i = 0; i < vertexCount; i++) {
            data.add(vertices.get(i * 6) + x);     // x
            data.add(vertices.get(i * 6 + 1) + y); // y
            data.add(vertices.get(i * 6 + 2) + z); // z

            float uOrig = vertices.get(i * 6 + 3);
            float vOrig = vertices.get(i * 6 + 4);
            float u = uvs[0] + uOrig * (uvs[2] - uvs[0]);
            float v = uvs[1] + vOrig * (uvs[3] - uvs[1]);

            data.add(u);
            data.add(v);
            data.add(vertices.get(i * 6 + 5)); // brightness
        }
    }

    public void getVertices(TextureAtlas atlas, int textureIndex, int x, int y, int z, ArrayList<Float> buffer) {
        getVertices(atlas.getUVCoords(textureIndex),x,y,z,buffer);
    }
}
