package org.infinitytwo.umbralore.ui;

import org.infinitytwo.umbralore.renderer.UIBatchRenderer;

public abstract class UpdatableUI extends UI {
    public UpdatableUI(UIBatchRenderer renderer) {
        super(renderer);
    }

    public abstract void update(float delta);
}
