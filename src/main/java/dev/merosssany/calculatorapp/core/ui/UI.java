package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import org.lwjgl.assimp.AIVector2D;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class UI {
    private UIVector2Df position;
    private float width;
    private float height;
    private RGB background;

    public UI(UIVector2Df position,float width, float height, RGB background) {
        this.height = height;
        this.width = width;
        this.position = position;
        this.background = background;
    }

    public Vector2D<Float> getPosition() {
        return position;
    }

    public void setPosition(UIVector2Df position) {
        this.position = position;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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
}
