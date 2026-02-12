package com.automation.bdd.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared state between step definition classes within a single scenario.
 * Injected via PicoContainer DI into all step classes.
 *
 * @author Victor Grozev
 */
public class ScenarioContext {

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(context.get(key));
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    public void remove(String key) {
        context.remove(key);
    }

    public void clear() {
        context.clear();
    }
}
