package org.infinitytwo.umbralore.ui.builtin;

import org.infinitytwo.umbralore.constants.Constants;
import org.infinitytwo.umbralore.data.TextComponent;
import org.infinitytwo.umbralore.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.event.state.WindowResizedEvent;
import org.infinitytwo.umbralore.renderer.FontRenderer;
import org.infinitytwo.umbralore.ui.Screen;
import org.infinitytwo.umbralore.ui.UI;
import org.infinitytwo.umbralore.ui.component.Scale;
import org.infinitytwo.umbralore.ui.component.Text;
import org.infinitytwo.umbralore.ui.position.Anchor;
import org.infinitytwo.umbralore.ui.position.Pivot;

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

    public void setText(TextComponent component) { // TextComponent is a prototype of what will I do
        text.setText(component.toString());
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
