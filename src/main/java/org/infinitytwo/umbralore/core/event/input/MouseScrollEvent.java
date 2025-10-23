package org.infinitytwo.umbralore.core.event.input;

import org.infinitytwo.umbralore.core.Window;
import org.infinitytwo.umbralore.core.event.Event;

public class MouseScrollEvent extends Event {
    public final Window window;
    public final int x,y;

    public MouseScrollEvent(Window window, int x, int y) {
        this.window = window;
        this.x = x;
        this.y = y;
    }
}
