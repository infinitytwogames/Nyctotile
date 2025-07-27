package org.infinitytwo.umbralore.core;

import org.joml.Matrix4f;
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

    public static boolean isPointWithinRectangle(Vector2i topLeft, Vector2i point, Vector2i bottomRight) {
        int minX = topLeft.x();
        int maxX = bottomRight.x();
        int minY = topLeft.y();
        int maxY = bottomRight.y();

        int x = point.x();
        int y = point.y();

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public static boolean isPointWithinRectangle(Vector2i topRight, int pointX, int pointY, Vector2i bottomLeft) {
        float minX = bottomLeft.x();
        float maxX = topRight.x();
        float minY = topRight.y();
        float maxY = bottomLeft.y();

        return (float) pointX >= minX && (float) pointX <= maxX &&
                (float) pointY >= minY && (float) pointY <= maxY;
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

    public static Vector2i transformVirtualToWindow(Window window, int virtualX, int virtualY) {
        float scale = (float) window.getHeight() / Display.height;

        int screenX = (int) (virtualX * scale);
        int screenY = (int) (virtualY * scale);
        return new Vector2i(screenX, screenY);
    }

    public static Vector2i transformVirtualToWindow(Window window, Vector2i pos) {
        float scale = (float) window.getHeight() / Display.height;

        int screenX = (int) (pos.x * scale);
        int screenY = (int) (pos.y * scale);
        return new Vector2i(screenX, screenY);
    }

    public static Vector2i transformWindowToVirtual(Window window, int windowX, int windowY) {
        float scale = (float) Display.height / window.getHeight();

        int virtualX = (int) (windowX * scale);
        int virtualY = (int) (windowY * scale);

        return new Vector2i(virtualX, virtualY);
    }

    public static Vector2i transformWindowToVirtual(Window window, Vector2i pos) {
        float scale = (float) Display.height / window.getHeight();

        int virtualX = (int) (pos.x * scale);
        int virtualY = (int) (pos.y * scale);

        return new Vector2i(virtualX, virtualY);
    }

}