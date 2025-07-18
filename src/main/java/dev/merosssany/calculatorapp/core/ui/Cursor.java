package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.intervals.Interval;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import org.joml.Vector2i;

public class Cursor extends UI {
    private Interval interval;

    public Cursor(UIBatchRenderer renderer, Vector2i position, float width, float height, RGBA background) {

    }

    public Cursor(Vector2i position, float height, RGBA background) {
//        super("Cursor", position, 0.05f, height, background);
    }
}
