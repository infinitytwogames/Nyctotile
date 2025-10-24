package org.infinitytwo.umbralore.core.ui.builtin;

import org.infinitytwo.umbralore.core.constants.Constants;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.event.state.WindowResizedEvent;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.ui.display.Screen;
import org.infinitytwo.umbralore.core.ui.UI;
import org.infinitytwo.umbralore.core.ui.component.Scale;
import org.infinitytwo.umbralore.core.ui.component.Text;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;

public class Tooltip extends UI {
    protected Text text;
    protected Scale scale = new Scale(0.75f,0.15f);
    protected FontRenderer fontRenderer;

    public Tooltip(Screen screen) {
        super(screen.getUIBatchRenderer());

        fontRenderer = new FontRenderer(Constants.fontFilePath,64);

        setBackgroundColor(0,0,0,0.5f);

        setPosition(new Anchor(0.5f,1), new Pivot(0.5f,1));
        text = new Text(fontRenderer,screen);
        text.setPosition(new Anchor(0.5f,0.5f), new Pivot(0.5f,0.5f));
        text.setParent(this);

        scale.windowResize(new WindowResizedEvent(screen.getWindow()));

    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText();
    }

    @Override
    public void onMouseClicked(MouseButtonEvent e) {

    }

    @Override
    public void onMouseHover(MouseHoverEvent e) { // Btw this refers to this Tooltip not other classes

    }

    @Override
    public void onMouseHoverEnded() {

    }

    @Override
    public void cleanup() {
        fontRenderer.cleanup();
    }

    @Override
    public void draw() {
        setSize(scale.getWidth(),scale.getHeight());
        super.draw();
    }
}
