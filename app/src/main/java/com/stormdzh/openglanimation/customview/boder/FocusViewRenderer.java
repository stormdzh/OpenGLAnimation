package com.stormdzh.openglanimation.customview.boder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
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

    //计算 物体实际的渲染位置
//    private RectF showRectF;
//    private float pixVerticesW, pixVerticesH;


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

//        pixVerticesW = width / width;
//        pixVerticesH = height / height;
//        pixVerticesW = 2.0f / width;
//        pixVerticesH = 2.0f / height;

        rootViewSufaceTexture = new SurfaceTexture(ROOT_TEX_ID);
        rootViewSuface = new Surface(rootViewSufaceTexture);

        glViewProgram = GLESTools.createProgram(viewVertexShader_filter, viewFragmentshader_filter);
        GLES20.glUseProgram(glViewProgram);
        glViewTextureLoc = GLES20.glGetUniformLocation(glViewProgram, "uViewTexture");

        glViewPostionLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewPosition");
        glViewTextureCoordLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewTextureCoord");

    }

    boolean isSetImg = false;

    public void onDrawFrame(GL10 gl) {

        //绘制图片
        if (drawView != null && !isSetImg) {
            isSetImg = true;
            rootViewSufaceTexture.setDefaultBufferSize(drawView.getMeasuredWidth(), drawView.getMeasuredHeight());
            updateViewLoacation();

            updateRunderViewTexture();

        }

//        if (drawView != null) {
//            updateRunderViewTexture();
//        }

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

    public void setRunderView(View view) {
        drawView = view;
//        if(rootViewSufaceTexture!=null) {
//            rootViewSufaceTexture.setDefaultBufferSize(drawView.getMeasuredWidth(), drawView.getMeasuredHeight());
//        }
//        updateViewLoacation();
    }

    public void updateRunderViewTexture() {
        //drawview  to Texture
        Canvas canvas = rootViewSuface.lockCanvas(null);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawView.draw(canvas);
        rootViewSuface.unlockCanvasAndPost(canvas);
        rootViewSufaceTexture.updateTexImage();
    }


    private float per = 0.82f;

    public void updateViewLoacation() {

//        showRectF = FocusViewTools.getFocusRect(drawView, pixVerticesW, pixVerticesH);

//        squareVertices[0] = showRectF.left;
//        squareVertices[1] = showRectF.top;
//
//        squareVertices[2] = showRectF.left;
//        squareVertices[3] = showRectF.bottom;
//
//        squareVertices[4] = showRectF.right;
//        squareVertices[5] = showRectF.bottom;
//
//        squareVertices[6] = showRectF.right;
//        squareVertices[7] = showRectF.top;

        if (squareVertices == null) return;
        squareVertices[0] = squareVertices[0] * per;
        squareVertices[1] = squareVertices[1] * per;
        squareVertices[2] = squareVertices[2] * per;
        squareVertices[3] = squareVertices[3] * per;
        squareVertices[4] = squareVertices[4] * per;
        squareVertices[5] = squareVertices[5] * per;
        squareVertices[6] = squareVertices[6] * per;
        squareVertices[7] = squareVertices[7] * per;

        shapeBuffer.clear();
        shapeBuffer.put(squareVertices);
        shapeBuffer.position(0);


    }
}
