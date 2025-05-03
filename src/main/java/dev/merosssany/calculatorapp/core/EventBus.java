package dev.merosssany.calculatorapp.core;

import dev.merosssany.calculatorapp.logging.Logger;

import java.lang.reflect.Method;
import java.util.*;

public abstract class EventBus {
    private static final Map<Class<?>, List<EventHandler>> handlers = new HashMap<>();
    private static final Logger logger = new Logger("EventBus");

    public static void register(Object listener) {
        Class<?> listenerClass = listener.getClass();
        for (Method method : listenerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class) &&
                    method.getParameterCount() == 1) {
                Class<?> eventType = method.getParameterTypes()[0];
                method.setAccessible(true); // Allow calling private methods

                EventHandler handler = new EventHandler(listener, method);
                handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
            }
        }
    }

    public static void post(Object event) {
        List<EventHandler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : new ArrayList<>(eventHandlers)) { // Iterate over a copy
                try {
                    handler.invoke(event);
                } catch (Exception e) {
                    logger.error(e,"Failed to call event subscriber:",handler.getClass().getName());
                    e.printStackTrace(); // Handle exceptions appropriately
                }
            }
        }
    }

    private static class EventHandler {
        private final Object listener;
        private final Method method;

        public EventHandler(Object listener, Method method) {
            this.listener = listener;
            this.method = method;
        }

        public void invoke(Object event) throws Exception {
            method.invoke(listener, event);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EventHandler other = (EventHandler) obj;
            return listener.equals(other.listener) && method.equals(other.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(listener, method);
        }
    }
}
