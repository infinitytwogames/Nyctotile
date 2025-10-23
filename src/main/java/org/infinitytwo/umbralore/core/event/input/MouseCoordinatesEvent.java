package org.infinitytwo.umbralore.core.event.input;

import org.infinitytwo.umbralore.core.event.Event;

public class MouseCoordinatesEvent extends Event {
    private final float normalizedX;
    private final float normalizedY;

    public MouseCoordinatesEvent(float normalizedX, float normalizedY) {
        this.normalizedX = normalizedX;
        this.normalizedY = normalizedY;
    }

    public float getNormalizedX() {
        return normalizedX;
    }

    public float getNormalizedY() {
        return normalizedY;
    }
}