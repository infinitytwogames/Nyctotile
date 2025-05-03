package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.EventBus;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.event.Event;
import dev.merosssany.calculatorapp.core.event.KeyPressEvent;
import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.logging.Logger;
import org.lwjgl.glfw.GLFW;

public class InteractableUI extends UI{
    private final Window window;

    public InteractableUI(UIVector2Df position, float width, float height, RGB background, Window window) {
        super(position, width, height, background);
        this.window = window;

        EventBus.register(this);
    }

    @SubscribeEvent
    public void onEventFired(Event e) {
        if (e instanceof KeyPressEvent) {
            int key = ((KeyPressEvent) e).getKey();
            int action = ((KeyPressEvent) e).getAction();

            if (key == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                double[] cursorPositionX = new double[1];
                double[] cursorPositionY = new double[1];
                GLFW.glfwGetCursorPos(window.getWindow(),cursorPositionX,cursorPositionY);
                float xpos = (float) cursorPositionX[0];
                float ypos = (float) cursorPositionY[0];

                Vector2D<Float> vector2D = new Vector2D<>(xpos,ypos);
                if (this.getPosition().isVectorPointIncludedIn(vector2D,this.getEnd())) {

                }
            }
        }
    }

    public void onMouseRightClick() {
        getLogger().log("Button Pressed");
    }
}
