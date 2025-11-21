package org.infinitytwo.umbralore.core.renderer;

import org.infinitytwo.umbralore.core.Main;
import org.infinitytwo.umbralore.core.RGBA;
import org.infinitytwo.umbralore.core.constants.ShaderFiles;
import org.infinitytwo.umbralore.core.event.SubscribeEvent;
import org.infinitytwo.umbralore.core.event.bus.EventBus;
import org.infinitytwo.umbralore.core.event.state.WindowResizedEvent;
import org.infinitytwo.umbralore.core.model.TextureAtlas;
import org.infinitytwo.umbralore.core.ui.UI;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.infinitytwo.umbralore.core.constants.Constants.UI_DESIGN_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class UIBatchRenderer {
    private int vaoId, vboId;
    private FloatBuffer vertexBuffer;
    private float[] vertexData;
    private int vertexDataIndex = 0;
    private final int MAX_QUADS = 1000;
    private final int VERTEX_SIZE = 9;
    private final int VERTICES_PER_QUAD = 6;
    private final int FLOATS_PER_QUAD = VERTEX_SIZE * VERTICES_PER_QUAD;
    
    private final Matrix4f projection = new Matrix4f();
    private int shaderProgramId;
    
    // --- BATCH STATE MANAGEMENT (NEW) ---
    private boolean currentBatchIsTextured = false;
    private int currentAtlasTextureID = 0; // The texture ID of the currently bound atlas
    
    public UIBatchRenderer(int shaderProgramId) {
        this.shaderProgramId = shaderProgramId;
        init();
    }
    
    public UIBatchRenderer(ShaderProgram program) {
        this.shaderProgramId = program.getProgramId();
        init();
    }
    
    public UIBatchRenderer() {
        this.shaderProgramId = new ShaderProgram(ShaderFiles.uiVertex, ShaderFiles.uiFragment).getProgramId();
        init();
    }
    
    private void init() {
        EventBus.connect(this);
        
        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        vertexData = new float[MAX_QUADS * FLOATS_PER_QUAD];
        vertexBuffer = MemoryUtil.memAllocFloat(MAX_QUADS * FLOATS_PER_QUAD);
        
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) vertexData.length * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        
        // Attribute Pointers remain correct
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 6 * Float.BYTES);
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        // Set the u_texture sampler uniform once
        GL20.glUseProgram(shaderProgramId);
        int locSampler = glGetUniformLocation(shaderProgramId, "u_texture");
        glUniform1i(locSampler, 0); // Tell shader u_texture always refers to Texture Unit 0
        GL20.glUseProgram(0);
        
        onWindowResize(new WindowResizedEvent(1024, 512, Main.getWindow()));
    }
    
    public void queue(UI ui) {
        // --- BATCH BREAK: Switching from Textured to Untextured ---
        if (currentBatchIsTextured) {
            flush();
            currentBatchIsTextured = false;
            currentAtlasTextureID = 0;
            begin();
        }
        
        if (vertexDataIndex + FLOATS_PER_QUAD > vertexData.length) {
            flush();
            begin();
        }
        
        RGBA color = ui.getBackgroundColor();
        float x = ui.getPosition().x(); // Use float for rotation
        float y = ui.getPosition().y(); // Use float for rotation
        float w = ui.getWidth();
        float h = ui.getHeight();
        
        float angle = ui.getAngle(); // Assuming this is defined
        
        // Calculate pivot
        float pivotX = x + w / 2.0f;
        float pivotY = y + h / 2.0f;
        
        float cosTheta = (float) Math.cos(Math.toRadians(angle));
        float sinTheta = (float) Math.sin(Math.toRadians(angle));
        
        // Define corner offsets relative to pivot
        // Corner: TL, BL, BR, TR
        float[] dx = {-w / 2.0f, -w / 2.0f, w / 2.0f, w / 2.0f};
        float[] dy = {-h / 2.0f, h / 2.0f, h / 2.0f, -h / 2.0f};
        
        // UVs are still 0
        float[] u = {0.0f, 0.0f, 0.0f, 0.0f}; // TL, BL, BR, TR
        float[] v = {0.0f, 0.0f, 0.0f, 0.0f}; // TL, BL, BR, TR
        
        // Index map: First triangle (0, 1, 2), Second triangle (0, 2, 3)
        int[] indices = {0, 1, 2, 0, 2, 3};
        
        float[] quad = new float[FLOATS_PER_QUAD]; // 6 vertices * 8 floats
        
        for (int i = 0; i < 6; i++) {
            int cornerIndex = indices[i];
            int vertexOffset = i * VERTEX_SIZE;
            
            // 1. Calculate Rotated Position
            float localX = dx[cornerIndex];
            float localY = dy[cornerIndex];
            
            float rotatedX = localX * cosTheta - localY * sinTheta;
            float rotatedY = localX * sinTheta + localY * cosTheta;
            
            float finalX = rotatedX + pivotX;
            float finalY = rotatedY + pivotY;
            
            // 2. Populate Vertex Data (9 FLOATS per vertex)
            quad[vertexOffset] = finalX;
            quad[vertexOffset + 1] = finalY;
            quad[vertexOffset + 2] = ui.getDrawOrder();
            
            quad[vertexOffset + 3] = color.getRed();
            quad[vertexOffset + 4] = color.getGreen();
            quad[vertexOffset + 5] = color.getBlue();
            quad[vertexOffset + 6] = color.getAlpha();
            
            quad[vertexOffset + 7] = u[cornerIndex]; // U
            quad[vertexOffset + 8] = v[cornerIndex]; // V
        }
        
        System.arraycopy(quad, 0, vertexData, vertexDataIndex, quad.length);
        vertexDataIndex += quad.length;
    }
    
    public void queueTextured(int textureIndex, TextureAtlas atlas, RGBA foregroundColor, UI ui) {
        // --- BATCH BREAK: Switching texture OR switching from untextured ---
        int atlasID = atlas.getTexture().getTextureID();
        
        if (!currentBatchIsTextured || currentAtlasTextureID != atlasID) {
            flush();
            currentBatchIsTextured = true;
            currentAtlasTextureID = atlasID;
            begin();
        }
        
        if (vertexDataIndex + FLOATS_PER_QUAD > vertexData.length) {
            flush();
            begin();
        }
        
        // Get UVs from atlas
        float x = ui.getPosition().x();
        float y = ui.getPosition().y();
        float w = ui.getWidth();
        float h = ui.getHeight();
        float angle = ui.getAngle();
        
        // Calculate pivot
        float pivotX = x + w / 2.0f;
        float pivotY = y + h / 2.0f;
        
        float cosTheta = (float) Math.cos(Math.toRadians(angle));
        float sinTheta = (float) Math.sin(Math.toRadians(angle));
        
        // Define corner offsets relative to pivot
        // Corner: TL, BL, BR, TR
        float[] dx = {-w / 2.0f, -w / 2.0f, w / 2.0f, w / 2.0f};
        float[] dy = {-h / 2.0f, h / 2.0f, h / 2.0f, -h / 2.0f};
        
        float[] uv = atlas.getUVCoords(textureIndex);
        
        // Fixed UV Mapping:
        // uv[0]=U0 (Left), uv[1]=U1 (Right)
        // uv[2]=V0 (Top), uv[3]=V1 (Bottom)
        float[] u = {uv[0], uv[0], uv[1], uv[1]}; // TL(U0), BL(U0), BR(U1), TR(U1)
        float[] v = {uv[2], uv[3], uv[3], uv[2]}; // TL(V0), BL(V1), BR(V1), TR(V0)
        
        // Index map: First triangle (0, 1, 2), Second triangle (0, 2, 3)
        int[] indices = {0, 1, 2, 0, 2, 3};
        
        float[] quad = new float[FLOATS_PER_QUAD]; // 6 vertices * 8 floats
        
        for (int i = 0; i < 6; i++) {
            int cornerIndex = indices[i];
            int vertexOffset = i * VERTEX_SIZE;
            
            // 1. Calculate Rotated Position
            float localX = dx[cornerIndex];
            float localY = dy[cornerIndex];
            
            float rotatedX = localX * cosTheta - localY * sinTheta;
            float rotatedY = localX * sinTheta + localY * cosTheta;
            
            float finalX = rotatedX + pivotX;
            float finalY = rotatedY + pivotY;
            
            // 2. Populate Vertex Data (8 FLOATS per vertex)
            quad[vertexOffset] = finalX;
            quad[vertexOffset + 1] = finalY;
            quad[vertexOffset + 2] = ui.getDrawOrder();
            
            quad[vertexOffset + 3] = foregroundColor.getRed();
            quad[vertexOffset + 4] = foregroundColor.getGreen();
            quad[vertexOffset + 5] = foregroundColor.getBlue();
            quad[vertexOffset + 6] = foregroundColor.getAlpha();
            
            quad[vertexOffset + 7] = u[cornerIndex]; // U
            quad[vertexOffset + 8] = v[cornerIndex]; // V
        }
        
        System.arraycopy(quad, 0, vertexData, vertexDataIndex, quad.length);
        vertexDataIndex += quad.length;
    }
    
    public void begin() {
        vertexDataIndex = 0; // Reset index for new batch
    }
    
    public void flush() {
        if (vertexDataIndex == 0) return; // Nothing to draw
        
        // 1. Prepare and upload vertex data
        vertexBuffer.clear();
        vertexBuffer.put(vertexData, 0, vertexDataIndex).flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);
        
        // 2. Activate Shader and VAO
        GL30.glBindVertexArray(vaoId);
        GL20.glUseProgram(shaderProgramId);
        
        // 3. Set Uniforms based on current batch state
        
        // --- CRITICAL: Set the useTexture flag ---
        int locUseTexture = glGetUniformLocation(shaderProgramId, "useTexture");
        // Convert Java boolean to int (1 for true, 0 for false)
        glUniform1i(locUseTexture, currentBatchIsTextured? 1 : 0);
        
        // Set projection matrix
        int locProj = glGetUniformLocation(shaderProgramId, "projection");
        try (var stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            projection.get(fb);
            glUniformMatrix4fv(locProj, false, fb);
        }
        
        // --- CRITICAL: Bind Texture for Textured Batch ---
        if (currentBatchIsTextured) {
            // Since we set u_texture to 0 in init(), we bind to GL_TEXTURE0
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, currentAtlasTextureID);
        } else {
            // Unbind texture unit 0 (good practice)
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        
        // 4. OpenGL State Setup
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        // 5. Draw
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexDataIndex / VERTEX_SIZE);
        
        // 6. Cleanup OpenGL State
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glUseProgram(0);
        GL30.glBindVertexArray(0);
        // No need to unbind texture here if we unbound it above,
        // but leaving glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0); in the else block is cleaner.
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
    
    @SubscribeEvent
    public void onWindowResize(WindowResizedEvent e) {
        float currentWindowWidth = e.width;
        float currentWindowHeight = e.height;
        
        float currentVirtualWidth = (currentWindowWidth / currentWindowHeight) * UI_DESIGN_HEIGHT;
        
        // Set the orthographic projection matrix
        projection.setOrtho(
                0.0f,
                currentVirtualWidth,
                UI_DESIGN_HEIGHT,
                0.0f,
                -100.0f,
                100.0f
        );
    }
}
