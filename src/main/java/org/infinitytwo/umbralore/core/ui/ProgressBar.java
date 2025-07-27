package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.RGB;
import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.ui.builtin.Rectangle;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgressBar extends Label {
    protected AtomicInteger total = new AtomicInteger(100);
    protected AtomicInteger current = new AtomicInteger(0);
    protected Rectangle bar;

    protected int percentage = 0;

    public ProgressBar(Screen renderer, FontRenderer textRenderer, RGB text, int max) {
        super(renderer,textRenderer,text);
        bar = new Rectangle(renderer.getUIBatchRenderer(),"Bar");
        total.set(max);

        setText("0%");
        setTextPosition(new Anchor(0.5f,0.5f), new Pivot(0.5f,0.5f), new Vector2i());

        bar.setBackgroundColor(new RGBA(1,0,1f,1));
        bar.setPosition(new Anchor(0,0.5f), new Pivot(0,0.5f));
        bar.setParent(this);
    }

    public int getTotal() {
        return total.get();
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public int getCurrent() {
        return current.get();
    }

    public void setCurrent(int current) {
        if (current <= total.get()) this.current.set(current);
        else this.current.set(total.get());
    }

    public void incrementCurrent() {
        if (current.get() +1 > total.get()) return;
        current.incrementAndGet();
    }

    public void update() {
        percentage = (int)(((float) current.get() / total.get()) * 100);
        setText(percentage+"%");
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        bar.setHeight(height);
    }

    @Override
    public void draw() {
        update();
        bar.setWidth((int) (width * (percentage /100f)));
        super.draw();
        bar.draw();
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
