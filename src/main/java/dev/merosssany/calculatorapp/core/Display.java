package dev.merosssany.calculatorapp.core;

import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.core.event.bus.EventBus;
import dev.merosssany.calculatorapp.core.event.state.WindowResizedEvent;

import static dev.merosssany.calculatorapp.core.constants.Constants.UI_DESIGN_HEIGHT;

public abstract class Display {
    public static int width;
    public static int height = (int) UI_DESIGN_HEIGHT;

    public static void init() {
        EventBus.register(Display.class);
    }

    @SubscribeEvent
    public static void onWindowResize(WindowResizedEvent e) {
        float currentWindowWidth = e.width;
        float currentWindowHeight = e.height;

        width = (int) ((currentWindowWidth / currentWindowHeight) * height);
    }
}
