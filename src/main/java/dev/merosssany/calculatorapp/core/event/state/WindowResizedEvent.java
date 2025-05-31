package dev.merosssany.calculatorapp.core.event.state;

import dev.merosssany.calculatorapp.core.event.Event;
import dev.merosssany.calculatorapp.core.Window;

public class WindowResizedEvent extends Event {
    public int width, height;
    public Window window;

    public WindowResizedEvent(int width, int height, Window window) {
        this.width = width;
        this.height = height;
        this.window = window;
    }
}
