package dev.merosssany.calculatorapp;

import dev.merosssany.calculatorapp.core.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class Main {
    public static void main(String[] args) {
         GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Window window = new Window(15,55,"Hello");
        window.show();
        System.out.println("...");
    }
}