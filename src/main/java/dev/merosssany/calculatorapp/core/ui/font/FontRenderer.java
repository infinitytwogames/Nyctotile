package dev.merosssany.calculatorapp.core.ui.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class FontRenderer {
    private static final int BITMAP_W   = 512;
    private static final int BITMAP_H   = 512;
    private static final int FIRST_CHAR = 32;
    private static final int CHAR_COUNT = 96;

    private final int texID;
    private final STBTTBakedChar.Buffer charData;
    private final ByteBuffer fontBuffer;  // keep reference

    public FontRenderer(String fontPath, float fontHeight) {
        try {
            fontBuffer = loadFont(fontPath);

            // Bake ASCII 32–127 into a single-channel bitmap
            charData = STBTTBakedChar.malloc(CHAR_COUNT);
            ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
            stbtt_BakeFontBitmap(fontBuffer, fontHeight, bitmap, BITMAP_W, BITMAP_H, FIRST_CHAR, charData);

            // Upload as GL_RED texture
            texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, BITMAP_W, BITMAP_H, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load font: " + fontPath, e);
        }
    }

    /**
     * Renders a string at (x,y) in screen pixels. You must set an orthographic
     * projection (e.g. via glOrtho) *before* calling this.
     */
    public void renderText(String text, float x, float y, float r, float g, float b) {
        // Enable modulation so the red-channel atlas masks the color
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texID);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor3f(r, g, b);
        glBegin(GL_QUADS);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Use stack‐allocated float buffers for pen position
            var xBuf = stack.floats(x);
            var yBuf = stack.floats(y);
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            for (char c : text.toCharArray()) {
                if (c < FIRST_CHAR || c >= FIRST_CHAR + CHAR_COUNT) continue;
                stbtt_GetBakedQuad(charData, BITMAP_W, BITMAP_H, c - FIRST_CHAR, xBuf, yBuf, quad, true);

                glTexCoord2f(quad.s0(), quad.t0()); glVertex2f(quad.x0(), quad.y0());
                glTexCoord2f(quad.s1(), quad.t0()); glVertex2f(quad.x1(), quad.y0());
                glTexCoord2f(quad.s1(), quad.t1()); glVertex2f(quad.x1(), quad.y1());
                glTexCoord2f(quad.s0(), quad.t1()); glVertex2f(quad.x0(), quad.y1());
            }
        }

        glEnd();
        glDisable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDeleteTextures(texID);
        charData.free();
    }

    private ByteBuffer loadFont(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.isReadable(p)) throw new IOException("Cannot read font: " + path);
        try (SeekableByteChannel ch = Files.newByteChannel(p)) {
            ByteBuffer buf = BufferUtils.createByteBuffer((int)ch.size()+1);
            while (ch.read(buf) != -1);
            buf.flip();
            return buf;
        }
    }
}
