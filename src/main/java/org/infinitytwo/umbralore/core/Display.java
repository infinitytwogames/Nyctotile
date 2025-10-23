package org.infinitytwo.umbralore.core;

import org.infinitytwo.umbralore.core.event.SubscribeEvent;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.state.WindowResizedEvent;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import static org.infinitytwo.umbralore.core.constants.Constants.UI_DESIGN_HEIGHT;
import static org.lwjgl.opengl.GL11.*;

public final class Display {
    public static int width;
    public static int height = (int) UI_DESIGN_HEIGHT;
    public static Matrix4f projection = new Matrix4f();
    private static volatile boolean enabled = true;

    public static void init() {
        EventBus.register(Display.class);
    }

    public static Vector2i transformVirtualToWindow(Window window, int virtualX, int virtualY) {
        float scale = (float) window.getHeight() / Display.height;

        int screenX = (int) (virtualX * scale);
        int screenY = (int) (virtualY * scale);
        return new Vector2i(screenX, screenY);
    }

    public static Vector2i transformVirtualToWindow(Window window, Vector2i pos) {
        float scale = (float) window.getHeight() / Display.height;

        int screenX = (int) (pos.x * scale);
        int screenY = (int) (pos.y * scale);
        return new Vector2i(screenX, screenY);
    }

    public static Vector2i transformWindowToVirtual(Window window, int windowX, int windowY) {
        float scale = (float) Display.height / window.getHeight();

        int virtualX = (int) (windowX * scale);
        int virtualY = (int) (windowY * scale);

        return new Vector2i(virtualX, virtualY);
    }

    public static Vector2i transformWindowToVirtual(Window window, Vector2i pos) {
        float scale = (float) Display.height / window.getHeight();

        int virtualX = (int) (pos.x * scale);
        int virtualY = (int) (pos.y * scale);

        return new Vector2i(virtualX, virtualY);
    }


    @SubscribeEvent
    public static void onWindowResize(WindowResizedEvent e) {
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

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void prepare2d() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

    public static void prepare3d() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glFrontFace(GL_CW);
    }

    public static void resetGL() {
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }
}
