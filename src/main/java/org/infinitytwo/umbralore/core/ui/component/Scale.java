package org.infinitytwo.umbralore.core.ui.component;

import org.infinitytwo.umbralore.core.Display;
import org.infinitytwo.umbralore.core.event.SubscribeEvent;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.state.WindowResizedEvent;

public class Scale implements Component {
    protected float xRatio, yRatio;
    protected int width, height;

    public Scale(float xRatio, float yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;

        updateSize(); // initialize once
        EventBus.register(this);
    }

    private void updateSize() {
        width = (int) (Display.width * xRatio);
        height = (int) (Display.height * yRatio);
    }

    public void setRatios(float xRatio, float yRatio) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        updateSize();
    }

    @SubscribeEvent
    public void windowResize(WindowResizedEvent e) {
        updateSize();
    }


    @Override
    public void draw() {

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
