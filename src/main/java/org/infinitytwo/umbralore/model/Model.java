package org.infinitytwo.umbralore.model;

import java.util.ArrayList;

/**
 * This class is a Model for a block for collision testing.
 * <br><br>
 * <strong>[ Disclaimer! ]</strong><br>
 * This class is <strong>NOT</strong> used in rendering.
 * It's used in collision testing only!
 */
public class Model {
    protected ArrayList<Cube> cubes = new ArrayList<>();

    public static Model standard() {
        Model model = new Model();

        model.cubes.add(Cube.standard());
        return model;
    }

    public void addCube(Cube cube) {
        cubes.add(cube);
    }

    public enum Face {
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
