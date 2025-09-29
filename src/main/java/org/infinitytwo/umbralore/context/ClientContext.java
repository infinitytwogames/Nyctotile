package org.infinitytwo.umbralore.context;

import org.infinitytwo.umbralore.Window;
import org.infinitytwo.umbralore.constants.LogicalSide;
import org.infinitytwo.umbralore.constants.PhysicalSide;
import org.infinitytwo.umbralore.renderer.Camera;

// TODO: ADD SOME MORE USEFUL STUFF
@Deprecated
public final class ClientContext extends Context {
    private final Camera camera;
    private final Window window;

    private ClientContext(Camera camera, Window window) {
        super(LogicalSide.CLIENT, PhysicalSide.LOCAL); // There is no way a Dedicated server handles a client
        this.camera = camera;
        this.window = window;
    }

    public static ClientContext of(Camera camera, Window window) {
        return new ClientContext(camera, window);
    }

    public Camera getCamera() {
        return camera;
    }

    public Window getWindow() {
        return window;
    }
}
