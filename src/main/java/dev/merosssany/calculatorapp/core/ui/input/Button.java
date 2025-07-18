package dev.merosssany.calculatorapp.core.ui.input;

import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.constants.Constants;
import dev.merosssany.calculatorapp.core.event.input.MouseButtonEvent;
import dev.merosssany.calculatorapp.core.event.input.MouseHoverEvent;
import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.ui.InteractableUI;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class Button extends InteractableUI {
    private String text;
    private final int fontSize;
    private final float padding;
    private FontRenderer renderer;
    private final String path;
    private final Runnable onClick;
    private RGB textColor;
    private final Logger logger = new Logger("Button");

    public Button(UIBatchRenderer renderer, String text, RGB textColor, int fontSize, String fontFilePath, float padding, Runnable onClick, Vector2i position, float width, float height, RGBA background, Window window) {
        super(window);
        this.text = text;
        this.fontSize = fontSize;
        this.padding = padding;
        this.path = fontFilePath;
        this.textColor = textColor;
        this.onClick = onClick;
    }

    public void click(int action) {
        if (action == GLFW.GLFW_RELEASE) {
            logger.info("Click");
            onClick.run();
            onMouseHover();
        } else {
            RGBA s = getCurrentBackgroundColor();
            setBackgroundColor(s.getRed() - 0.3f,s.getGreen() - 0.3f,s.getBlue() -0.3f, s.getAlpha());
        }
    }

    @Override
    public void onMouseRightClick(int action) {
        click(action);
    }

    @Override
    public void onMouseLeftClick(int action) {
        click(action);
    }

    @Override
    public void onMouseHover() {
        logger.info("Hover");
        RGBA original = getBackgroundColor();
        setBackgroundColor(
                original.getRed() - 0.3f,
                original.getGreen() - 0.3f,
                original.getBlue() - 0.3f,
                original.getAlpha()
        );
    }

    @Override
    public void onMouseHoverEnded() {
        setBackgroundColor(getBackgroundColor());
    }

    @Override
    public void init() {
        super.init();
        renderer = new FontRenderer(path,fontSize);
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

    @SubscribeEvent
    public void onHover(MouseHoverEvent e) {
        super.mouseHoverEvent(e); // DO NOT REMOVE! CRIRICAL
    }

    @SubscribeEvent
    public void onClick(MouseButtonEvent e) {
        super.onEventFired(e);
    }
}