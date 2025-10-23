package org.infinitytwo.umbralore.core.registry;

import org.infinitytwo.umbralore.core.constants.ResourceType;
import org.infinitytwo.umbralore.core.exception.UnknownRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceLocation {
    private static final Logger log = LoggerFactory.getLogger(ResourceLocation.class);
    private static Map<ResourceLocation, Path> location = new ConcurrentHashMap<>();
    private static Set<String> mods = new HashSet<>();
    private static ArrayList<String> resources = new ArrayList<>();

    private final String key;
    private final String mod;
    private final ResourceType type;

    private ResourceLocation(String key, String mod, ResourceType type) {
        this.key = key.toLowerCase();
        this.mod = mod.toLowerCase();
        this.type = type;
    }

    public static void registerKey(String mod, String key, Path path, ResourceType type) {
        if (resources.contains(mod+":"+key)) return;
        if (mods.contains(mod.toLowerCase())) {
            File file = path.toFile();
            if (file.exists()) {
                resources.add(mod+":"+key);
                location.put(new ResourceLocation(key.toLowerCase(),mod.toLowerCase(),type), path);
            }
        }
    }

    public static void registerMod(String mod) {
        mods.add(mod.toLowerCase(Locale.ROOT));
    }

    public static ResourceLocation get(String key, String mod) {
        if (key == null && mod == null) throw new IllegalArgumentException("Key or Mod argument is null, cannot proceed.");
        for (ResourceLocation resource : location.keySet()) {
            if (key.equals(resource.key.toLowerCase()) && mod.equals(resource.mod.toLowerCase())) return resource;
        }
        return null;
    }

    public static Path getAsPath(ResourceLocation resource) throws UnknownRegistryException {
        Path path = location.getOrDefault(resource, null);
        if (path == null) throw new UnknownRegistryException("Unable to find path using ResourceLocation \""+resource.toString()+",\" This might mean that it's not registered.");
        return path;
    }

    public static Path getPathFromString(String mod, String key) throws UnknownRegistryException {
        ResourceLocation resource = get(key.toLowerCase(),mod.toLowerCase());
        if (resource != null) return getAsPath(resource);
        else return Path.of("");
    }

    @Override
    public String toString() {
        return mod+":"+key;
    }
}
