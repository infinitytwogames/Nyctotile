package org.infinitytwo.umbralore.core.ui.display.scroll;

import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.ui.UI;
import org.infinitytwo.umbralore.core.ui.display.Screen;

import java.util.ArrayList;

public class ScrollableMenu extends UI {
    protected final ArrayList<UI> uis = new ArrayList<>();
    protected final Screen screen;
    
    public ScrollableMenu(Screen screen) {
        super(screen.getUIBatchRenderer());
        this.screen = screen;
    }
    
    public void addUI(UI ui) {
        screen.register(ui);
        uis.add(ui);
    }
    
    public void removeUI(UI ui) {
        uis.remove(ui);
    }
    
    
    
    @Override
    public void onMouseClicked(MouseButtonEvent e) {
    
    }
    
    @Override
    public void onMouseHover(MouseHoverEvent e) {
    
    }
    
    @Override
    public void onMouseHoverEnded() {
    
    }
    
    @Override
    public void cleanup() {
    
    }
}
