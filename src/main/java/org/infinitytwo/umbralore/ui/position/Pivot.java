package org.infinitytwo.umbralore.ui.position;

public record Pivot(float x, float y) {
    public Pivot(float x, float y) {
        this.x = -x;
        this.y = -y;
    }
}
