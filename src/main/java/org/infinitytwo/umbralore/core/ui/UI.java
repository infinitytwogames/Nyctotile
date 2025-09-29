package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.Display;
import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Vector2i;

public abstract class UI {
    protected final UIBatchRenderer renderer;
    protected RGBA backgroundColor = new RGBA();

    protected int width = 0;
    protected int height = 0;
    protected int index = 0;

    protected boolean hovering = false;

    protected Texture texture = null;
    protected Anchor anchor = new Anchor();
    protected Pivot pivot = new Pivot(0,0);
    protected Vector2i offset = new Vector2i();
    protected UI parent;

    public UI(UIBatchRenderer renderer) {
        this.renderer = renderer;
    }

    public boolean isHovering() {
        return hovering;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Vector2i getPosition() {
        int xa, ya;
        Vector2i o;

        if (parent == null) {
            xa = (int) (Display.width * anchor.x);
            ya = (int) (Display.height * anchor.y);
            o = offset;
        } else {
            xa = (int) (parent.width * anchor.x);
            ya = (int) (parent.height * anchor.y);
            o = new Vector2i(offset).add(parent.getPosition());
        }

        int xp = (int) (width * pivot.x());
        int yp = (int) (height * pivot.y());

        int x = xa + xp;
        int y = ya + yp;
        return new Vector2i(x+o.x,y+o.y);
//        return new Vector2i();
    }

    public void setPosition(Anchor anchor, Pivot pivot) {
        this.anchor = anchor;
        this.pivot = pivot;
    }

    public void setPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        setPosition(anchor,pivot);
        this.offset = offset;
    }

    public Vector2i getOffset() {
        return new Vector2i(offset);
    }

    public void setOffset(Vector2i offset) {
        this.offset = offset;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RGBA getBackgroundColor() {
        return backgroundColor;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public UI getParent() {
        return parent;
    }

    public void setParent(UI parent) {
        this.parent = parent;
    }

    public void setBackgroundColor(RGBA backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor.set(r,g,b,a);
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void draw() { // Can be used as "update"
        renderer.queue(this);
    }

    public Vector2i getEnd() {
        return getPosition().add(width,height);
    }

    public void addOffset(Vector2i v) {
        offset.add(v);
    }

    public abstract void onMouseClicked(MouseButtonEvent e);
    public abstract void onMouseHover(MouseHoverEvent e);
    public abstract void onMouseHoverEnded();
    public abstract void cleanup();
}