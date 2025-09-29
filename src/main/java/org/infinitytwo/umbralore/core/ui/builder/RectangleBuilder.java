package org.infinitytwo.umbralore.core.ui.builder;

import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.builtin.Rectangle;

public class RectangleBuilder extends UIBuilder<Rectangle> {
    public RectangleBuilder(UIBatchRenderer renderer, Rectangle element) {
        super(renderer, element);
    }

    @Override
    public UIBuilder<Rectangle> applyDefault() {
        backgroundColor(new RGBA(0,0,0,1));
        return this;
    }
}
