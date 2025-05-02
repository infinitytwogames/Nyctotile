package dev.merosssany.calculatorapp.core;

import static org.lwjgl.glfw.GLFW.*;

public class CleanupManager {

    private static boolean glfwInitialized = false; // Flag to track initialization

    public static void initGLFW() {
        if (!glfwInitialized) {
            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }
            glfwInitialized = true;
        }
    }

    public static void exit(int code) {
        if (code != 0) {
            // TODO: SHOW POP UP
        } else {
            cleanup();
        }
        terminateGLFW();
        System.exit(code);
    }

    private static void cleanup() {
        // TODO: CODE CLEANUP METHOD (release other resources)
    }

    public static void terminateGLFW() {
        if (glfwInitialized) {
            glfwTerminate();
            glfwSetErrorCallback(null).free(); // Also free the error callback
            glfwInitialized = false;
        }
    }
}