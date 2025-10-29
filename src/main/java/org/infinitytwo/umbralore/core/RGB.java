package org.infinitytwo.umbralore.core;

public class RGB {
    public float red;
    public float green;
    public float blue;

    public RGB(float red, float green, float blue) {
        this.blue = blue;
        this.green = green;
        this.red = red;
    }

    public RGB() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public RGB(RGBA color) {
        red = color.red;
        green = color.green;
        blue = color.blue;
    }

    public RGB add(float num) {
        this.red = red + num;
        this.green = green + num;
        this.blue = blue + num;
        return this;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float r() {
        return getRed();
    }

    public float g() {
        return getGreen();
    }

    public float b() {
        return getBlue();
    }

    public void r(int r) {
        setRed(r);
    }

    public void g(int g) {
        setGreen(g);
    }

    public void b(int b) {
        setBlue(b);
    }

    public void set(float r, float g, float b) {
        this.red = r;
        this.blue = b;
        this.green = g;
    }
    
    // Inside the RGB class
    /**
     * Calculates the perceived luminosity (brightness) of this color and returns
     * either pure black (0, 0, 0) or pure white (1, 1, 1) for maximum contrast.
     * <p>
     * The standard formula weights green highest, then red, then blue.
     * The threshold is typically half the max luminosity.
     *
     * @return RGB color (Black or White) with max contrast.
     */
    public RGB getContrastColor() {
        // 1. Calculate Relative Luminance (Luminosity)
        // Formula: L = 0.2126 * R + 0.7152 * G + 0.0722 * B
        // These coefficients account for human eye sensitivity.
        float luminosity = (0.2126f * this.red) +
                (0.7152f * this.green) +
                (0.0722f * this.blue);
        
        // 2. Determine the contrast color based on a mid-luminosity threshold.
        // A standard threshold for 0.0-1.0 scale is 0.5, but using the actual
        // Luminosity (L) directly as a threshold is more accurate for perceptually
        // dark/light. We'll use 0.5 as a simple threshold.
        float threshold = 0.5f;
        
        if (luminosity < threshold) {
            // The Background is dark, return White (1.0, 1.0, 1.0)
            return new RGB(1.0f, 1.0f, 1.0f);
        } else {
            // Background is light, return Black (0.0, 0.0, 0.0)
            return new RGB(0.0f, 0.0f, 0.0f);
        }
    }
}