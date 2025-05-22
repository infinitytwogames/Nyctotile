package dev.merosssany.calculatorapp;

import dev.merosssany.calculatorapp.core.*;
import dev.merosssany.calculatorapp.core.constants.Constants;
import dev.merosssany.calculatorapp.core.event.EventBus;
import dev.merosssany.calculatorapp.core.event.input.MouseHoverEvent;
import dev.merosssany.calculatorapp.core.event.stack.EventStack;
import dev.merosssany.calculatorapp.core.io.HoverEventRegister;
import dev.merosssany.calculatorapp.core.io.InputHandler;
import dev.merosssany.calculatorapp.core.logging.LoggingLevel;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.render.CleanupManager;
import dev.merosssany.calculatorapp.core.render.Window;
import dev.merosssany.calculatorapp.core.ui.Cursor;
import dev.merosssany.calculatorapp.core.ui.input.Button;
import dev.merosssany.calculatorapp.core.ui.UI;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.ui.input.TextInput;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static final Logger logger = new Logger("Main");
    private static FontRenderer test;

    public static void main(String[] args) {
        EventStack.registerChannel("initial");

        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Window window = new Window(1024, 512, "Hello");
        logger.info("Launching game...");
        window.initOpenGL();

        InputHandler handler = new InputHandler(window);
        HoverEventRegister hover = new HoverEventRegister(window);
        Cursor cursor = new Cursor("...",new UIVector2Df(-1f,1),0.25f,new RGBA(1f,1f,1f,1f));

        TextInput input = null;
        try {
            input = new TextInput(window,Main.class.getDeclaredMethod("tesst",String.class),null,new UIVector2Df(0,0),32,new RGB(1f,1f,1f),Constants.fontFilePath,2f,0.5f,new RGBA(1f,0.5f,0.5f,1f));
        } catch (NoSuchMethodException e) {
            CleanupManager.createPopup(logger.formatStacktrace(e, LoggingLevel.FATAL));
        }

        logger.info("Constructing....");
        Matrix4f textProj = new Matrix4f().ortho2D(0, window.getWidth(), window.getHeight(), 0);
        test = new FontRenderer(Constants.fontFilePath,32);

//        InteractableUI t = new InteractableUI(new UIVector2Df(-1f, 0f), 2f, 0.5f, new RGBA(0, 255, 0, 1f), window);
//        Button button = new Button("Test",new RGB(1f,0f,0f),32,Constants.fontFilePath,0f,Main::tesst,new UIVector2Df(-1f,0f), 0.5f, 1f, new RGBA(1f,1f,1f,1f),window);
        UI topLeftHalfElement = new UI("Test", new UIVector2Df(-1f, 1f), 2f, 0.5f, new RGBA(0f, 0, 1f, 1f));

//        button.init();
        handler.init();
        topLeftHalfElement.init();
        input.init();
        cursor.init();

        cursor.animate();
//        t.init();
        // Render Loop
        while (!glfwWindowShouldClose(window.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            hover.update();
//            topLeftHalfElement.draw();
//            button.draw();
//            t.draw();
            input.draw();
            cursor.draw();

            EventBus.post(new MouseHoverEvent());
            test.renderText(textProj,"Helljewhf ejwvhfiwehvwnhvgo",0,138,1,1,1);

            glfwSwapBuffers(window.getWindow()); // Swap the color buffers
            glfwPollEvents();       // Poll for window events
        }

        window.cleanup();
        CleanupManager.exit(0);
        handler.cleanup();
    }

    public static void tesst(String hmm) {
        logger.info(hmm);
    }
}