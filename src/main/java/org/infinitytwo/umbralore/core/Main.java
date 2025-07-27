package org.infinitytwo.umbralore.core;

import org.infinitytwo.umbralore.core.constants.Constants;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.event.state.WindowResizedEvent;
import org.infinitytwo.umbralore.core.renderer.*;
import org.infinitytwo.umbralore.core.ui.*;
import org.infinitytwo.umbralore.core.logging.Logger;
import org.infinitytwo.umbralore.core.ui.input.Button;
import org.infinitytwo.umbralore.core.ui.input.TextInput;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static final Logger logger = new Logger("Main");
    private static FontRenderer fontRenderer;
    private static ConcurrentLinkedQueue<Runnable> queuedTasks;
    private static Window window;
    private static UI topLeftHalfElement;
    private static Matrix4f textProj;
    private static Label tets;
    private static UIBatchRenderer renderer;
    private static TextBatchRenderer textRenderer;
    private static Screen screen;

    private static final int VIRTUAL_UI_WIDTH = 1280;
    private static final int VIRTUAL_UI_HEIGHT = 720;
    public static ProgressBar bar;

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
        Display.init();
        Display.onWindowResize(new WindowResizedEvent(window));
        ShaderProgram program = new ShaderProgram(
                """
                        #version 330 core
                        // Layouts
                        layout (location = 0) in vec2 aPos;
                        layout (location = 1) in vec4 aColor;
                        layout (location = 2) in vec2 aTexturePos;
                        
                        // Uniform
                        uniform mat4 projection;
                        
                        // Output
                        out vec4 color;
                        out vec2 texCoords;
                        
                        void main() {
                            gl_Position = projection * vec4(aPos, 0.0, 1.0);
                            color = aColor;
                            texCoords = aTexturePos;
                        }
                        """,
                """
                        #version 330 core
                        // Input
                        in vec4 color;
                        in vec2 texCoords;
                        
                        // Uniform
                        uniform sampler2D uTexture;
                        uniform bool useTexture;
                        
                        // Output
                        out vec4 FragColor;
                        
                        void main() {
                            if (useTexture) {
                                FragColor = texture(uTexture, texCoords) * color;
                            } else {
                                FragColor = color;
                            }
                        }
                        """
        );
        // IMPORTANT CONSTRUCTION
        EventBus.register(Main.class);
        fontRenderer = new FontRenderer(Constants.fontFilePath,32);
        textRenderer = new TextBatchRenderer(fontRenderer, 1);
        renderer = new UIBatchRenderer(program);
        screen = new Screen(renderer,window);
        logger.info("Constructing...");
        window.setWindowIcon("src/main/resources/assets/icon/icon.png");
        queuedTasks = new ConcurrentLinkedQueue<>();
//        tets = new Label();
//        topLeftHalfElement = new UI(screen, "Test",new UIVector2Df(0f,0f),2f,1f,new RGBA(0,1f,0f,1f));
//        try {
//            input = new TextInput(screen, window,Main.class.getDeclaredMethod("tesst",String.class),null,new UIVector2Df(0,0), new RGB(1f,1f,1f), 2f,0.5f,new RGBA(1f,0.5f,0.5f,1f));
//        } catch (NoSuchMethodException e) {
//            CleanupManager.createPopup(logger.formatStacktrace(e));
//        }
    }

    private static void init() {
        Display.init();
        FontRenderer renderer1 = new FontRenderer("src/main/resources/assets/fonts/Main.ttf",32);
        Button ti = new Button(screen,renderer1,new RGB(1,1,1), "Hello :)") {
            @Override
            public void onMouseClicked(MouseButtonEvent e) {
                bar.incrementCurrent();
            }
        };
        ti.setBackgroundColor(new RGBA(0,0,1,0.6f));
        ti.setWidth(512);
        ti.setHeight(150);
        ti.setPosition(new Anchor(0.5f,0.5f),new Pivot(0.5f,0.5f));

        bar = new ProgressBar(screen,fontRenderer,new RGB(1,1,1),10);
        bar.setPosition(new Anchor(0.5f,1f),new Pivot(0,0.5f),new Vector2i(0,-150));
        bar.setBackgroundColor(0,0,0.25f,1);
        bar.setHeight(150);
        bar.setWidth(512);

        logger.info(ti.getPosition());

        screen.register(ti);
        screen.register(bar);
    }

    private static void render() {
        screen.draw();
    }

    public static void cleanup() {
        fontRenderer.cleanup();
        renderer.cleanup();
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
        return renderer;
    }

    public static TextBatchRenderer getFontBatchRenderer() {
        return textRenderer;
    }
}