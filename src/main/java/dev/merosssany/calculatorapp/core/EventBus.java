package dev.merosssany.calculatorapp.core;

import dev.merosssany.calculatorapp.core.event.Event;
import dev.merosssany.calculatorapp.core.event.SubscribeEvent;
import dev.merosssany.calculatorapp.logging.Logger;

import java.lang.reflect.Method;
import java.util.*;

public abstract class EventBus {
    private static final Map<Class<?>, List<EventHandler>> handlers = new HashMap<>();
    private static final Logger logger = new Logger("EventBus");

    public static void register(Object listener) {
        logger.log("Registering",logger.formatClassName(listener.getClass()));
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

    public static void post(Event event) {
        List<EventHandler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : new ArrayList<>(eventHandlers)) { // Iterate over a copy
                try {
                    handler.invoke(event);
                } catch (Exception e) {
                    logger.error(e,"Failed to call event subscriber:",handler.getClass().getName());
                }
            }
        }
    }

    private record EventHandler(Object listener, Method method) {

        public void invoke(Event event) throws Exception {
                method.invoke(listener, event);
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                EventHandler other = (EventHandler) obj;
                return listener.equals(other.listener) && method.equals(other.method);
            }

    }
}
