package org.infinitytwo.umbralore.core.ui.input;

import org.infinitytwo.umbralore.core.RGB;
import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.ui.Label;
import org.infinitytwo.umbralore.core.ui.Screen;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Vector2i;

public abstract class Button extends Label {
    protected RGBA original;

    public Button(Screen renderer, FontRenderer fontRenderer, RGB color, String s) {
        super(renderer, fontRenderer, color);
        original = getBackgroundColor();

        setText(s);
        setTextPosition(new Anchor(0.5f,0.5f),new Pivot(0.5f,0.5f),new Vector2i());
    }

    @Override
    public void setBackgroundColor(RGBA backgroundColor) {
        original = backgroundColor;
        super.setBackgroundColor(backgroundColor);
    }

    @Override
    public void setBackgroundColor(float r, float g, float b, float a) {
        original.set(r, g, b, a);
        super.setBackgroundColor(r,g,b,a);
    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {
        float d = 0.25f;
        super.setBackgroundColor(
                Math.max(0, original.red - d),
                Math.max(0, original.green - d),
                Math.max(0, original.blue - d),
                original.alpha
        );
    }


    @Override
    public void onMouseHoverEnded() {
        super.setBackgroundColor(original);
    }

    public static class ButtonBuilder<T extends Button> extends LabelBuilder<T> {
        public ButtonBuilder(UIBatchRenderer renderer, T element) {
            super(renderer, element);
        }
    }
}