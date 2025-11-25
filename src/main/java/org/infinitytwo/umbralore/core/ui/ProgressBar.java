package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.RGB;
import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.core.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.ui.animations.UpdatableUI;
import org.infinitytwo.umbralore.core.ui.builder.RectangleBuilder;
import org.infinitytwo.umbralore.core.ui.builder.UIBuilder;
import org.infinitytwo.umbralore.core.ui.builtin.Rectangle;
import org.infinitytwo.umbralore.core.ui.display.Scene;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;

import java.util.concurrent.atomic.AtomicInteger;

import static org.joml.Math.clamp;
import static org.joml.Math.lerp;

public class ProgressBar extends UpdatableUI {
    protected AtomicInteger total = new AtomicInteger(100);
    protected AtomicInteger current = new AtomicInteger(0);
    protected Rectangle bar;

    protected int percentage = 0;
    protected float speed = 10;

    public ProgressBar(Scene renderer, FontRenderer textRenderer, RGB text, int max) {
        super(renderer.getUIBatchRenderer());
        bar = new Rectangle(renderer.getUIBatchRenderer());
        total.set(max);

        bar.setBackgroundColor(new RGBA(1,0,1f,1));
        bar.setPosition(new Anchor(0,0.5f), new Pivot(0,0.5f));
        bar.setParent(this);
    }

    public static ProgressBarBuilder builder(Scene renderer, FontRenderer textRenderer, RGB text) {
        return new ProgressBarBuilder(renderer,textRenderer,text);
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

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setCurrent(int current) {
        if (current <= total.get()) this.current.set(current);
        else this.current.set(total.get());
    }

    public void incrementCurrent() {
        if (current.get() +1 > total.get()) return;
        current.incrementAndGet();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        bar.setHeight(height);
    }

    @Override
    public void draw() {
        super.draw();
        bar.draw();
    }

    @Override
    public void onMouseClicked(MouseButtonEvent e) {}

    @Override
    public void onMouseHover(MouseHoverEvent e) {}

    @Override
    public void onMouseHoverEnded() {}

    @Override
    public void cleanup() {}

    @Override
    public void update(float delta) {
        percentage = (int)(((float) current.get() / total.get()) * 100);
        float targetWidth = width * ((float) current.get() / total.get());
        bar.setWidth((int) clamp(lerp(bar.getWidth(), targetWidth,delta * speed),0,width));
    }

    public static class ProgressBarBuilder extends UIBuilder<ProgressBar> {
        public ProgressBarBuilder(Scene renderer, FontRenderer fontRenderer, RGB color) {
            super(new ProgressBar(renderer, fontRenderer, color, 10));
        }

        public ProgressBarBuilder max(int max) {
            ui.setTotal(max);
            return this;
        }

        public ProgressBarBuilder current(int current) {
            ui.setCurrent(current);
            return this;
        }

        public ProgressBarBuilder bar(RectangleBuilder builder) {
            ui.bar = builder.build();
            return this;
        }

        @Override
        public ProgressBarBuilder applyDefault() {
            return this;
        }
    }
}

