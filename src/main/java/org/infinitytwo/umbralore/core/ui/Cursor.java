package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;

public class Cursor extends UpdatableUI {
    private float blinkTimer = 0f;
    private boolean visible = true;
    private boolean active = true;

    public Cursor(UIBatchRenderer renderer, int height) {
        super(renderer, "Cursor");
        setWidth(5);
        setHeight(height);
        setBackgroundColor(new RGBA(1,1,1,1));
    }

    public void update(float delta) {
        blinkTimer += delta;
        if (blinkTimer >= 0.5f) {
            blinkTimer = 0f;
            visible = !visible;
        }
    }

    public void reset() {
        blinkTimer = 0f;
    }

    @Override
    public void draw() {
        if (visible && active) {
            super.draw();
        }
    }

    @Override
    public void onMouseClicked(MouseButtonEvent e) {

    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {

    }

    @Override
    public void onMouseHoverEnded() {

    }

    @Override
    public void cleanup() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

