package org.infinitytwo.umbralore.block;

import org.joml.Vector3f;

@Deprecated
public class Block<T extends BlockType> {
    public float x, y, z;
    protected String material;

    // Per-face texture indices
    protected T type;

    public Block<T> create(T type) {
        this.type = type;
        return this;
    }

    public T getType() {
        return type;
    }

    public Block<T> setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public static float[] createSingleFace(int faceIndex, float[] uv, float brightness) {
        float uMin = uv[0], vMin = uv[1];
        float uMax = uv[2], vMax = uv[3];

        switch (faceIndex) {
            case 0: // Front (+Z)
                return new float[]{
                        0, 0, 1, uMin, vMin, brightness,
                        1, 0, 1, uMax, vMin, brightness,
                        1, 1, 1, uMax, vMax, brightness,
                        1, 1, 1, uMax, vMax, brightness,
                        0, 1, 1, uMin, vMax, brightness,
                        0, 0, 1, uMin, vMin, brightness,
                };
            case 1: // Back (-Z)
                return new float[]{
                        1, 0, 0, uMin, vMin, brightness,
                        0, 0, 0, uMax, vMin, brightness,
                        0, 1, 0, uMax, vMax, brightness,
                        0, 1, 0, uMax, vMax, brightness,
                        1, 1, 0, uMin, vMax, brightness,
                        1, 0, 0, uMin, vMin, brightness,
                };
            case 2: // Left (-X)
                return new float[]{
                        0, 0, 0, uMin, vMin, brightness,
                        0, 0, 1, uMax, vMin, brightness,
                        0, 1, 1, uMax, vMax, brightness,
                        0, 1, 1, uMax, vMax, brightness,
                        0, 1, 0, uMin, vMax, brightness,
                        0, 0, 0, uMin, vMin, brightness,
                };
            case 3: // Right (+X)
                return new float[]{
                        1, 0, 1, uMin, vMin, brightness,
                        1, 0, 0, uMax, vMin, brightness,
                        1, 1, 0, uMax, vMax, brightness,
                        1, 1, 0, uMax, vMax, brightness,
                        1, 1, 1, uMin, vMax, brightness,
                        1, 0, 1, uMin, vMin, brightness,
                };
            case 4: // Top (+Y)
                return new float[]{
                        0, 1, 1, uMin, vMin, brightness,
                        1, 1, 1, uMax, vMin, brightness,
                        1, 1, 0, uMax, vMax, brightness,
                        1, 1, 0, uMax, vMax, brightness,
                        0, 1, 0, uMin, vMax, brightness,
                        0, 1, 1, uMin, vMin, brightness,
                };
            case 5: // Bottom (-Y)
                return new float[]{
                        0, 0, 0, uMin, vMin, brightness,
                        1, 0, 0, uMax, vMin, brightness,
                        1, 0, 1, uMax, vMax, brightness,
                        1, 0, 1, uMax, vMax, brightness,
                        0, 0, 1, uMin, vMax, brightness,
                        0, 0, 0, uMin, vMin, brightness,
                };
            default:
                throw new IllegalArgumentException("Invalid face index: " + faceIndex);
        }
    }

    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }

    public static float[] createCube(float[] frontUV, float[] backUV, float[] leftUV, float[] rightUV, float[] topUV, float[] bottomUV) {
        return new float[]{
                // Front (+Z)
                -0.5f, -0.5f,  0.5f,  frontUV[0], frontUV[1],
                0.5f, -0.5f,  0.5f,  frontUV[2], frontUV[1],
                0.5f,  0.5f,  0.5f,  frontUV[2], frontUV[3],
                -0.5f, -0.5f,  0.5f,  frontUV[0], frontUV[1],
                0.5f,  0.5f,  0.5f,  frontUV[2], frontUV[3],
                -0.5f,  0.5f,  0.5f,  frontUV[0], frontUV[3],

                // Back (-Z)
                0.5f, -0.5f, -0.5f,  backUV[2], backUV[1],
                -0.5f, -0.5f, -0.5f,  backUV[0], backUV[1],
                -0.5f,  0.5f, -0.5f,  backUV[0], backUV[3],
                0.5f, -0.5f, -0.5f,  backUV[2], backUV[1],
                -0.5f,  0.5f, -0.5f,  backUV[0], backUV[3],
                0.5f,  0.5f, -0.5f,  backUV[2], backUV[3],

                // Left (-X)
                -0.5f, -0.5f, -0.5f,  leftUV[0], leftUV[1],
                -0.5f, -0.5f,  0.5f,  leftUV[2], leftUV[1],
                -0.5f,  0.5f,  0.5f,  leftUV[2], leftUV[3],
                -0.5f, -0.5f, -0.5f,  leftUV[0], leftUV[1],
                -0.5f,  0.5f,  0.5f,  leftUV[2], leftUV[3],
                -0.5f,  0.5f, -0.5f,  leftUV[0], leftUV[3],

                // Right (+X)
                0.5f, -0.5f,  0.5f,  rightUV[0], rightUV[1],
                0.5f, -0.5f, -0.5f,  rightUV[2], rightUV[1],
                0.5f,  0.5f, -0.5f,  rightUV[2], rightUV[3],
                0.5f, -0.5f,  0.5f,  rightUV[0], rightUV[1],
                0.5f,  0.5f, -0.5f,  rightUV[2], rightUV[3],
                0.5f,  0.5f,  0.5f,  rightUV[0], rightUV[3],

                // Top (+Y)
                -0.5f,  0.5f,  0.5f,  topUV[0], topUV[1],
                0.5f,  0.5f,  0.5f,  topUV[2], topUV[1],
                0.5f,  0.5f, -0.5f,  topUV[2], topUV[3],
                -0.5f,  0.5f,  0.5f,  topUV[0], topUV[1],
                0.5f,  0.5f, -0.5f,  topUV[2], topUV[3],
                -0.5f,  0.5f, -0.5f,  topUV[0], topUV[3],

                // Bottom (-Y)
                -0.5f, -0.5f, -0.5f,  bottomUV[0], bottomUV[1],
                0.5f, -0.5f, -0.5f,  bottomUV[2], bottomUV[1],
                0.5f, -0.5f,  0.5f,  bottomUV[2], bottomUV[3],
                -0.5f, -0.5f, -0.5f,  bottomUV[0], bottomUV[1],
                0.5f, -0.5f,  0.5f,  bottomUV[2], bottomUV[3],
                -0.5f, -0.5f,  0.5f,  bottomUV[0], bottomUV[3]
        };
    }

    public void setPosition(Vector3f position) {
        x = position.x;
        y = position.y;
        z = position.z;
    }

    public Block<T> copy(Block<T> block) {
        this.type = block.type;
        this.y = block.y;
        this.x = block.x;
        this.z = block.z;
        this.material = block.material;
        return this;
    }
}
