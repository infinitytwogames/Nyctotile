package dev.merosssany.calculatorapp.core.ui;

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
    private final String filePath;
    private final int fontSize;
    private final Window window;

    private String text;
    private FontRenderer renderer;
    private RGB textColor;

    public Label(Window window, UIVector2Df position, String text, int fontSize, RGB color, String fontFilePath, float width, float height, RGBA background) {
        super("Label", position, width, height, background);
        this.text = text;
        this.fontSize = fontSize;
        this.filePath = fontFilePath;
        this.window = window;
        textColor = color;
    }

    @Override
    public void init() {
        super.init();
        renderer = new FontRenderer(filePath,fontSize);
    }

    @Override
    public void draw() {
        super.draw();
        Vector2D<Integer> pos = AdvancedMath.calculateCenteredTextPosition(new UIVector2Df(getPosition()),getWidth(),getHeight(),window,renderer,text,fontSize);
        renderer.renderText(Constants.getTextProjectionMatrix(window),text,pos,textColor);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        renderer.cleanup();
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
}
