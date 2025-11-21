package org.infinitytwo.umbralore.core;

import org.infinitytwo.umbralore.core.manager.Mouse;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private static boolean locked = true;
    static double delta;
    
    public static void pauseGame(Window window) {
        locked = !locked;
        
        if (locked) {
            // Mouse locked/disabled -> Normal/Visible (Pausing)
            glfwSetInputMode(window.getWindow(),GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            
            // FIX: Clear the delta that might have been generated *before* pausing
        } else {
            // Mouse Normal/Visible -> Locked/Disabled (Resuming)
            glfwSetCursorPos(window.getWindow(), (double) window.getWidth() /2, (double) window.getHeight() /2);
            glfwSetInputMode(window.getWindow(),GLFW_CURSOR,GLFW_CURSOR_DISABLED);
            
            // CRITICAL FIX: Clear the delta generated *by* the cursor mode change
            // to prevent the camera from jumping on the first frame of movement.
        }
        Mouse.setDeltaX(0);
        Mouse.setDeltaY(0);
    }
    
    public static boolean isLocked() {
        return locked;
    }
    
    public static double getDelta() {
        return delta;
    }
}