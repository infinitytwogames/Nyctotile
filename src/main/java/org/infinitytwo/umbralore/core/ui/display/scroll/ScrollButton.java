package org.infinitytwo.umbralore.core.ui.display.scroll;

import org.infinitytwo.umbralore.core.constants.Constants;
import org.infinitytwo.umbralore.core.event.SubscribeEvent;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseCoordinatesEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.UI;
import org.infinitytwo.umbralore.core.ui.display.Screen;
import org.infinitytwo.umbralore.core.ui.input.Button;

import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class ScrollButton extends Button {
    protected final ScrollableMenu menu;
    protected boolean hold;
    
    public ScrollButton(Screen screen, ScrollableMenu menu) {
        super(screen, Path.of(Constants.fontFilePath), "");
        this.menu = menu;
        
        EventBus.connect(this);
    }
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        hold = e.action != GLFW_RELEASE;
    }
    
    @SubscribeEvent
    public void onMouseMovement(MouseCoordinatesEvent e) {
    
    }
}
