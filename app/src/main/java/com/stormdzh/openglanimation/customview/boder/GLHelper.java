package com.stormdzh.openglanimation.customview.boder;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLHelper {


    private static short drawIndices[] = {0, 1, 2, 0, 2, 3};

    public static float SquareVertices[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f};

    public static float ScreenTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    public static int FLOAT_SIZE_BYTES = 4;
    public static int SHORT_SIZE_BYTES = 2;
    public static int COORDS_PER_VERTEX = 2;
    public static int TEXTURE_COORDS_PER_VERTEX = 2;


    public static void enableVertex(int posLoc, int texLoc, FloatBuffer shapeBuffer, FloatBuffer texBuffer) {
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glEnableVertexAttribArray(texLoc);
        GLES20.glVertexAttribPointer(posLoc, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, shapeBuffer);
        GLES20.glVertexAttribPointer(texLoc, TEXTURE_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEXTURE_COORDS_PER_VERTEX * 4, texBuffer);
    }

    public static void updateVertex(int posLoc, int texLoc, FloatBuffer shapeBuffer, FloatBuffer texBuffer) {
        GLES20.glVertexAttribPointer(posLoc, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, shapeBuffer);
        GLES20.glVertexAttribPointer(texLoc, TEXTURE_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEXTURE_COORDS_PER_VERTEX * 4, texBuffer);
    }

    public static void disableVertex(int posLoc, int texLoc) {
        GLES20.glDisableVertexAttribArray(posLoc);
        GLES20.glDisableVertexAttribArray(texLoc);
    }


    public static ShortBuffer getDrawIndecesBuffer() {
        ShortBuffer result = ByteBuffer.allocateDirect(SHORT_SIZE_BYTES * drawIndices.length).
                order(ByteOrder.nativeOrder()).
                asShortBuffer();
        result.put(drawIndices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getShapeVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * SquareVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(SquareVertices);
        result.position(0);
        return result;
    }


    public static FloatBuffer getScreenTextureVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * ScreenTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(ScreenTextureVertices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getShapeVerticesBuffer(float SquareVertices[]) {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * SquareVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(SquareVertices);
        result.position(0);
        return result;
    }


    public static FloatBuffer getScreenTextureVerticesBuffer(float ScreenTextureVertices[]) {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * ScreenTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(ScreenTextureVertices);
        result.position(0);
        return result;
    }

    private static float flip(final float i) {
        return (1.0f - i);
    }


}