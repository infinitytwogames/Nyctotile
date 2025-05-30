package dev.merosssany.calculatorapp.core.ui.font;

import dev.merosssany.calculatorapp.core.RGB;
import dev.merosssany.calculatorapp.core.logging.Logger;
import dev.merosssany.calculatorapp.core.position.Vector2D;
import dev.merosssany.calculatorapp.core.render.ShaderFiles;
import dev.merosssany.calculatorapp.core.render.ShaderProgram;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * Shader-based FontRenderer using dynamic VBO/VAO. No fixed-function matrices.
 * Pass the compiled shader program ID in the constructor.
 */
public class FontRenderer {
    private static final int BITMAP_W   = 512;
    private static final int BITMAP_H   = 512;
    private static final int FIRST_CHAR = 32;
    private static final int CHAR_COUNT = 96;

    private int texID;
    private STBTTBakedChar.Buffer charData;

    // Shader and uniforms
    private final int shaderProgramId;
    private  int locProj;
    private  int locTextColor;
    private  int locFontAtlas;

    // VAO/VBO for vertex data (x,y,s,t)
    private int vaoId;
    private int vboId;
    private String fontPath;

    private float fontHeight;
    private ShaderProgram program;
    private boolean initialized = false;
    private final Logger logger;

    /**
     * @param fontPath    path to TTF file
     * @param height      pixel height
     */
    public FontRenderer(String fontPath, float height) {
        this.fontHeight = height;
        program = new ShaderProgram(ShaderFiles.textVertex,ShaderFiles.textFragment);
        this.shaderProgramId = program.getProgramId();
        this.fontPath = fontPath;
        this.logger = new Logger("FontRenderer");
        init();
    }

    private void init() {
        // Query uniforms once
        logger.info("Initializing Font Renderer");
        locProj      = glGetUniformLocation(shaderProgramId, "uProj");
        locTextColor = glGetUniformLocation(shaderProgramId, "uTextColor");
        locFontAtlas = glGetUniformLocation(shaderProgramId, "uFontAtlas");

        // Load font data
        ByteBuffer fontBuffer;
        try {
            fontBuffer = loadFont(fontPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font: " + fontPath, e);
        }

        // Bake glyphs
        charData = STBTTBakedChar.malloc(CHAR_COUNT);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H); // CONVERTED
        stbtt_BakeFontBitmap(fontBuffer, fontHeight, bitmap, BITMAP_W, BITMAP_H, FIRST_CHAR, charData);

        // Upload texture atlas
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, BITMAP_W, BITMAP_H, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        // Setup VAO/VBO
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, 2048 * 6 * 4 * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        logger.info("Initialized Successfully");
        initialized = true;
    }

    /**
     * Render text at screen coordinates (x, y).
     */
    public void renderText(Matrix4f proj, String text, float x, float y, float r, float g, float b) {
        if (!initialized) {
            logger.fatal(new IllegalStateException("FontRenderer is not initialized"),"FontRenderer is not initialized");
            return;
        }
        glUseProgram(shaderProgramId);
        // Upload projection
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer mat = stack.mallocFloat(16);
            proj.get(mat);
            glUniformMatrix4fv(locProj, false, mat);
        }
        // Color & sampler
        glUniform3f(locTextColor, r, g, b);
        glUniform1i(locFontAtlas, 0);

        // Bind atlas
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Build dynamic vertex buffer
        FloatBuffer buf = BufferUtils.createFloatBuffer(text.length() * 6 * 4);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xb = stack.floats(x);
            FloatBuffer yb = stack.floats(y);
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);
            for (char c : text.toCharArray()) {
                if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT) continue;
                stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - FIRST_CHAR, xb, yb, quad, true);
                // Triangle 1
                buf.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());
                buf.put(quad.x1()).put(quad.y0()).put(quad.s1()).put(quad.t0());
                buf.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
                // Triangle 2
                buf.put(quad.x1()).put(quad.y1()).put(quad.s1()).put(quad.t1());
                buf.put(quad.x0()).put(quad.y1()).put(quad.s0()).put(quad.t1());
                buf.put(quad.x0()).put(quad.y0()).put(quad.s0()).put(quad.t0());
            }
        }
        buf.flip();

        // Upload & draw
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buf);
        glDrawArrays(GL_TRIANGLES, 0, buf.limit() / 4);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDisable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, 0);
        glUseProgram(0);
    }

    /** Cleanup GPU resources */
    public void cleanup() {
        glDeleteTextures(texID);
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
        charData.free();
        initialized = false;
    }

    private ByteBuffer loadFont(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.isReadable(p)) throw new IOException("Cannot read font: " + path);
        try (SeekableByteChannel ch = Files.newByteChannel(p)) {
            ByteBuffer buf = BufferUtils.createByteBuffer((int) ch.size() + 1);
            while (ch.read(buf) != -1);
            buf.flip();
            return buf;
        }
    }

    public float getStringWidth(String text) {
        // We'll simulate the text rendering to get the final x position
        float currentX = 0.0f; // Start at 0, as if rendering from the origin
        float currentY = 0.0f; // Y doesn't affect width, but GetBakedQuad requires it

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xb = stack.floats(currentX);
            FloatBuffer yb = stack.floats(currentY); // Y doesn't matter for width
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            for (char c : text.toCharArray()) {
                if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT) continue;
                // The 'true' at the end means it modifies xb and yb in-place.
                stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - FIRST_CHAR, xb, yb, quad, true);
                // xb.get(0) now holds the x position for the next character
            }
            return xb.get(0) - currentX; // Return the total advance from the starting x
        }
    }

    public void renderText(Matrix4f proj, String text, Vector2D<Integer> position, RGB color) {
        renderText(proj,text,position.getX(),position.getY(),color.getRed(),color.getGreen(),color.getBlue());
    }

    public void renderText(Matrix4f proj, String text, int x, int y, RGB color) {
        renderText(proj,text,x,y,color.getRed(),color.getGreen(),color.getBlue());
    }

    public float getFontHeight() {
        return this.fontHeight;
    }

    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
        reinit();
    }

    private void reinit() {
        cleanup();
        init();
    }

    public STBTTBakedChar.Buffer getCharData() {
        return charData;
    }

    public int getTextureID() {
        return texID;
    }

    public int getBitmapWidth() {
        return BITMAP_W;
    }

    public int getBitmapHeight() {
        return BITMAP_H;
    }

    public int getShaderProgramId() {
        return shaderProgramId;
    }

    public int getLocProj() {
        return locProj;
    }

    public int getLocTextColor() {
        return locTextColor;
    }

    public int getLocFontAtlas() {
        return locFontAtlas;
    }
}
