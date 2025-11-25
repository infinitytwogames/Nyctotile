package org.infinitytwo.umbralore.core.ui.animations;

import org.infinitytwo.umbralore.core.renderer.UIBatchRenderer;
import org.infinitytwo.umbralore.core.ui.UI;

public abstract class UpdatableUI extends UI {
    public UpdatableUI(UIBatchRenderer renderer) {
        super(renderer);
    }

    public abstract void update(float delta);
}
