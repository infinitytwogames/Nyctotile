package org.infinitytwo.umbralore.core.data;

import org.infinitytwo.umbralore.core.renderer.Chunk;
import org.joml.Vector2i;

public record ChunkPos(int x, int z) {
    public ChunkPos(Chunk chunk) {
        this(chunk.getPosition().x,chunk.getPosition().y);
    }
    
    public ChunkPos(Vector2i pos) {
        this(pos.x,pos.y);
    }
}
