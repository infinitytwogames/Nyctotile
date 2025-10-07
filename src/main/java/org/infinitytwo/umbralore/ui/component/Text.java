package org.infinitytwo.umbralore.ui.component;

import org.infinitytwo.umbralore.Display;
import org.infinitytwo.umbralore.RGB;
import org.infinitytwo.umbralore.renderer.FontRenderer;
import org.infinitytwo.umbralore.ui.Screen;
import org.infinitytwo.umbralore.ui.UI;
import org.infinitytwo.umbralore.ui.position.Anchor;
import org.infinitytwo.umbralore.ui.position.Pivot;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class Text implements Component {
    protected Anchor anchor = new Anchor();
    protected Pivot pivot = new Pivot(0, 0);
    protected Vector2i offset = new Vector2i();
    protected UI parent = null;
    protected final FontRenderer renderer;
    protected String text = "";
    protected RGB color = new RGB(1, 1, 1);
    protected boolean centerY = true;
    protected Screen screen;

    public Text(FontRenderer renderer, Screen screen) {
        this.renderer = renderer;
        this.screen = screen;
    }

    public boolean isCenterY() {
        return centerY;
    }

    public void setCenterY(boolean centerY) {
        this.centerY = centerY;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public void setOffset(Vector2i offset) {
        this.offset = offset;
    }

    public UI getParent() {
        return parent;
    }

    public void setParent(UI parent) {
        this.parent = parent;
    }

    public void setPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        setAnchor(anchor);
        setPivot(pivot);
        setOffset(offset);
    }

    public Vector2i getPosition() {
        int xa, ya;
        Vector2i o;
        if (parent == null) {
            xa = (int) (Display.width * anchor.x);
            ya = (int) (Display.height * anchor.y);
            o = offset;
        } else {
            xa = (int) (parent.getWidth() * anchor.x);
            ya = (int) (parent.getHeight() * anchor.y);
            int div = centerY ? 2 : 1;
            o = new Vector2i(offset).add(parent.getPosition()).add(0, (int) (renderer.getFontHeight() /div));
        }

        int xp = (int) (renderer.getStringWidth(text) * pivot.x());
        int yp = (int) ((renderer.getFontHeight() / 2) * pivot.y());

        int x = xa + xp;
        int y = ya + yp;
        return new Vector2i(x + o.x, y + o.y);
    }

    public RGB getColor() {
        return color;
    }

    public void setColor(@NotNull RGB color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    public void draw() {
        screen.run(() -> renderer.renderText(text, getPosition(), color));
    }

    public void setPosition(Anchor anchor, Pivot pivot) {
        setAnchor(anchor);
        setPivot(pivot);
    }

    public int getTextSize(String string) {
        return (int) renderer.getStringWidth(string);
    }

    public void setOffset(int x, int y) {
        offset.set(x,y);
    }
}
