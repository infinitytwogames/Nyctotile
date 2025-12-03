package org.infinitytwo.nyctotile.core.data;

import org.joml.Vector3i;

public class Light {
    protected RGBA color;
    protected Vector3i position = new Vector3i();
    protected int level;
    
    public Light(RGBA color, Vector3i position, int level) {
        this.color = color;
        this.position.set(position);
        this.level = level;
    }
    
    public Light(Vector3i position, int level) {
        this.position = position;
        this.level = level;
    }
    
    public RGBA getColor() {
        return color;
    }
    
    public void setColor(RGBA color) {
        this.color = color;
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
        this.level = level;
    }
}
