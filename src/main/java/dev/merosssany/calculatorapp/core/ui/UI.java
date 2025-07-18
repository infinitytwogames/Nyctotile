package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.Display;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import org.joml.Vector2i;

public abstract class UI {
    protected int width = 0;
    protected int height = 0;
    protected RGBA backgroundColor = new RGBA();
    protected Texture texture = null;
    protected String name;
    protected UIBatchRenderer renderer;
    protected Anchor anchor;
    protected Pivot pivot;

    public UI(UIBatchRenderer renderer, String name) {
        this.renderer = renderer;
        this.name = name;
    }

    public Vector2i getPosition() {
        int xa = (int) (Display.width * anchor.x);
        int ya = (int) (Display.height * anchor.y);

        int xp = (int) (width * pivot.x());
        int yp = (int) (height * pivot.y());

        int x = xa + xp;
        int y = ya + yp;
        return new Vector2i(x,y);
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

    public void setBackgroundColor(RGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.backgroundColor = new RGBA(r,g,b,a);
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public void draw() {
        renderer.queue(this);
    }

    public Vector2i getEnd() {
        return getPosition().add(width,height);
    }
}