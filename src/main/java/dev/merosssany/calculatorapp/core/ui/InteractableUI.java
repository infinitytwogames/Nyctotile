package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.event.EventBus;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.event.input.MouseButtonEvent;
import dev.merosssany.calculatorapp.core.event.input.MouseHoverEvent;
import dev.merosssany.calculatorapp.core.render.Window;
import dev.merosssany.calculatorapp.core.event.*;
import dev.merosssany.calculatorapp.core.io.HoverEventRegister;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.logging.Logger;
import org.lwjgl.glfw.GLFW;

public abstract class InteractableUI extends UI{
    private final Window window;
    private final Logger logger = new Logger("Interactable UI");
    private RGBA original;
    private boolean isHovered = false;

    public InteractableUI(UIVector2Df position, float width, float height, RGBA background, Window window) {
        super("Interactable UI", position, width, height, background);
        this.window = window;
        this.original = super.getBackgroundColor();
    }

    @SubscribeEvent
    public void onEventFired(MouseButtonEvent e) {
        if (e != null) {
            int key = e.getButton();
            int action = e.getAction();


                if (isInRange()) {
                    logger.info("CLick");
                    if (key == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        onMouseRightClick(action);
                    } else if (key == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        onMouseLeftClick(action);
                    }
                }

        }
    }
    public abstract void onMouseRightClick(int action);
    public abstract void onMouseLeftClick(int action);
    public abstract void onMouseHover();
    public abstract void onMouseHoverEnded();

    private void darkenBackground() {
        RGBA color = getBackgroundColor();
        setBackgroundColor(color.getRed() - 0.3f,color.getGreen() - 0.3f,color.getBlue() - 0.3f,color.getAlpha());
    }

    public void setOriginalBackgroundColor(RGBA color) {
        this.original = color;
        setBackgroundColor(color);
    }

    public void changeOriginalBackgroundColor(float r, float g, float b, float a) {
        this.original = new RGBA(r,g,b,a);
        setBackgroundColor(this.original);
    }

    private boolean isInRange() {
        double[] cursorPositionX = new double[1];
        double[] cursorPositionY = new double[1];
        GLFW.glfwGetCursorPos(window.getWindow(), cursorPositionX, cursorPositionY);
        double mouseX = cursorPositionX[0];
        double mouseY = cursorPositionY[0];

        int windowWidth = window.getWidth(); // Assuming you have a getWidth() method in your Window class
        int windowHeight = window.getHeight(); // Assuming you have a getHeight() method

// Convert mouse X to normalized coordinates (-1 to 1)
        float normalizedMouseX = (float) ((2.0 * mouseX) / windowWidth - 1.0);

// Convert mouse Y to normalized coordinates (1 to -1, assuming UI y-axis goes down)
        float normalizedMouseY = (float) (1.0 - (2.0 * mouseY) / windowHeight);

        Vector2D<Float> normalizedMousePos = new Vector2D<>(normalizedMouseX, normalizedMouseY);
//        logger.log(this.getPosition(),normalizedMousePos, this.getEnd());
        return this.getPosition().isVectorPointIncludedIn(normalizedMousePos, this.getEnd());
    }

    @SubscribeEvent
    public void mouseHoverEvent(MouseHoverEvent e) {
        if (isInRange()) {
            if (!isHovered) {
                logger.info("Hover");
                isHovered = true;
                onMouseHover();
            }
        } else {
            if (isHovered) {
                logger.info("End Hover");
                isHovered = false;
                onMouseHoverEnded();
            }
        }
    }

    @Override
    public RGBA getBackgroundColor() {
        return original;
    }

    public RGBA getCurrentBackgroundColor() {
        return super.getBackgroundColor();
    }

    @Override
    public void init() {
        super.init();
        HoverEventRegister.registerUI(this);
        EventBus.register(this);
    }

    public Window getWindow() {
        return window;
    }

    public boolean isBeingHovered() {
        return isHovered;
    }
}
