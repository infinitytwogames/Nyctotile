package dev.merosssany.calculatorapp;

import dev.merosssany.calculatorapp.core.CleanupManager;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.ui.UI;
import dev.merosssany.calculatorapp.logging.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static final Logger logger = new Logger("Main");

    public static void main(String[] args) {
         GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        logger.log("Launching game...");

        Window window = new Window(700,450,"Hello");
        window.initOpenGL();

        while (!glfwWindowShouldClose(window.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

            render(window);

            // --- END RENDERING ---

            glfwSwapBuffers(window.getWindow()); // Swap the color buffers
            glfwPollEvents();       // Poll for window events
        }

        CleanupManager.exit(0);
    }

    public static void render(Window window) {
        logger.info(window.getWidth(),window.getHeight());
        // Set the color for the display background (e.g., a lighter gray)
        Vector2D<Float> topLeftHalfAnchorMin = new Vector2D<>(0.0f, 0.5f); // Bottom-left of top half
        Vector2D<Float> topLeftHalfAnchorMax = new Vector2D<>(0.5f, 1.0f); // Top-right of top half
        UI topLeftHalfElement = new UI(new UIVector2Df(-1f,1f),2f,0.5f ,new RGB(1.0f, 0.0f, 0.0f)); // Red
        // Define the vertices of the display rectangle
        topLeftHalfElement.draw(window.getWidth(),window.getHeight());
        glBegin(GL_QUADS);

        glEnd();
    }
}