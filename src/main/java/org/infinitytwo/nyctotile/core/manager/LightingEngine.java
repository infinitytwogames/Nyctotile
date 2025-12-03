package org.infinitytwo.nyctotile.core.manager;

import org.infinitytwo.nyctotile.core.data.Light;
import org.infinitytwo.nyctotile.core.world.GMap;
import org.infinitytwo.nyctotile.core.world.GridMap;
import org.joml.Vector3i;

import java.util.LinkedList;
import java.util.Queue;

// TODO: RESUME IMPLEMENTATION
public class LightingEngine {
    private GMap map;
    private final Queue<Light> lightAdditionQueue = new LinkedList<>();
    private final Queue<Light> lightRemovalQueue = new LinkedList<>();
    
    // The 6 directions for checking neighbors
    private static final Vector3i[] NEIGHBOR_DIRS = {
            new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0),
            new Vector3i(0, 1, 0), new Vector3i(0, -1, 0),
            new Vector3i(0, 0, 1), new Vector3i(0, 0, -1)
    };
    
    public LightingEngine(GMap map) {
        this.map = map;
    }
    
    public void addLightSource(Vector3i worldPos, int lightLevel) {
        if (lightLevel > 0) {
            // 1. Set the initial light level at the source (assuming it's white light for simplicity)
            map.setLight(worldPos.x, worldPos.y, worldPos.z, 255, 255, 255, lightLevel);
            
            // 2. Seed the addition queue to start the BFS
            lightAdditionQueue.add(new Light(worldPos, lightLevel));
        }
    }
    
    public void update() {
        processLightAddition();
        // processLightRemoval(); // Implement this later
    }
    
    // The BFS for light spreading (Step 1 in the plan)
    private void processLightAddition() {
        while (!lightAdditionQueue.isEmpty()) {
            Light current = lightAdditionQueue.poll();
            Vector3i pos = current.getPosition();
            int currentLevel = current.getLevel();
            
            if (currentLevel <= 1) continue; // Light extinguished
            
            int nextLevel = currentLevel - 1;
            
            for (Vector3i dir : NEIGHBOR_DIRS) {
                Vector3i neighborPos = pos.add(dir, new Vector3i()); // Use a new Vector3i to avoid modification
                
                // 1. Check if the block is loaded and accessible (GMap handles this)
                if (!map.isBlockLoaded(neighborPos.x, neighborPos.y, neighborPos.z)) continue;
                
                // 2. Check OPAQUENESS (Light stops at opaque blocks)
                if (!map.getBlockType(neighborPos).isTransparent()) continue; // Requires a GMap method to check block properties
                
                // 3. Intensity Check
                int existingLevel = map.getLightLevel(neighborPos.x, neighborPos.y, neighborPos.z);
                
                if (nextLevel > existingLevel) {
                    // Update and Re-enqueue
                    map.setLightLevel(neighborPos.x, neighborPos.y, neighborPos.z, nextLevel);
                    lightAdditionQueue.add(new Light(neighborPos, nextLevel));
                    
                    // You must ensure the chunk containing neighborPos gets rebuilt.
                    // This is often handled by GMap/GridMap calling Chunk.dirty()
                    if (map instanceof GridMap m) m.rebuildChunk(neighborPos.x,neighborPos.z); // Assuming GMap/GridMap has this abstract method
                }
            }
        }
    }
}
