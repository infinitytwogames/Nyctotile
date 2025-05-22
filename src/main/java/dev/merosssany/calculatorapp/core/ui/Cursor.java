package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.BlinkingInterval;
import dev.merosssany.calculatorapp.core.Interval;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;

public class Cursor extends UI {
    private final BlinkingInterval interval = new BlinkingInterval(700,500, super::draw);

    public Cursor(String name, UIVector2Df position, float height, RGBA background) {
        super(name, position, 0.2f, height, background);
    }

    @Override
    public void draw() {
        interval.update();
    }

    public void animate() {
        interval.start();
    }

    public void stop() {
        interval.end();
    }
}
