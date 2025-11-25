package org.infinitytwo.umbralore.core.ui.display;

import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.VectorMath;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.UI;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public abstract class BasicButton extends UI {
    protected RGBA original = backgroundColor.copy();
    
    public BasicButton(UIBatchRenderer renderer) {
        super(renderer);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (e.action == GLFW_RELEASE) {
            if (VectorMath.isPointWithinRectangle(getPosition(),e.x,e.y, getEndPoint())) {
                clicked(e);
            }
        }
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
        super.setBackgroundColor(
                original.r() - 0.25f,
                original.g() - 0.25f,
                original.b() - 0.25f,
                original.a()
        );
    }
    
    @Override
    public void onMouseHoverEnded() {
        backgroundColor.set(original);
    }
    
    @Override
    public void cleanup() {
    
    }
    
    public abstract void clicked(MouseButtonEvent e);
}
