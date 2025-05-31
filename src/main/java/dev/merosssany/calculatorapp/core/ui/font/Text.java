package dev.merosssany.calculatorapp.core.ui.font;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.position.Vector2D;

public class Text {
    public String text;
    public Vector2D<Integer> position;

    public Text(String text, RGB color, Vector2D<Integer> position) {
        this.text = text;
        this.position = position;
    }
}
