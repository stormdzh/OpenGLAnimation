package com.stormdzh.openglanimation.customview.boder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 绘制控件
 * @Author: dzh
 * @CreateDate: 2020-06-24 17:13
 */
public class FocusViewRenderer {

    private View drawView;
    private static final int ROOT_TEX_ID = 11;
    private SurfaceTexture rootViewSufaceTexture;
    private Surface rootViewSuface;

    private float squareVertices[];
    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer shapeBuffer;
    private FloatBuffer textrueBuffer;

    private int glViewProgram;
    private int glViewTextureLoc;
    private int glViewPostionLoc;
    private int glViewTextureCoordLoc;

    private String viewVertexShader_filter = "" +
            "attribute vec4 aViewPosition;\n" +
            "attribute vec2 aViewTextureCoord;\n" +
            "varying vec2 vViewTextureCoord;\n" +
            "void main(){\n" +
            "    gl_Position= aViewPosition;\n" +
            "    vViewTextureCoord = aViewTextureCoord;\n" +
            "}";

    private String viewFragmentshader_filter = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying mediump vec2 vViewTextureCoord;\n" +
            "uniform samplerExternalOES uViewTexture;\n" +
            "void main(){\n" +
            "    vec4  c1 = texture2D(uViewTexture, vec2(vViewTextureCoord.x,1.0-vViewTextureCoord.y));\n" +
            "    lowp vec4 outputColor = c1;\n" +
            "    gl_FragColor = outputColor;\n" +
            "}";

    public FocusViewRenderer() {

    }

    private void initBuffer() { //初始化坐标顶点 与 纹理顶点

        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        squareVertices = Arrays.copyOf(GLHelper.SquareVertices, GLHelper.SquareVertices.length);

        shapeBuffer = GLHelper.getShapeVerticesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initBuffer();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {

        rootViewSufaceTexture = new SurfaceTexture(ROOT_TEX_ID);
        rootViewSuface = new Surface(rootViewSufaceTexture);

        glViewProgram = GLESTools.createProgram(viewVertexShader_filter, viewFragmentshader_filter);
        GLES20.glUseProgram(glViewProgram);
        glViewTextureLoc = GLES20.glGetUniformLocation(glViewProgram, "uViewTexture");

        glViewPostionLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewPosition");
        glViewTextureCoordLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewTextureCoord");

    }


    public void onDrawFrame(GL10 gl) {

        //绘制图片
        if (drawView != null && viewIsChange) {
            viewIsChange = false;
            rootViewSufaceTexture.setDefaultBufferSize(drawView.getMeasuredWidth(), drawView.getMeasuredHeight());
            squareVertices = Arrays.copyOf(GLHelper.SquareVertices, GLHelper.SquareVertices.length);
            updateViewLoacation();

            updateRunderViewTexture();

        }

        //draw texture
        GLES20.glUseProgram(glViewProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, ROOT_TEX_ID);
        GLES20.glUniform1i(glViewTextureLoc, 0);

        GLHelper.enableVertex(glViewPostionLoc, glViewTextureCoordLoc, shapeBuffer, textrueBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);
        GLES20.glFinish();

        GLHelper.disableVertex(glViewPostionLoc, glViewTextureCoordLoc);

    }

    private boolean viewIsChange = true;

    public void setRunderView(View view) {
        viewIsChange = true;
        drawView = view;
    }

    public void updateRunderViewTexture() {
        //drawview  to Texture
        Canvas canvas = rootViewSuface.lockCanvas(null);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawView.draw(canvas);
        rootViewSuface.unlockCanvasAndPost(canvas);
        rootViewSufaceTexture.updateTexImage();
    }


    private float bubbleWidthPer = 0.82f;//气泡占据顶点坐标的百分比
    private float bubbleHeightPer = 0.82f;//气泡占据顶点坐标的百分比

    public void updateViewLoacation() {

        if (squareVertices == null) return;
        Log.i("adu","焦点控件中   bubbleWidthPer："+bubbleWidthPer+   "  bubbleHeightPer:"+bubbleHeightPer);
        squareVertices[0] = squareVertices[0] * bubbleWidthPer;
        squareVertices[1] = squareVertices[1] * bubbleHeightPer;
        squareVertices[2] = squareVertices[2] * bubbleWidthPer;
        squareVertices[3] = squareVertices[3] * bubbleHeightPer;
        squareVertices[4] = squareVertices[4] * bubbleWidthPer;
        squareVertices[5] = squareVertices[5] * bubbleHeightPer;
        squareVertices[6] = squareVertices[6] * bubbleWidthPer;
        squareVertices[7] = squareVertices[7] * bubbleHeightPer;

        shapeBuffer.clear();
        shapeBuffer.put(squareVertices);
        shapeBuffer.position(0);


    }

    public void setBublePer(float bubbleWidthPer, float bubbleHeightPer) {
        this.bubbleWidthPer = 1 - bubbleWidthPer;
        this.bubbleHeightPer = 1 - bubbleHeightPer;
    }
}
