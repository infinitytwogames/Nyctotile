package org.infinitytwo.umbralore.ui.builder;

import org.infinitytwo.umbralore.RGBA;
import org.infinitytwo.umbralore.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.ui.builtin.Rectangle;

public class RectangleBuilder extends UIBuilder<Rectangle> {
    public RectangleBuilder(UIBatchRenderer renderer, Rectangle element) {
        super(element);
    }

    @Override
    public UIBuilder<Rectangle> applyDefault() {
        backgroundColor(new RGBA(0,0,0,1));
        return this;
    }
}
