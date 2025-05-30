package dev.merosssany.calculatorapp.core.ui;

import dev.merosssany.calculatorapp.Main;
import dev.merosssany.calculatorapp.core.render.ShaderProgram;
import dev.merosssany.calculatorapp.core.render.UIBatchRenderer;
import dev.merosssany.calculatorapp.core.render.Window;

public class Screen extends UIBatchRenderer {
    private String title = "Screen";
    private final Window window;

    public Screen(int shaderProgramId, String title) {
        super(shaderProgramId);
        this.title = title;
        this.window = Main.getWindow();
    }

    public Screen(ShaderProgram program, String title) {
        super(program);
        this.title = title;
        this.window = Main.getWindow();
    }

    public Screen(String title) {
        this.title = title;
        this.window = Main.getWindow();
    }

    public Screen() {
        this.window = Main.getWindow();
    }

    public Screen(int shaderProgramId, Window window, String title) {
        super(shaderProgramId);
        this.window = window;
        this.title = title;
    }

    public Screen(ShaderProgram program, Window window, String title) {
        super(program);
        this.window = window;
        this.title = title;
    }

    public Screen(Window window, String title) {
        this.window = window;
        this.title = title;
    }


}
