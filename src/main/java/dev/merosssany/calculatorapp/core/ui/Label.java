package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.Main;
import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.constants.Constants;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.render.Window;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;

public class Label extends UI {
    private final Logger logger = new Logger("Label");
    private int fontSize;
    private final Window window;

    private String text;
    private FontRenderer renderer;
    private RGB textColor;
    private boolean isCentered;
    private Vector2D<Integer> pos;

    public Label(Window window, UIVector2Df position, String text, boolean isCentered, RGB color, float width, float height, RGBA background) {
        super(Main.getUIBatchRenderer(), "Label", position, width, height, background);
        this.text = text;
        this.window = window;
        textColor = color;
        this.isCentered = isCentered;
    }

    @Override
    public void init() {
        super.init();
        renderer = Main.getFontRenderer();
        fontSize = (int) renderer.getFontHeight();
    }

    @Override
    public void draw() {
        super.draw();

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RGB getTextColor() {
        return textColor;
    }

    public void setTextColor(RGB textColor) {
        this.textColor = textColor;
    }

    public Window getWindow() {
        return window;
    }

    public FontRenderer getFontRenderer() {
        return renderer;
    }

    public boolean isCentered() {
        return isCentered;
    }

    public void setCentered(boolean centered) {
        isCentered = centered;
    }

    public Vector2D<Integer> getTextPosition() {
        return pos;
    }
}
