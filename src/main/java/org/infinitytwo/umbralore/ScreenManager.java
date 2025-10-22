package org.infinitytwo.umbralore;

import org.infinitytwo.umbralore.exception.UnknownRegistryException;
import org.infinitytwo.umbralore.ui.Screen;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    public static final Map<String, Screen> screens = new HashMap<>();
    public static Screen current;

    public static void register(String name, Screen screen) {
        screens.put(name, screen);
    }

    public static Screen setCurrent(String name) {
        Screen screen = screens.get(name);

        if (screen != null) {
            current = screen;
        } else throw new UnknownRegistryException("The screen \""+name+"\" is not found in registry.");
        return screen;
    }

    public static void draw() {
        current.draw();
    }
}
