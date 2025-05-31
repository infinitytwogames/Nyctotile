package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.intervals.GLFWBlinkingInterval;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.intervals.Interval;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;

public class Cursor extends UI {
    private Interval interval;

    public Cursor(UIBatchRenderer renderer, UIVector2Df position, float width, float height, RGBA background) {
        super(renderer, "Cursor", position, width, height, background);
    }

    public Cursor(UIVector2Df position, float height, RGBA background) {
        super("Cursor", position, 0.05f, height, background);
    }

    @Override
    public void init() {

    }
}
