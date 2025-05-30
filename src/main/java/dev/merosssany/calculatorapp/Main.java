package dev.merosssany.calculatorapp;

import dev.merosssany.calculatorapp.core.*;
import dev.merosssany.calculatorapp.core.constants.Constants;
import dev.merosssany.calculatorapp.core.event.bus.EventBus;
import dev.merosssany.calculatorapp.core.event.input.MouseHoverEvent;
import dev.merosssany.calculatorapp.core.io.HoverEventRegister;
import dev.merosssany.calculatorapp.core.io.InputHandler;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.render.*;
import dev.merosssany.calculatorapp.core.ui.Cursor;
import dev.merosssany.calculatorapp.core.ui.Label;
import dev.merosssany.calculatorapp.core.ui.Screen;
import dev.merosssany.calculatorapp.core.ui.UI;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.ui.input.TextInput;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static final Logger logger = new Logger("Main");
    private static FontRenderer fontRenderer;
    private static ConcurrentLinkedQueue<Runnable> queuedTasks;
    private static Window window;
    private static InputHandler handler;
    private static HoverEventRegister hover;
    private static Cursor cursor;
    private static TextInput input;
    private static UI topLeftHalfElement;
    private static Matrix4f textProj;
    private static Matrix4f uiProjectionMatrix;
    private static Label tets;
    private static UIBatchRenderer screen;
    private static TextBatchRenderer textRenderer;

    private static final int VIRTUAL_UI_WIDTH = 1280;
    private static final int VIRTUAL_UI_HEIGHT = 720;

    public static void dispatchTask(Runnable task) {
        if (task == null) return;
        queuedTasks.add(task);
    }

    private static void runTasks() {
        Runnable task;
        while ((task = queuedTasks.poll()) != null) {
            task.run();
        }
    }

    public static void main(String[] args) {
        // Early Setup
        earlySetup();
        // Construction
        construction();
        // Initialization
        init();
        // Render Loop
        while (!glfwWindowShouldClose(window.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            runTasks(); // Most of them are reliant to OpenGL so here is suitable
            render();
            glfwSwapBuffers(window.getWindow()); // Swap the color buffers
            glfwPollEvents();       // Poll for window events
        }
        cleanup();
    }

    public static void tesst(String hmm) {
        logger.info(hmm);
    }

    private static void earlySetup() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = new Window(1024, 512, "Hello");
        logger.info("Early Setup");
        window.initOpenGL();
    }

    private static void construction() {
        EventBus.register(Main.class);
        String vertexSource = """
    #version 330 core
                
                    layout(location = 0) in vec2 aPos;
                    layout(location = 1) in vec4 aColor;
                
                    out vec4 vColor;
                
                    void main() {
                        gl_Position = vec4(aPos, 0.0, 1.0); // already in NDC
                        vColor = aColor;
                    }
                
""";

        String fragmentSource = """
    #version 330 core
                
                    in vec4 vColor;
                    out vec4 FragColor;
                
                    void main() {
                        FragColor = vColor;
                    }
                
""";
        fontRenderer = new FontRenderer(Constants.fontFilePath,32);
        textRenderer = new TextBatchRenderer(fontRenderer, 4);
        ShaderProgram shader = new ShaderProgram(vertexSource, fragmentSource);
        screen = new Screen(shader.getProgramId(),"Main");
        logger.info("Constructing...");
        cursor = new Cursor(screen, new UIVector2Df(-1f,1f), 0.1f, new RGBA(1f,1f,1f,1f));
        handler = new InputHandler(window);
        hover = new HoverEventRegister(window);
        topLeftHalfElement = new UI(screen, "Test",new UIVector2Df(0f,0f),2f,0.5f,new RGBA(0,1f,0f,0.5f));
        textProj = AdvancedMath.createVirtualProjection(1920,1080);
        queuedTasks = new ConcurrentLinkedQueue<>();
        tets = new Label(window,new UIVector2Df(-1f,1f),"hello",false, new RGB(1f,1f,1f),2,0.5f,new RGBA(1f,0,0,1f));
        topLeftHalfElement = new UI(screen, "Test",new UIVector2Df(0f,0f),2f,1f,new RGBA(0,1f,0f,1f));

        try {
            input = new TextInput(screen, window,Main.class.getDeclaredMethod("tesst",String.class),null,new UIVector2Df(0,0), new RGB(1f,1f,1f), 2f,0.5f,new RGBA(1f,0.5f,0.5f,0f));
        } catch (NoSuchMethodException e) {
            CleanupManager.createPopup(logger.formatStacktrace(e));
        }
    }

    private static void init() {
        topLeftHalfElement.init();;
    }

    private static void render() {
        screen.begin(new Matrix4f().ortho2D(1, 2, 2, 1));
//        topLeftHalfElement.draw();
        input.draw();
        screen.flush();
//        topLeftHalfElement.draw();wsd


        textRenderer.begin(AdvancedMath.createScaledProjection(window.getWidth(),window.getHeight()),new RGB(1f,1f,1f));
        textRenderer.queue("Hello",0,138);
        textRenderer.flush();
        EventBus.post(new MouseHoverEvent());
    }

    public static void cleanup() {
        fontRenderer.cleanup();
        window.cleanup();
        CleanupManager.exit(0);
    }

    public static Window getWindow() {
        return window;
    }

    public static FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public static UIBatchRenderer getUIBatchRenderer() {
        return screen;
    }
}