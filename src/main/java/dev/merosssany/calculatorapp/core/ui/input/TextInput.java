package dev.merosssany.calculatorapp.core.ui.input;

import dev.merosssany.calculatorapp.Main;
import dev.merosssany.calculatorapp.core.AdvancedMath;
import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.event.bus.EventBus;
import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.core.event.input.KeyPressEvent;
import dev.merosssany.calculatorapp.core.event.input.MouseButtonEvent;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.position.UIVector2Df;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;
import dev.merosssany.calculatorapp.core.ui.Cursor;
import dev.merosssany.calculatorapp.core.ui.Label;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Deprecated
public class TextInput extends Label {
    private boolean isInputting = false;
    private final StringBuilder builder = new StringBuilder();
    private final Method method;
    private final Object obj;
    private final Logger logger = new Logger("TextInput");
//    private final Cursor cursor;
    private final int fontHeight;

    public TextInput(UIBatchRenderer screen, Window window, Method method, Object obj, UIVector2Df position, RGB color, float width, float height, RGBA background) {
        super(window, position, "", false, color, width, height, background);
        this.method = method;
        this.obj = obj;
        fontHeight = (int) Main.getFontRenderer().getFontHeight();
//        this.cursor = new Cursor(screen, new UIVector2Df(0,0), AdvancedMath.pixelToNdc(fontHeight,0,window).getX(), new RGBA(1f,1f,1f,1f));
    }

    public static String convertGlfwKeyToLetterOrDigit(int glfwKeyCode) {
        // Check for letters (A-Z)
        if (glfwKeyCode >= GLFW.GLFW_KEY_A && glfwKeyCode <= GLFW.GLFW_KEY_Z) {
            return GLFW.glfwGetKeyName(glfwKeyCode, 0);
        }
        // Check for numbers (0-9)
        else if (glfwKeyCode >= GLFW.GLFW_KEY_0 && glfwKeyCode <= GLFW.GLFW_KEY_9) {
            return GLFW.glfwGetKeyName(glfwKeyCode, 0);
        }
        // For keys from the Numpad (0-9) if you want to include them
        else if (glfwKeyCode >= GLFW.GLFW_KEY_KP_0 && glfwKeyCode <= GLFW.GLFW_KEY_KP_9) {
            String keyName = GLFW.glfwGetKeyName(glfwKeyCode, 0);
            // This will return "Keypad 0", "Keypad 1", etc.
            // You might want to parse it to just "0", "1" if needed.
            // For now, we'll return the raw name, or you can simplify:
            if (keyName != null && keyName.startsWith("Keypad ")) {
                return keyName.substring("Keypad ".length()); // Returns "0", "1", etc.
            }
            return keyName; // Fallback for unexpected format
        }

        // If the key is not a letter, a regular digit, or a numpad digit,
        // or if glfwGetKeyName returned null unexpectedly, return null.
        return null;
    }

    @SubscribeEvent
    public void onKeyPressed(KeyPressEvent e) {
        if (e.getKey() == GLFW.GLFW_KEY_ESCAPE) release();
        if (e.getAction() == GLFW.GLFW_RELEASE) return;
        if (e.getKey() == GLFW.GLFW_KEY_ENTER) submit();

        if (isInputting) {
            String c = convertGlfwKeyToLetterOrDigit(e.getKey());
            if (c != null) {
                builder.append(c);
                setText(builder.toString());
                updateCursorPosition();
            }
        }
    }

    private void release() {
        isInputting = false;
//        cursor.stop();
    }

    private void focus() {
        isInputting = true;
//        cursor.start();
    }

    @SubscribeEvent
    public void onMousePressed(MouseButtonEvent e) {

        if (e.getAction() == GLFW.GLFW_RELEASE) {
            if (!AdvancedMath.isInRange(getPosition(),getEnd(),getWindow())) release();
            else focus();
        }
    }

    private void updateCursorPosition() {
        UIVector2Df textPosition = AdvancedMath.pixelToNdc(getTextPosition(),getWindow());
//        cursor.setHeight(AdvancedMath.pixelToNdc(fontHeight,getWindow()));
//        UIVector2Df newPosition = new UIVector2Df(textPosition.getX() + AdvancedMath.pixelToNdc((int) getFontRenderer().getStringWidth(getText()), getWindow()) + getWidth(), textPosition.getY());

//        cursor.setPosition(newPosition);
//        logger.info(newPosition);
    }


    private void submit() {
        logger.info("Submitting with: "+builder);
        try {
            method.invoke(obj,builder.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e,"Couldn't submit the inputted text");
        }
    }

    @Override
    public void init() {
        super.init();
        EventBus.register(this);
//        cursor.init();
    }
}
