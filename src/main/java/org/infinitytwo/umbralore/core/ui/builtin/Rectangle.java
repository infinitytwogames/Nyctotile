package org.infinitytwo.umbralore.core.ui.builtin;

import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.UI;
import org.infinitytwo.umbralore.core.ui.builder.UIBuilder;

public class Rectangle extends UI {
    public Builder builder(UIBatchRenderer renderer) {
        return new Builder(renderer);
    }

    public Rectangle(UIBatchRenderer renderer) {
        super(renderer);
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

    public static class Builder extends UIBuilder<Rectangle> {
        public Builder(UIBatchRenderer renderer) {
            super(new Rectangle(renderer));
        }

        @Override
        public UIBuilder<Rectangle> applyDefault() {
            return this;
        }

        public Rectangle build() {
            return ui;
        }
    }
}
