package dev.merosssany.calculatorapp.core.position;

import dev.merosssany.calculatorapp.core.AdvancedMath;

public class UIVector2Df extends Vector2D<Float> {
    public UIVector2Df(float x, float y) {
        super(AdvancedMath.clamp((Float) x, -1f, 1f),AdvancedMath.clamp((Float) y,-1f,1f));
    }
}
