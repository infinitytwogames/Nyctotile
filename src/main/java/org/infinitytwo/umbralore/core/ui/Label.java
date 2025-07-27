package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.RGB;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.font.Text;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Vector2i;

public abstract class Label extends UI {
    protected FontRenderer textRenderer;
    protected Text text;

    public Label(Screen renderer, FontRenderer textRenderer, RGB color) {
        super(renderer.getUIBatchRenderer(), "Label");
        this.textRenderer = textRenderer;
        text = new Text(textRenderer, renderer);
        text.setColor(color);
        text.setParent(this);
    }

    public void setTextPosition(Anchor anchor, Pivot pivot, Vector2i offset) {
        text.setPosition(anchor,pivot,offset);
    }

    @Override
    public void cleanup() {
        textRenderer.cleanup();
    }

    @Override
    public void draw() {
        super.draw();
        text.draw();
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public RGB getColor() {
        return text.getColor();
    }

    public void setColor(RGB color) {
        this.text.setColor(color);
    }

    public Vector2i getTextPosition() {
        return text.getPosition();
    }

    public Text getTextComponent() {
        return text;
    }
}
