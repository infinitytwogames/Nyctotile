package org.infinitytwo.umbralore.data;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;

public class ChunkMeshData {
    private final List<Float> vertexList;
    private int vertexCount;
    private FloatBuffer buffer;

    public ChunkMeshData(List<Float> combinedVertices) {
        vertexList = combinedVertices;
        vertexCount = combinedVertices.size() / 6; // 6 floats per vertex
    }

    public void build() {
        buffer = memAllocFloat(vertexList.size());
        for (float f : vertexList) buffer.put(f);
        buffer.flip();
    }

    public FloatBuffer getBuffer() {
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void free() {
        if (buffer != null) {
            memFree(buffer);
            buffer = null;
        }
        vertexList.clear();
        vertexCount = 0;
    }

    public FloatBuffer toFloatBuffer() {
        return getBuffer();
    }
}
