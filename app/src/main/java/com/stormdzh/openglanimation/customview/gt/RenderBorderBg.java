package com.stormdzh.openglanimation.customview.gt;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.View;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by lifuhao on 19/6/18.
 * gl 渲染 呼吸背景
 *
 */
public class RenderBorderBg {
    private View drawView;

    private int backguardTexture =  GLESTools.NO_TEXTURE;

    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer shapeBuffer;
    private FloatBuffer textrueBuffer;
    //计算 物体实际的渲染位置
    private float squareVertices[];
    private RectF showRectF;
    private float pixVerticesW,pixVerticesH;
    //呼吸效果
    //动画100帧
    private final int duration = 100;

    private final float minScale = 1.8f;
    private final float maxScale = 2.2f;
    private float frameScale = (maxScale - minScale) / duration;

    private float mBgScale = minScale;

    private boolean isToBig = true;
    private long drawCont = 0;



    private int glProgram;
    private int glBgTextureLoc;
    private int glBgPostionLoc;
    private int glBgTextureCoordLoc;
    private String vertexShader_filter = "" +
            "attribute vec4 aBgPosition;\n" +
            "attribute vec2 aBgTextureCoord;\n" +
            "varying vec2 vBgTextureCoord;\n" +
            "void main(){\n" +
            "   gl_Position= aBgPosition;\n" +
            "   vBgTextureCoord = aBgTextureCoord;\n" +
            "}";

    private String fragmentShader_filter = "" +
            "precision mediump float;\n" +
            "varying mediump vec2 vBgTextureCoord;\n" +
            "uniform sampler2D uBgTexture;\n" +
            "void main(){\n" +
            "   lowp vec4 c1 = texture2D(uBgTexture,vec2(vBgTextureCoord.x,1.0-vBgTextureCoord.y));\n" +
            "   lowp vec4 outputColor = c1;\n" +
            "   gl_FragColor = outputColor;\n" +
            "}";


    public void onInit( int width, int height, Bitmap bitmap) {

        pixVerticesW = 2.0f / width;
        pixVerticesH =  2.0f / height;

        initBuffer();

        backguardTexture = GLESTools.loadTexture(bitmap, GLESTools.NO_TEXTURE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        glProgram = GLESTools.createProgram(vertexShader_filter, fragmentShader_filter);
        GLES20.glUseProgram(glProgram);
        glBgTextureLoc = GLES20.glGetUniformLocation(glProgram, "uBgTexture");
        glBgPostionLoc = GLES20.glGetAttribLocation(glProgram, "aBgPosition");
        glBgTextureCoordLoc = GLES20.glGetAttribLocation(glProgram, "aBgTextureCoord");

    }

    private void initBuffer() { //初始化坐标顶点 与 纹理顶点
        squareVertices = GLHelper.SquareVertices;
        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        shapeBuffer = GLHelper.getShapeVerticesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }



    public void setRunderView(View view) {
        drawView = view;
        drawCont = 0;
        mBgScale = minScale;
        isToBig = true;
    }


    public void updateViewLoacation(){
        if (drawCont >= duration){
            drawCont = 0;
            isToBig = !isToBig;
        }

        if (isToBig){
            mBgScale += frameScale;
        }else {
            mBgScale -= frameScale;
        }
        showRectF = FocusViewTools.getRect(drawView,mBgScale,pixVerticesW,pixVerticesH);


        squareVertices[0] = showRectF.left;
        squareVertices[1] = showRectF.top;

        squareVertices[2] = showRectF.left;
        squareVertices[3] = showRectF.bottom;

        squareVertices[4] = showRectF.right;
        squareVertices[5] = showRectF.bottom;

        squareVertices[6] = showRectF.right;
        squareVertices[7] = showRectF.top;

        shapeBuffer.put(squareVertices);
        shapeBuffer.position(0);
    }




    public void onDraw() {
        updateViewLoacation();

        //draw bg
        GLES20.glUseProgram(glProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backguardTexture);
        GLES20.glUniform1i(glBgTextureLoc, 0);


        GLHelper.enableVertex(glBgPostionLoc, glBgTextureCoordLoc, shapeBuffer,textrueBuffer);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);

        GLES20.glFinish();

        GLHelper.disableVertex(glBgPostionLoc, glBgTextureCoordLoc);

        drawCont ++;


    }





}
