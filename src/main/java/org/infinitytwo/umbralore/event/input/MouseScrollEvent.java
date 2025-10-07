package org.infinitytwo.umbralore.event.input;

import org.infinitytwo.umbralore.Window;
import org.infinitytwo.umbralore.event.Event;

public class MouseScrollEvent extends Event {
    public final Window window;
    public final int x,y;

    public MouseScrollEvent(Window window, int x, int y) {
        this.window = window;
        this.x = x;
        this.y = y;
    }
}
