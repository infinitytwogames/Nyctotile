package org.infinitytwo.umbralore.data.buffer;

public abstract class NativeBuffer {
    protected int written;
    protected int capacity;

    public abstract void cleanup();

    public int getWritten() { return written; }
}
