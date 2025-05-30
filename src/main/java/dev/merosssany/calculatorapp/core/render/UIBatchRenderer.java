package dev.merosssany.calculatorapp.core.render;

import dev.merosssany.calculatorapp.core.RGBA;
import dev.merosssany.calculatorapp.core.ui.UI;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class UIBatchRenderer {
    private int vaoId, vboId;
    private FloatBuffer vertexBuffer;
    private float[] vertexData;
    private int vertexDataIndex = 0;
    private final int MAX_QUADS = 1000;
    private final int VERTEX_SIZE = 6; // x, y, r, g, b, a
    private final int VERTICES_PER_QUAD = 6;
    private final int FLOATS_PER_QUAD = VERTEX_SIZE * VERTICES_PER_QUAD;
    private Matrix4f projection;

    private int shaderProgramId;

    public UIBatchRenderer(int shaderProgramId) {
        this.shaderProgramId = shaderProgramId;
        init();
    }

    public UIBatchRenderer(ShaderProgram program) {
        this.shaderProgramId = program.getProgramId();
        init();
    }

    public UIBatchRenderer() {
        this.shaderProgramId = new ShaderProgram(ShaderFiles.uiVertex,ShaderFiles.uiFragment).getProgramId();
    }

    private void init() {
        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        vertexData = new float[MAX_QUADS * FLOATS_PER_QUAD];
        vertexBuffer = MemoryUtil.memAllocFloat(MAX_QUADS * FLOATS_PER_QUAD);

        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) vertexData.length * Float.BYTES, GL15.GL_DYNAMIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);                 // Position
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);   // Color

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    public void begin(Matrix4f projection) {
        vertexDataIndex = 0;
        this.projection = projection;
    }

    public void queue(UI ui) {
        if (vertexDataIndex + FLOATS_PER_QUAD >= vertexData.length) {
            flush(); // Too many elements? Draw current batch and start a new one
            begin(projection);
        }

        RGBA color = ui.getBackgroundColor();
        float x = ui.getPosition().getX();
        float y = ui.getPosition().getY();
        float w = ui.getWidth();
        float h = ui.getHeight();

        float[] quad = {
                x,     y,     color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
                x + w, y,     color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
                x + w, y - h, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),

                x,     y,     color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
                x + w, y - h, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
                x,     y - h, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
        };

        System.arraycopy(quad, 0, vertexData, vertexDataIndex, quad.length);
        vertexDataIndex += quad.length;
    }

    public void flush() {
        if (vertexDataIndex == 0) return;

        vertexBuffer.clear();
        vertexBuffer.put(vertexData, 0, vertexDataIndex).flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);

        GL30.glBindVertexArray(vaoId);
        GL20.glUseProgram(shaderProgramId);

        // Set projection matrix uniform
//        int locProj = GL20.glGetUniformLocation(shaderProgramId, "uProjection");
//        try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
//            FloatBuffer fb = stack.mallocFloat(16);
//            projection.get(fb);
//            GL20.glUniformMatrix4fv(locProj, false, fb);
//        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexDataIndex / VERTEX_SIZE);

        GL11.glDisable(GL11.GL_BLEND);
        GL20.glUseProgram(0);
        GL30.glBindVertexArray(0);
    }


    public void changeProgramId(int id) {
        shaderProgramId = id;
    }

    public int getMaxQuads() {
        return MAX_QUADS;
    }

    public void cleanup() {
        GL15.glDeleteBuffers(vboId);
        GL30.glDeleteVertexArrays(vaoId);
        MemoryUtil.memFree(vertexBuffer);
    }
}