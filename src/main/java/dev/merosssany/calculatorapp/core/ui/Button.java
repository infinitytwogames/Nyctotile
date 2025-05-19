package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.*;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.position.Vector2Dx2;
import dev.merosssany.calculatorapp.core.ui.font.FontRendererGL;

import java.io.IOException;

import static dev.merosssany.calculatorapp.core.ShaderProgram.load;
import static org.lwjgl.opengl.GL11.*;

public class Button extends InteractableUI {
    private RGBA textColor;
    private ShaderProgram shader;
    private boolean isPressed = false;
    private Runnable mouseClick;
    private String text;
    private final float padding;
    private FontRendererGL fontRenderer;
    private float scaleDownFactor = 0.5f;

    public Button(String text, float scale, RGBA color, UIVector2Df position, float width, float height, float padding, RGBA background, Window window) throws IOException {
        super(position, width, height, background, window);
        this.padding = padding;
        this.text = text;
        this.textColor = color;
        scaleDownFactor = scale;
        initBtn();
    }

    public Button(String text, float scale, RGBA color, Runnable onMouseRightClick, UIVector2Df position, float width, float height, float padding, RGBA background, Window window) throws IOException {
        super(position, width, height, background, window);
        this.padding = padding;
        this.mouseClick = onMouseRightClick;
        this.text = text;
        this.textColor = color;
        scaleDownFactor = scale;
        initBtn();
    }

    public RGBA getTextColor() {
        return textColor;
    }

    public void setTextColor(RGBA textColor) {
        this.textColor = textColor;
    }

    public Button(String text, RGBA color, UIVector2Df position, float width, float height, RGBA background, Window window, ButtonSettings settings) throws IOException {
        super(position, width, height, background, window);
        this.padding = settings.padding;
        this.text = text;
        this.shader = settings.program;
        this.mouseClick = settings.onClick;
        textColor = color;
        initBtn();
    }

    private void initBtn() throws IOException {
        if (shader == null)
            shader = new ShaderProgram(load("assets/font/vertexShader.glsl"), load("assets/font/fragmentShader.glsl"));
        fontRenderer = new FontRendererGL("src/main/resources/fonts/Main.ttf", 48.0f, shader);
    }

    public boolean isPressed() {
        return isPressed;
    }

    @Override
    public void onMouseRightClick() {
        isPressed = true;
        if (mouseClick != null) mouseClick.run();
    }

    @Override
    public void draw() {
        // 2. Enable blending for the text
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 3. Set the text color
        fontRenderer.setColor(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), textColor.getAlpha());

        // Get the text position.
        Vector2Dx2<Float> textScalePosition = calculatePadding();
        float textOpenGLX = textScalePosition.getPoint1().getX();
        float textOpenGLY = textScalePosition.getPoint1().getY();

        float baseScaleY = (1.0f / getWindow().getHeight() * getHeight()) * -5;
        float baseScaleX = -baseScaleY;

        // 4.  Calculate the text's pixel position, relative to the button's position.
        Vector2D<Integer> buttonPixelPosition = AdvancedMath.ndcToPixel(getPosition().getX() -0.40f, getPosition().getY() + 0.35f, getWindow());
        int textX = buttonPixelPosition.getX() + (int) (padding * getWindow().getWidth());
        int textY = buttonPixelPosition.getY() + (int) (padding * getWindow().getHeight());

        // 5. Render the text
//        fontRenderer.renderText(
//                text,
//                textX,
//                textY,
//                baseScaleX * scaleDownFactor,
//                baseScaleY * scaleDownFactor,
//                getWindow()
//        );

        glDisable(GL_BLEND);
        super.draw();
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getPadding() {
        return padding;
    }

    private Vector2Dx2<Float> calculatePadding() {
        Vector2D<Float> position = this.getPosition(); // This is in NDC
        Vector2D<Float> x = new Vector2D<>(
                position.getX() + padding, position.getY() - padding
        );
        Vector2D<Float> y = new Vector2D<>(
                super.getEnd().getX() - padding, super.getEnd().getY() + padding
        );

        return new Vector2Dx2<>(x, y); // This is in NDC
    }

    @Override
    public void cleanup() {
        super.cleanup();
        fontRenderer.cleanup();
    }
}
