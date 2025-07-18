package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.Main;
import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.render.TextBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;

public class Label extends UI {
    private final Logger logger = new Logger("Label");
    private int fontSize;
    private final Window window;

    private String text;
    private TextBatchRenderer renderer;
    private RGB textColor;
    private boolean isCentered;
    private Vector2i pos;

    public Label(Window window, Vector2i position, String text, boolean isCentered, RGB color, float width, float height, RGBA background) {
//        super(Main.getUIBatchRenderer(), "Label", position, width, height, background);
        this.text = text;
        this.window = window;
        textColor = color;
        this.isCentered = isCentered;
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
        return renderer.getFontRenderer();
    }

    public TextBatchRenderer getTextBatchRenderer() {
        return renderer;
    }

    public boolean isCentered() {
        return isCentered;
    }

    public void setCentered(boolean centered) {
        isCentered = centered;
    }

    public Vector2i getTextPosition() {
        return pos;
    }
}
