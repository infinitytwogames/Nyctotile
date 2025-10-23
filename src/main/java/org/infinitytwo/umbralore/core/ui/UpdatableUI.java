package org.infinitytwo.umbralore.core.ui;

import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;

public abstract class UpdatableUI extends UI {
    public UpdatableUI(UIBatchRenderer renderer) {
        super(renderer);
    }

    public abstract void update(float delta);
}
