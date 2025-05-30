package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.GLFWBlinkingInterval;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;

public class Cursor extends UI {
    private final GLFWBlinkingInterval interval;

    public Cursor(UIBatchRenderer screen, UIVector2Df position, float height, RGBA background) {
        super(screen,"Cursor", position, 0.01f, height, background);
        interval = new GLFWBlinkingInterval(this::draw,500,400);
    }

    public void start() {
        interval.start();
    }

    public void stop() {
        interval.end();
    }
}
