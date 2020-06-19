package com.stormdzh.openglanimation.customview.gt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.View;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * Created by lifuhao on 19/6/18.
 * gl 渲染view
 */
public class RenderBorderView {
    private View drawView;
    private static final int ROOT_TEX_ID = 11;
    private SurfaceTexture rootViewSufaceTexture;
    private Surface rootViewSuface;

    private float squareVertices[];
    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer shapeBuffer;
    private FloatBuffer textrueBuffer;

    //计算 物体实际的渲染位置
    private RectF showRectF;
    private float pixVerticesW, pixVerticesH;


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


    public void onInit(int width, int height) {
        pixVerticesW = 2.0f / width;
        pixVerticesH = 2.0f / height;

        initBuffer();

        rootViewSufaceTexture = new SurfaceTexture(ROOT_TEX_ID);
        rootViewSuface = new Surface(rootViewSufaceTexture);

        glViewProgram = GLESTools.createProgram(viewVertexShader_filter, viewFragmentshader_filter);
        GLES20.glUseProgram(glViewProgram);
        glViewTextureLoc = GLES20.glGetUniformLocation(glViewProgram, "uViewTexture");

        glViewPostionLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewPosition");
        glViewTextureCoordLoc = GLES20.glGetAttribLocation(glViewProgram, "aViewTextureCoord");
    }

    private void initBuffer() { //初始化坐标顶点 与 纹理顶点

        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        squareVertices = Arrays.copyOf(GLHelper.SquareVertices, GLHelper.SquareVertices.length);

        shapeBuffer = GLHelper.getShapeVerticesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }


    public void setRunderView(View view) {
        drawView = view;
        rootViewSufaceTexture.setDefaultBufferSize(drawView.getMeasuredWidth(), drawView.getMeasuredHeight());
        updateViewLoacation();
    }

    public void updateRunderViewTexture() {
        //drawview  to Texture
        Canvas canvas = rootViewSuface.lockCanvas(null);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawView.draw(canvas);
        rootViewSuface.unlockCanvasAndPost(canvas);
        rootViewSufaceTexture.updateTexImage();
    }


    public void updateViewLoacation() {

        showRectF = FocusViewTools.getFocusRect(drawView, pixVerticesW, pixVerticesH);

        squareVertices[0] = showRectF.left;
        squareVertices[1] = showRectF.top;

        squareVertices[2] = showRectF.left;
        squareVertices[3] = showRectF.bottom;

        squareVertices[4] = showRectF.right;
        squareVertices[5] = showRectF.bottom;

        squareVertices[6] = showRectF.right;
        squareVertices[7] = showRectF.top;
        shapeBuffer.clear();
        shapeBuffer.put(squareVertices);
        shapeBuffer.position(0);


    }

    public void onDraw() {

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


    public void onDestroy() {
        rootViewSuface.release();
        rootViewSufaceTexture.release();
    }


}
