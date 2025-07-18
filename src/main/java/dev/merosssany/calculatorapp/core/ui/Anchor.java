package dev.merosssany.calculatorapp.core.ui;

import org.joml.Vector2f;

public class Anchor {
    public final float x;
    public final float y;

    public Anchor(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Anchor(Vector2f pos) {
        this.x = pos.x;
        this.y = pos.y;
    }
}
