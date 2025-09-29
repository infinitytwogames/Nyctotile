package org.infinitytwo.umbralore;

import org.infinitytwo.umbralore.event.SubscribeEvent;
import org.infinitytwo.umbralore.event.bus.EventBus;
import org.infinitytwo.umbralore.event.state.WindowResizedEvent;
import org.joml.Matrix4f;

import static org.infinitytwo.umbralore.constants.Constants.UI_DESIGN_HEIGHT;

public final class Display {
    public static int width;
    public static int height = (int) UI_DESIGN_HEIGHT;
    public static Matrix4f projection = new Matrix4f();
    public static boolean enabled = true;

    public static void init() {
        EventBus.register(Display.class);
    }

    @SubscribeEvent
    public static void onWindowResize(WindowResizedEvent e) {
        if (!enabled) return;
        float currentWindowWidth = e.width;
        float currentWindowHeight = e.height;

        width = (int) ((currentWindowWidth / currentWindowHeight) * UI_DESIGN_HEIGHT);
        projection.setOrtho(
                0.0f,               // Left
                width, // Right
                UI_DESIGN_HEIGHT,   // Bottom (max Y)
                0.0f,               // Top (min Y)
                -1.0f,              // Near
                1.0f                // Far
        );
    }
}
