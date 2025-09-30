package org.infinitytwo.umbralore.ui.builtin;

import org.infinitytwo.umbralore.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.ui.UI;
import org.infinitytwo.umbralore.ui.builder.UIBuilder;

public class Rectangle extends UI {
    public RectBuilder builder(UIBatchRenderer renderer) {
        return new RectBuilder(renderer);
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

    public static class RectBuilder extends UIBuilder<Rectangle> {
        public RectBuilder(UIBatchRenderer renderer) {
            super(renderer, new Rectangle(renderer));
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
