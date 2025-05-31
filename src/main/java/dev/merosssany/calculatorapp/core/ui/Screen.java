package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.render.ShaderProgram;
import dev.merosssany.calculatorapp.core.render.TextBatchRenderer;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import dev.merosssany.calculatorapp.core.Window;

import java.util.ArrayList;

public class Screen {
    private ShaderProgram textShader;
    private final ShaderProgram uiShader;
    private final String name;
    private final UIBatchRenderer uiBatchRenderer;
    private final TextBatchRenderer fontBatchRenderer;
    private final ArrayList<UI> uis;
    private RGB fontColor;
    private final Window window;

    public Screen(ShaderProgram textShader, ShaderProgram uiShader, UIBatchRenderer uiBatchRenderer, TextBatchRenderer fontBatchRenderer, String name, RGB fontColor, Window window) {
        this.textShader = textShader;
        this.uiShader = uiShader;
        this.uiBatchRenderer = uiBatchRenderer;
        this.fontBatchRenderer = fontBatchRenderer;
        this.name = name;
        this.fontColor = fontColor;
        this.window = window;
        this.uis = new ArrayList<>();
    }

    public void register(UI ui) {
        if (!uis.contains(ui)) {
            uis.add(ui);
        }
    }

    public void draw() {
        uiBatchRenderer.begin();
        for (UI ui : uis) {
            ui.draw();
        }
        uiBatchRenderer.flush();
    }
}
