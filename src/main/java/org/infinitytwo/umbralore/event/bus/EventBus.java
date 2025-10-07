package org.infinitytwo.umbralore.event.bus;

import org.infinitytwo.umbralore.event.Event;
import org.infinitytwo.umbralore.event.SubscribeEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class EventBus {
    private static final Map<Class<?>, List<ListenerMethod>> listeners = new HashMap<>();

    public static void register(Object listenerInstance) {
        for (Method method : listenerInstance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    Class<?> eventType = params[0];
                    method.setAccessible(true);
                    listeners
                            .computeIfAbsent(eventType, k -> new ArrayList<>())
                            .add(new ListenerMethod(listenerInstance, method));
                }
            }
        }
    }

    public static void register(Class<?> listenerClass) {
        if (listenerClass == null) {
            System.err.println("EventBus: Cannot register null class for static methods.");
            return;
        }

        for (Method method : listenerClass.getDeclaredMethods()) {
            // Only process static methods for static registration
            if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    Class<?> eventType = params[0];
                    method.setAccessible(true); // Allow access to private static methods
                    listeners
                            .computeIfAbsent(eventType, k -> new ArrayList<>())
                            .add(new ListenerMethod(null, method)); // Pass null for instance for static methods
                }
            }
        }
    }

    public static void post(Event event) {
        List<ListenerMethod> methods = listeners.get(event.getClass());
        if (methods != null) {
            for (ListenerMethod lm : new ArrayList<>(methods)) {
                try {
                    lm.method.invoke(lm.instance, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void unregister(Object listenerInstance) {
        if (listenerInstance == null) return;

        for (List<ListenerMethod> methods : listeners.values()) {
            methods.removeIf(lm -> lm.instance == listenerInstance);
        }
    }


    private record ListenerMethod(Object instance, Method method) {}
}
