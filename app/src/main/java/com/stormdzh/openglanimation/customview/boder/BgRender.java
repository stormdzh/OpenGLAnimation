package com.stormdzh.openglanimation.customview.boder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.stormdzh.openglanimation.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 背景
 * @Author: dzh
 * @CreateDate: 2020-06-23 18:00
 */
public class BgRender {

    private int backguardTexture = GLESTools.NO_TEXTURE;

    private ShortBuffer drawIndecesBuffer;
    private FloatBuffer shapeBuffer;
    private FloatBuffer textrueBuffer;
    //计算 物体实际的渲染位置
    private float squareVertices[] = {
            -0.8f, 0.8f,
            -0.8f, -0.8f,
            0.8f, -0.8f,
            0.8f, 0.8f
    };

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

    private Bitmap bitmap;
    private Context mContext;
    private float setp = 1.02f;
    private boolean isSurfaceChanged = false;

    public BgRender(Context context) {
        mContext = context;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initBuffer();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if (!isSurfaceChanged) {
            isSurfaceChanged = true;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.focus_border_bg);
//        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_opgl_test);
            backguardTexture = GLESTools.loadTexture(bitmap, GLESTools.NO_TEXTURE);

            //设置环绕方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            glProgram = GLESTools.createProgram(vertexShader_filter, fragmentShader_filter);
            GLES20.glUseProgram(glProgram);
            glBgTextureLoc = GLES20.glGetUniformLocation(glProgram, "uBgTexture");
            glBgPostionLoc = GLES20.glGetAttribLocation(glProgram, "aBgPosition");
            glBgTextureCoordLoc = GLES20.glGetAttribLocation(glProgram, "aBgTextureCoord");

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public void onDrawFrame(GL10 gl) {
        updateBgSize();

        //draw bg
        GLES20.glUseProgram(glProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backguardTexture);
        GLES20.glUniform1i(glBgTextureLoc, 0);

        GLHelper.enableVertex(glBgPostionLoc, glBgTextureCoordLoc, shapeBuffer, textrueBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);

        GLES20.glFinish();

        GLHelper.disableVertex(glBgPostionLoc, glBgTextureCoordLoc);

    }

    public void updateBgSize() {

        float v = shapeBuffer.get(0);
        if (Math.abs(v) >= 2.2) {
            for (int i = 0; i < 8; i++) {
                shapeBuffer.put(i, squareVertices[i]);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                shapeBuffer.put(i, shapeBuffer.get(i) * setp);
            }
        }
        shapeBuffer.position(0);
    }

    private void initBuffer() { //初始化坐标顶点 与 纹理顶点
        drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
        shapeBuffer = GLHelper.getShapeVerticesBuffer();
        textrueBuffer = GLHelper.getScreenTextureVerticesBuffer();
    }


    public void destroy() {
        if (drawIndecesBuffer != null) {
            drawIndecesBuffer.clear();
            drawIndecesBuffer = null;
        }
        if (shapeBuffer != null) {
            shapeBuffer.clear();
            shapeBuffer = null;
        }

        if (textrueBuffer != null) {
            textrueBuffer.clear();
            textrueBuffer = null;
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
