package dev.merosssany.calculatorapp.core.render;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.ui.font.FontRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

public class TextBatchRenderer {
    private final FontRenderer font;
    private final FloatBuffer vertexBuffer;
    private final int vaoId, vboId;
    private int glyphCount = 0;

    public TextBatchRenderer(FontRenderer font, float scale) {
        this.font = font;

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, 4096 * 6 * 4 * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        vertexBuffer = BufferUtils.createFloatBuffer(4096 * 6 * 4); // Max 4096 glyphs per frame
    }

    public void begin(Matrix4f proj, RGB color) {
        glyphCount = 0;
        vertexBuffer.clear();

        glUseProgram(font.getShaderProgramId());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer mat = stack.mallocFloat(16);
            proj.get(mat);
            glUniformMatrix4fv(font.getLocProj(), false, mat);
        }
        glUniform3f(font.getLocTextColor(), color.getRed(), color.getGreen(), color.getBlue());
        glUniform1i(font.getLocFontAtlas(), 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureID());
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void queue(@NotNull String text, float x, float y) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xb = stack.floats(x);
            FloatBuffer yb = stack.floats(y);
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            STBTTBakedChar.Buffer charData = font.getCharData();

            for (char c : text.toCharArray()) {
                if (c < 32 || c >= 128) continue;
                stbtt_GetBakedQuad(
                        charData,
                        font.getBitmapWidth(),
                        font.getBitmapHeight(),
                        c - 32,
                        xb,
                        yb,
                        quad,
                        true);

                // Triangle 1
                vertexBuffer.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());
                vertexBuffer.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
                vertexBuffer.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
                // Triangle 2
                vertexBuffer.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
                vertexBuffer.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
                vertexBuffer.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());

                glyphCount++;
            }
        }
    }

    public void flush() {
        vertexBuffer.flip();
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
        glDrawArrays(GL_TRIANGLES, 0, glyphCount * 6);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
        glUseProgram(0);
        glDisable(GL_BLEND);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }
}
