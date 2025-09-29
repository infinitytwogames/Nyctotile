package org.infinitytwo.umbralore;

import org.lwjgl.glfw.GLFW;

public class Mouse {
    private double lastX, lastY;
    private double deltaX, deltaY;
    private boolean first = true;

    public void update(long windowHandle) {
        double[] x = new double[1];
        double[] y = new double[1];
        GLFW.glfwGetCursorPos(windowHandle, x, y);

        if (first) {
            lastX = x[0];
            lastY = y[0];
            first = false;
        }

        deltaX = x[0] - lastX;
        deltaY = y[0] - lastY;

        lastX = x[0];
        lastY = y[0];
    }

    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
}
