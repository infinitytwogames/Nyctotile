package dev.merosssany.calculatorapp.core;

import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public abstract class AdvancedMath {
    public static final float VIRTUAL_WIDTH = 1920f;
    public static final float VIRTUAL_HEIGHT = 1080f;

    // Clamp
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    public static short clamp(short value, short min, short max) {
        return (short) Math.max(min, Math.min(value, max));
    }

    // Lerp (Linear Interpolation)
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    // Inverse Lerp
    public static float inverseLerp(float a, float b, float value) {
        return (value - a) / (b - a);
    }

    public static double inverseLerp(double a, double b, double value) {
        return (value - a) / (b - a);
    }

    // Map (Remap a value from one range to another)
    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((value - inMin) * (outMax - outMin)) / (inMax - inMin);
    }

    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return outMin + ((value - inMin) * (outMax - outMin)) / (inMax - inMin);
    }

    // Normalize (value to 0..1 based on a range)
    public static float normalize(float value, float min, float max) {
        return clamp((value - min) / (max - min), 0f, 1f);
    }

    public static double normalize(double value, double min, double max) {
        return clamp((value - min) / (max - min), 0.0, 1.0);
    }

    // Snap to nearest step
    public static float snap(float value, float step) {
        return Math.round(value / step) * step;
    }

    public static double snap(double value, double step) {
        return Math.round(value / step) * step;
    }

    // Scale
    public static float scale(float a, float b) {
        return a * b;
    }

    public static double scale(double a, double b) {
        return a * b;
    }

    // Round to N decimal places
    public static float round(float value, int places) {
        float scale = (float) Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static <T extends Number & Comparable<T>> boolean isVectorPointIncludedIn(Vector2D<T> topRight, Vector2D<T> point, Vector2D<T> bottomLeft) {
        float minX = topRight.getX().floatValue();
        float maxX = bottomLeft.getX().floatValue();
        float minY = bottomLeft.getY().floatValue(); // Bottom boundary (assuming UI y increases downwards or is already flipped)
        float maxY = topRight.getY().floatValue(); // Top boundary (assuming UI y increases downwards or is already flipped)

        float targetX = point.getX().floatValue();
        float targetY = point.getY().floatValue();

        return targetX >= minX && targetX <= maxX &&
                targetY <= maxY && targetY >= minY; // Adjusted Y comparisons
    }

    public static <T extends Number & Comparable<T>> boolean isVectorPointIncludedAround(Vector2D<T> topRight, Vector2D<T> point, Vector2D<T> bottomLeft) {
        T minX = (point.getX().compareTo(bottomLeft.getX()) < 0) ? point.getX() : bottomLeft.getX();
        T maxX = (point.getX().compareTo(bottomLeft.getX()) > 0) ? point.getX() : bottomLeft.getX();
        T minY = (point.getY().compareTo(bottomLeft.getY()) < 0) ? point.getY() : bottomLeft.getY();
        T maxY = (point.getY().compareTo(bottomLeft.getY()) > 0) ? point.getY() : bottomLeft.getY();

        return topRight.getX().compareTo(minX) > 0 && topRight.getX().compareTo(maxX) < 0 &&
                topRight.getY().compareTo(minY) > 0 && topRight.getY().compareTo(maxY) < 0;
    }

    /**
     * Creates a 2D translation matrix.
     *
     * @param tx The translation distance along the x-axis.
     * @param ty The translation distance along the y-axis.
     * @return A 4x4 translation matrix.
     */
    public static Matrix4f translate2D(float tx, float ty) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(tx, ty, 0);
        return matrix;
    }

    /**
     * Creates a 2D scaling matrix.
     *
     * @param sx The scaling factor along the x-axis.
     * @param sy The scaling factor along the y-axis.
     * @return A 4x4 scaling matrix.
     */
    public static Matrix4f scale2D(float sx, float sy) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.scale(sx, sy, 1);
        return matrix;
    }

    /**
     * Creates a 2D rotation matrix (around the Z-axis).
     *
     * @param angle The rotation angle in radians.
     * @return A 4x4 rotation matrix.
     */
    public static Matrix4f rotate2D(float angle) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate(angle, 0, 0, 1);
        return matrix;
    }

    /**
     * Creates a 2D orthographic projection matrix.
     *
     * @param left The left edge of the projection.
     * @param right The right edge of the projection.
     * @param bottom The bottom edge of the projection.
     * @param top The top edge of the projection.
     * @return A 4x4 orthographic projection matrix.
     */
    public static Matrix4f orthographicProjection2D(float left, float right, float bottom, float top) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.setOrtho(left, right, bottom, top, -1, 1); // zNear = -1, zFar = 1
        return matrix;
    }

    /**
     * Calculates the combined model matrix from translation, scaling, and rotation.
     *
     * @param translationMatrix The translation matrix.
     * @param scalingMatrix     The scaling matrix.
     * @param rotationMatrix      The rotation matrix.
     * @return A 4x4 model matrix representing the combined transformation (Scale -> Rotate -> Translate).
     */
    public static Matrix4f calculateModelMatrix(Matrix4f translationMatrix, Matrix4f scalingMatrix, Matrix4f rotationMatrix) {
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();
        modelMatrix.mul(scalingMatrix);
        modelMatrix.mul(rotationMatrix);
        modelMatrix.mul(translationMatrix);
        return modelMatrix;
    }

    public static Vector2D<Integer> ndcToPixel(float ndcX, float ndcY) {

        // NDC to Normalized (0 to 1)
        float normalizedX = (ndcX + 1.0f) / 2.0f;
        float normalizedY = (1.0f - ndcY) / 2.0f; // <--- THIS LINE IS CRUCIAL FOR Y-INVERSION

        // Normalized (0 to 1) to Pixel
        int pixelX = (int) (normalizedX * (int) VIRTUAL_WIDTH);
        int pixelY = (int) (normalizedY * (int) VIRTUAL_HEIGHT);

        return new Vector2D<>(pixelX, pixelY);
    }

    public static UIVector2Df pixelToNdc(int pixelX, int pixelY, Window window) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        // Pixel to Normalized (0 to 1)
        float normalizedX = (float) pixelX / windowWidth;
        float normalizedY = (float) pixelY / windowHeight;

        // Normalized (0 to 1) to NDC
        // Reverse: ndcX = (2.0f * normalizedX) - 1.0f
        float ndcX = (2.0f * normalizedX) - 1.0f;

        // Reverse: normalizedY = (1.0f - ndcY) / 2.0f
        // 2.0f * normalizedY = 1.0f - ndcY
        // ndcY = 1.0f - (2.0f * normalizedY)
        float ndcY = 1.0f - (2.0f * normalizedY);

        return new UIVector2Df(ndcX, ndcY);
    }

    public static float pixelToNdc(int pixel, Window window) {
        return pixelToNdc(pixel,0,window).getX();
    }

    public static UIVector2Df pixelToNdc(Vector2D<Integer> positionPixel, Window window) {
        return pixelToNdc(positionPixel.getX(),positionPixel.getY(),window);
    }

    public static Vector2D<Integer> ndcToPixel(Vector2D<Float> vector2D) {
        return ndcToPixel(vector2D.getX(),vector2D.getY());
    }

    public static Vector2D<Integer> ndcToPixel(UIVector2Df vector2Df) {
        return ndcToPixel(vector2Df.getX(),vector2Df.getY());
    }

    /**
     * Calculates the top-left pixel position for text to be centered within a UI element.
     *
     * @param uiPosition The top-left NDC position of the UI element.
     * @param uiWidth The width of the UI element in NDC.
     * @param uiHeight The height of the UI element in NDC.
     * @param fontRenderer The FontRenderer instance to measure text dimensions.
     * @param text The string to be rendered.
     * @param fontSize The font size used, for specific vertical adjustments.
     * @return A Vector2D<Integer> representing the top-left pixel coordinates where the text should start.
     */
    public static Vector2D<Integer> calculateCenteredTextPosition(
            UIVector2Df uiPosition,
            float uiWidth,
            float uiHeight,
            FontRenderer fontRenderer,
            String text,
            int fontSize
    ) {
        // 1. Get the UI element's center in Normalized Device Coordinates (NDC)
        // Assuming uiPosition.getY() is the top edge, and uiHeight extends downwards in UI space
        float uiCenterX_NDC = uiPosition.getX() + uiWidth / 2.0f;
        float uiCenterY_NDC = uiPosition.getY() - uiHeight / 2.0f; // Subtract half height to get center Y

        // 2. Convert UI element center from NDC to pixel coordinates
        Vector2D<Integer> uiCenterPixels = ndcToPixel(
                uiCenterX_NDC,
                uiCenterY_NDC
        );

        // 3. Get text dimensions in pixels using the provided FontRenderer
        float textWidthPixels = fontRenderer.getStringWidth(text);
        float textHeightPixels = fontRenderer.getFontHeight(); // Ensure FontRenderer has this method

        // 4. Calculate the top-left pixel position for the text to be centered
        int textXPixels = (int) (uiCenterPixels.getX() - textWidthPixels / 4);
        int textYPixels = (int) (uiCenterPixels.getY() + textHeightPixels / 2.0f); // shift baseline down

        return new Vector2D<>(textXPixels, textYPixels);

    }

    public static boolean isInRange(Vector2D<?> top_left, Vector2D<?> bottom_right, Window window) {
        double[] cursorPositionX = new double[1];
        double[] cursorPositionY = new double[1];
        GLFW.glfwGetCursorPos(window.getWindow(), cursorPositionX, cursorPositionY);
        double mouseX = cursorPositionX[0];
        double mouseY = cursorPositionY[0];

        int windowWidth = window.getWidth(); // Assuming you have a getWidth() method in your Window class
        int windowHeight = window.getHeight(); // Assuming you have a getHeight() method

// Convert mouse X to normalized coordinates (-1 to 1)
        float normalizedMouseX = (float) ((2.0 * mouseX) / windowWidth - 1.0);

// Convert mouse Y to normalized coordinates (1 to -1, assuming UI y-axis goes down)
        float normalizedMouseY = (float) (1.0 - (2.0 * mouseY) / windowHeight);

        Vector2D<Float> normalizedMousePos = new Vector2D<>(normalizedMouseX, normalizedMouseY);
//        logger.log(this.getPosition(),normalizedMousePos, this.getEnd());
        return top_left.isVectorPointIncludedIn(normalizedMousePos, bottom_right);
    }

    public static Matrix4f createVirtualProjection(float virtualWidth, float virtualHeight) {
        return new Matrix4f().ortho(0, virtualWidth, virtualHeight, 0, -1, 1);
    }

    public static Matrix4f createScaledProjection(int windowWidth, int windowHeight) {


        float windowAspect = (float) windowWidth / windowHeight;
        float virtualAspect = VIRTUAL_WIDTH / VIRTUAL_HEIGHT;

        float scale;
        float width, height;

        if (windowAspect > virtualAspect) {
            // Window is wider than virtual — scale based on height
            scale = (float) windowHeight / VIRTUAL_HEIGHT;
            width = windowWidth / scale;
            height = VIRTUAL_HEIGHT;
        } else {
            // Window is taller than virtual — scale based on width
            scale = (float) windowWidth / VIRTUAL_WIDTH;
            width = VIRTUAL_WIDTH;
            height = windowHeight / scale;
        }

        return new Matrix4f().ortho(0, width, height, 0, -1, 1);
    }

    public static Matrix4f createNDCVirtualProjectionCentered(int windowWidth, int windowHeight) {
        float windowAspect = (float) windowWidth / windowHeight;
        float virtualAspect = VIRTUAL_WIDTH / VIRTUAL_HEIGHT;

        float scale;
        float width, height;

        if (windowAspect > virtualAspect) {
            // Window is wider than virtual — scale based on height
            scale = (float) windowHeight / VIRTUAL_HEIGHT;
            width = windowWidth / scale;
            height = VIRTUAL_HEIGHT;
        } else {
            // Window is taller than virtual — scale based on width
            scale = (float) windowWidth / VIRTUAL_WIDTH;
            width = VIRTUAL_WIDTH;
            height = windowHeight / scale;
        }

        // Centered coordinate space: -width/2 to width/2, -height/2 to height/2
        // Map that to NDC [-1,1]
        return new Matrix4f().ortho(
                -width / 2f, width / 2f,
                -height / 2f, height / 2f,
                -1f, 1f
        ).scale(2f / width, 2f / height, 1f);
    }

    public static Matrix4f createVirtualProjectionNDC(float virtualWidth, float virtualHeight) {
        return new Matrix4f().ortho(0f, virtualWidth, virtualHeight, 0f, -1f, 1f);
    }
}