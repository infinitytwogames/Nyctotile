package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class UI {
    private UIVector2Df position;
    private final float width;
    private final float height;
    private RGB background;
    private Vector2D<Float> end;
    private final Logger logger = new Logger("UI Handler");

    public void setBackgroundColor(RGB color) {
        background = color;
    }

    public UI(UIVector2Df position,float width, float height, RGB background) {
        this.height = height;
        this.width = width;
        this.position = position;
        this.background = background;

        float topLeftX = position.getX();
        float topLeftY = position.getY();

        this.end = new Vector2D<>(topLeftX + width, topLeftY - height);
    }

    public Vector2D<Float> getPosition() {
        return position;
    }

    public void setPosition(UIVector2Df position) {
        this.position = position;
        float topLeftX = position.getX();
        float topLeftY = position.getY();

        this.end = new Vector2D<>(topLeftX + width, topLeftY - height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void draw(int windowWidth, int windowHeight) {
        if (background != null) {
            glColor3f(background.getRed(), background.getGreen(), background.getBlue());
        } else {
            glColor3f(1.0f, 1.0f, 1.0f);
        }

        float topLeftX = position.getX();
        float topLeftY = position.getY();
        float widthNDC = width;   // Assuming width is in NDC scale
        float heightNDC = height; // Assuming height is in NDC scale

        glBegin(GL_QUADS);
        glVertex2f(topLeftX, topLeftY);             // Top-left
        glVertex2f(topLeftX + widthNDC, topLeftY);      // Top-right
        glVertex2f(topLeftX + widthNDC, topLeftY - heightNDC); // Bottom-right (assuming +Y is up in NDC)
        glVertex2f(topLeftX, topLeftY - heightNDC);      // Bottom-left
        glEnd();
    }

    public Vector2D<Float> getEnd() {
        return end;
    }

    public Logger getLogger() {
        return logger;
    }
}
