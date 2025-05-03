package dev.merosssany.calculatorapp;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.ui.UI;
import dev.merosssany.calculatorapp.logging.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {
         GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Logger logger = new Logger("Main");
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
    }

    public static void render(Window window) {
        // Set the color for the display background (e.g., a lighter gray)
        glColor3f(0.9f, 0.9f, 0.9f);
        UI ui = new UI(new UIVector2Df(-1f,1f),1,0.5f,new RGB(1,1,1));
        // Define the vertices of the display rectangle
        ui.draw(window.getWidth(),window.getHeight());
        glBegin(GL_QUADS);

        glEnd();
    }
}