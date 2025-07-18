package dev.merosssany.calculatorapp.core.event.input;

import dev.merosssany.calculatorapp.core.event.Event;
import org.joml.Vector2f;

public class MouseHoverEvent extends Event {
    private Vector2f mousePosition;

    public MouseHoverEvent(Vector2f mousePosition) {
        this.mousePosition = mousePosition;
    }

    public MouseHoverEvent(float x, float y) {
        this.mousePosition = new Vector2f(x,y);
    }

    public MouseHoverEvent() {}

    public Vector2f getMousePosition() {
        return mousePosition;
    }


}
