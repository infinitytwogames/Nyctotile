package org.infinitytwo.umbralore.core.registry;

import org.infinitytwo.umbralore.core.model.Model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelRegistry {
    private static final Map<Integer, Model> intToModel = new ConcurrentHashMap<>();

    public static int register(Model model) {
        int index = intToModel.size();
        intToModel.put(index,model);
        return index;
    }

    public static Model get(int index) {
        return intToModel.get(index);
    }
}
