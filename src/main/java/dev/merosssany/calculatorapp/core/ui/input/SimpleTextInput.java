package dev.merosssany.calculatorapp.core.ui.input;

import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.event.Event;
import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.core.event.bus.EventBus;
import dev.merosssany.calculatorapp.core.event.bus.LocalEventBus;
import dev.merosssany.calculatorapp.core.event.input.CharacterInputEvent;
import dev.merosssany.calculatorapp.core.event.input.KeyPressEvent;
import dev.merosssany.calculatorapp.core.event.input.MouseButtonEvent;
import dev.merosssany.calculatorapp.core.intervals.Interval;
import dev.merosssany.calculatorapp.core.render.TextBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.ui.Label;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleTextInput extends Label {
    private final Interval interval;
    private final AtomicBoolean isBlinking;
    private final AtomicBoolean isFocused = new AtomicBoolean(false);
    private final StringBuilder text = new StringBuilder();
    private boolean isSubmitted = false;
    private final LocalEventBus eventBus;
    private int cursorPosition = 0;

    public SimpleTextInput(Window window, Vector2i position, RGB color, float width, float height, RGBA background) {
        super(window, position, "", false, color, width, height, background);
        interval = new Interval(400,this::update);
        eventBus = new LocalEventBus("TextInput");
        isBlinking = new AtomicBoolean(false);
    }

    private void update() {
        if (isFocused.get()) isBlinking.set(!isBlinking.get());
    }

    @Override
    public String getText() {
        return text.toString();
    }

    public void init() {
        interval.start();
        EventBus.register(this);
    }

    @Override
    public void setText(String text) {
        this.text.setLength(0);
        this.text.append(text);
        cursorPosition = text.length();
        updateText();
    }

    @SubscribeEvent
    public void onMouseButtonPressed(MouseButtonEvent e) {
        if (e.getAction() == GLFW.GLFW_RELEASE) if(isInRange(getPosition(), getEnd(), getWindow())) focus();
        else unfocus();
    }

    private boolean isInRange(Vector2i position, Vector2i end, Window window) {
        return false;
    }

    private void unfocus() {
        isFocused.set(false);
        isBlinking.set(false);
    }

    public void focus() {
        isFocused.set(true);
        isBlinking.set(true);
        cursorPosition = text.length();
    }

    @SubscribeEvent
    public void onKeyCharacterPressed(CharacterInputEvent e) {
        if (!isFocused.get()) return;
        if (isSubmitted) {
            isSubmitted = false;
            text.setLength(0);
            cursorPosition = 0;
        }
        if (e.codepoint >= 32) {
            text.insert((cursorPosition < 0 ? text.length() : cursorPosition), Character.toChars(e.codepoint));
            cursorPosition++;
            updateText();
        }
    }

    @SubscribeEvent
    public void onKeyPressed(KeyPressEvent e) {
        if (!isFocused.get()) return;;
        if (e.getAction() == GLFW.GLFW_RELEASE) {
            if (e.getKey() == GLFW.GLFW_KEY_ESCAPE) {
                isFocused.set(false);
            } else if (e.getKey() == GLFW.GLFW_KEY_ENTER) {
                submit();
            } else if (e.getKey() == GLFW.GLFW_KEY_BACKSPACE) {
                if (!text.isEmpty() && cursorPosition > 0) {
                    text.deleteCharAt(cursorPosition - 1); // Delete the character *before* the cursor
                    cursorPosition--; // Move the cursor back by one
                    updateText();
                }
            } else if (e.getKey() == GLFW.GLFW_KEY_RIGHT) {
                cursorPosition = AdvancedMath.clamp(cursorPosition +1,0,text.length());
                updateText();
            } else if (e.getKey() == GLFW.GLFW_KEY_LEFT) {
                cursorPosition = AdvancedMath.clamp(cursorPosition -1,0, text.length());
                updateText();
            }
        }
    }

    public void submit() {
        isSubmitted = true;
        isFocused.set(false);
        eventBus.post(new TextInputSubmitted(text.toString()));
    }

    private void updateText() {
        isBlinking.set(true);
        super.setText(text.toString());
    }

    public static class TextInputSubmitted extends Event {
        public String text;

        public TextInputSubmitted(String text) {
            this.text = text;
        }
    }

    public LocalEventBus getEventBus() {
        return eventBus;
    }
}
