package dev.merosssany.calculatorapp.core.constants;

import dev.merosssany.calculatorapp.core.render.Window;
import org.joml.Matrix4f;

public abstract class Constants {
    public static final String fontFilePath = "src/main/resources/fonts/Main.ttf";

    public static Matrix4f getTextProjectionMatrix(Window window) {
         return new Matrix4f().ortho2D(0, window.getWidth(), window.getHeight(), 0);
    }

    public static Matrix4f getTextProjectionMatrix(int width, int height) {
        return new Matrix4f().ortho2D(0, width, height, 0);
    }
}
