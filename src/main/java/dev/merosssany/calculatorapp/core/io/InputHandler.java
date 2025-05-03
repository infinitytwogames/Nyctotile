package dev.merosssany.calculatorapp.core.io;

import dev.merosssany.calculatorapp.core.EventBus;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.event.KeyPressEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class InputHandler {
    private final Window window;
    private GLFWKeyCallback glfwKeyCallback;

    public InputHandler(Window window) {
        this.window = window;
    }

    public void init() {
        glfwKeyCallback = GLFW.glfwSetKeyCallback(window.getWindow(), (windowHandle, key, scancode, action, mods) -> {
            EventBus.post(new KeyPressEvent(key,action));
        });
    }

    public void cleanup() {
        if (glfwKeyCallback != null) {
            glfwKeyCallback.close();
            glfwKeyCallback.free();
        }
    }
}
