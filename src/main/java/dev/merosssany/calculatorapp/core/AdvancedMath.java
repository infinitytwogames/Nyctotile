package dev.merosssany.calculatorapp.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;

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

    public static boolean isPointWithinRectangle(Vector2f topRight, Vector2f point, Vector2i bottomLeft) {
        float minX = bottomLeft.x();
        float maxX = topRight.x();
        float minY = topRight.y();
        float maxY = bottomLeft.y();

        float targetX = point.x();
        float targetY = point.y();

        return targetX >= minX && targetX <= maxX &&
                targetY >= minY && targetY <= maxY;
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