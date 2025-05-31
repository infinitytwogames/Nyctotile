package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.Main;
import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.render.TextBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;

import static org.lwjgl.opengl.GL11.*;

public class Label extends UI {
    private final Logger logger = new Logger("Label");
    private int fontSize;
    private final Window window;

    private String text;
    private TextBatchRenderer renderer;
    private RGB textColor;
    private boolean isCentered;
    private Vector2D<Integer> pos;

    public Label(Window window, UIVector2Df position, String text, boolean isCentered, RGB color, float width, float height, RGBA background) {
        super(Main.getUIBatchRenderer(), "Label", position, width, height, background);
        this.text = text;
        this.window = window;
        textColor = color;
        this.isCentered = isCentered;
        init();
    }

    @Override
    public void init() {
        renderer = Main.getFontBatchRenderer();
        fontSize = (int) renderer.getFontRenderer().getFontHeight();
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

    public Vector2D<Integer> getTextPosition() {
        return pos;
    }

    @Override
    public void draw() {
        if (isCentered) pos = AdvancedMath.calculateCenteredTextPosition(new UIVector2Df(getPosition()),getWidth(),getHeight(), getFontRenderer(),text,fontSize);
        else {
            float centerXNDC = getPosition().getX() / 2;
        }
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        super.draw();
        getUIBatchRenderer().flush();
        renderer.getFontRenderer().renderText(AdvancedMath.createScaledProjection(window.getWidth(),window.getHeight()),text,pos,textColor);
        getUIBatchRenderer().begin();

    }
}
