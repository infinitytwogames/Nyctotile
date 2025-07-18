package dev.merosssany.calculatorapp.core;

import dev.merosssany.calculatorapp.core.event.bus.EventBus;
import dev.merosssany.calculatorapp.core.event.input.CharacterInputEvent;
import dev.merosssany.calculatorapp.core.event.input.KeyPressEvent;
import dev.merosssany.calculatorapp.core.event.input.MouseButtonEvent;
import dev.merosssany.calculatorapp.core.event.state.WindowResizedEvent;
import dev.merosssany.calculatorapp.core.io.ResourceLoader;
import dev.merosssany.calculatorapp.core.logging.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;

public class Window {
    private long window = 0;
    private String title;
    private int width;
    private int height;
    private final Logger logger = new Logger("Window");

    private GLFWFramebufferSizeCallback framebufferSizeCallback;
    private GLFWKeyCallback glfwKeyCallback;
    private GLFWMouseButtonCallback glfwMouseButtonCallback;
    private GLFWCharCallback glfwCharCallback;

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public Window(int width, int height, String title) {
        this.height = height;
        this.width = width;
        this.title = title;
        initGLFW();
    }

    private void initGLFW() {
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(primaryMonitor);

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        this.window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        Window instance = this;
        GLFW.glfwSetFramebufferSizeCallback(window, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int newWidth, int newHeight) {
                width = newWidth;
                height = newHeight;
                EventBus.post(new WindowResizedEvent(width,height,instance));
                GL11.glViewport(0, 0, width, height);
            }
        });

        if (vidMode != null) {
            int centerX = (vidMode.width() - width) / 2;
            int centerY = (vidMode.height() - height) / 2;
            GLFW.glfwSetWindowPos(window, centerX, centerY);
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        // Enable v-sync
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        EventBus.post(new WindowResizedEvent(width,height,this));

        glfwKeyCallback = GLFW.glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            logger.info("KeyPressEvent Callback Fired - InputHandler Instance: " + this.hashCode());
            if (isFocused()) EventBus.post(new KeyPressEvent(key, action));
        });

        glfwMouseButtonCallback = GLFW.glfwSetMouseButtonCallback(window, (windowHandle, button, action, mods) -> {
            logger.info("Posting MouseButtonEvent - InputHandler Instance: " + this.hashCode());
            if (isFocused()) EventBus.post(new MouseButtonEvent(button, action));
        });

        glfwCharCallback = GLFW.glfwSetCharCallback(window, (windowHandle, codepoint) -> {
            if (isFocused()) EventBus.post(new CharacterInputEvent(codepoint));
        });
    }

    public void initOpenGL() {
        GLCapabilities capabilities = GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glViewport(0, 0, width, height);
    }

    public long getWindow() {
        return window;
    }

    public void cleanup() {
        if (glfwKeyCallback != null) glfwKeyCallback.free();
        if (glfwMouseButtonCallback != null) glfwMouseButtonCallback.free();
        if (glfwCharCallback != null) glfwCharCallback.free();
        if (framebufferSizeCallback != null) {
            framebufferSizeCallback.free();
        }

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public Vector2f getMousePosition() {
        double[] cursorPositionX = new double[1];
        double[] cursorPositionY = new double[1];
        GLFW.glfwGetCursorPos(window, cursorPositionX, cursorPositionY);
        double mouseX = cursorPositionX[0];
        double mouseY = cursorPositionY[0];
        float normalizedMouseX = (float) ((2.0 * mouseX) / width - 1.0);
        float normalizedMouseY = (float) (1.0 - (2.0 * mouseY) / height);

        return new Vector2f(normalizedMouseX,normalizedMouseY);
    }

    /**
     * Sets the window icon for the given GLFW window handle.
     * @param iconPath     The path to the icon image file (e.g., "assets/textures/icon.png").
     * This path should be relative to your resources folder or project root.
     */
    public void setWindowIcon(String iconPath) {
        ByteBuffer imageBuffer;
        try (MemoryStack stack = MemoryStack.stackPush()) { // Use MemoryStack for temporary allocations
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1); // Number of components (e.g., 3 for RGB, 4 for RGBA) // BTW you see the "components" here. It did not count it

            // 1. Load the image into a ByteBuffer
            try {
                // Use the helper to load from classpath or filesystem
                imageBuffer = ResourceLoader.ioResourceToByteBuffer(iconPath, 4 * 1024); // 4KB initial buffer
            } catch (IOException e) {
                logger.error("Failed to load icon resource: " + iconPath, e);
                return;
            }

            // Load image using STBImage (expects 4 components for RGBA) // BUT NOT THAT ONE 💀
            ByteBuffer decodedImage = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);
            if (decodedImage == null) {
                logger.error("Failed to decode image data for icon: " + iconPath + " - " + STBImage.stbi_failure_reason());
                return;
            }

            int width = w.get(0);
            int height = h.get(0);

            // 2. Prepare GLFWImage structure(s)
            // You can provide multiple icons (e.g., different sizes) for the OS to choose from.
            // For simplicity, we'll provide one.
            GLFWImage.Buffer icons = GLFWImage.malloc(1, stack); // Allocate space for 1 GLFWImage struct

            // Populate the GLFWImage struct
            icons.width(width);
            icons.height(height);
            icons.pixels(decodedImage); // The raw pixel data

            // 3. Set the window icon
            GLFW.glfwSetWindowIcon(window, icons);

            // 4. Clean up the decoded image data
            STBImage.stbi_image_free(decodedImage);

        } catch (Exception e) {
            logger.error("An error occurred while setting the window icon for: " + iconPath, e);
        }
    }

    public boolean isFocused() {
        // glfwGetWindowAttrib returns GLFW_TRUE (1) if focused, GLFW_FALSE (0) otherwise
        return glfwGetWindowAttrib(window, GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }
}