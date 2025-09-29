package org.infinitytwo.umbralore.core.ui.builtin;

import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.UI;

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

    public static class RectBuilder {
        public final Rectangle rectangle;

        public RectBuilder(UIBatchRenderer renderer) {
            rectangle = new Rectangle(renderer);
        }
    }
}
