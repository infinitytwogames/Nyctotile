package org.infinitytwo.nyctotile.core.data;

import org.joml.Vector3i;

import static org.joml.Math.clamp;

public class Light {
    protected RGB color = new RGB();
    protected Vector3i position = new Vector3i();
    protected int level;
    
    public Light(RGBA color, Vector3i position, int level) {
        this.color.set(color);
        this.position.set(position);
        this.level = level;
    }
    
    public Light(Vector3i position, int level) {
        this.position = position;
        this.level = level;
    }
    
    public Light(int r, int g, int b, int level) {
        r(r); g(g); b(b); level(level);
    }
    
    public Light(Vector3i pos, int red, int green, int blue, int lightLevel) {
        position.set(pos); r(red); g(green); b(blue); level(lightLevel);
    }
    
    public Light() {
        level = 0;
    }
    
    public RGB getColor() {
        return color;
    }
    
    public Vector3i getPosition() {
        return position;
    }
    
    public void setPosition(Vector3i position) {
        this.position.set(position);
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = clamp(0,15,level);
    }
    
    public void setRed(int red) {
        color.setRed((float) red / 255);
    }
    
    public int getRed() {
        return (int) color.getRed() * 255;
    }
    
    public int getGreen() {
        return (int) color.getGreen() * 255;
    }
    
    public void setGreen(int green) {
        color.setGreen((float) green / 255);
    }
    
    public int getBlue() {
        return (int) color.getBlue() * 255;
    }
    
    public void setBlue(int blue) {
        color.setBlue((float) blue / 255);
    }
    
    public RGB setColor(int r, int g, int b) {
        return color.set((float) r /255, (float) g /255, (float) b /255);
    }
    
    public int r() {
        return getRed();
    }
    
    public int g() {
        return getGreen();
    }
    
    public void r(int r) {
        setRed(r);
    }
    
    public void b(int b) {
        setBlue(b);
    }
    
    public int b() {
        return getBlue();
    }
    
    public void g(int g) {
        setGreen(g);
    }
    
    public void setColor(RGB color) {
        this.color.set(color);
    }
    
    public int level() {
        return getLevel();
    }
    
    public void level(int a) {
        setLevel(a);
    }
    
    public void set(Light light) {
        setColor(light.getColor());
        setLevel(light.getLevel());
        setPosition(light.getPosition());
    }
}
