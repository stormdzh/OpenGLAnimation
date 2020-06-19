package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.util.LogUtil;
import com.stormdzh.openglanimation.util.shader.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 图片移动-着色器  这个貌似不是常规的做法，需要在调研
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class TranslateRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;
    private int texureId;

    private int avPosition;
    private int afPosition;
    private int sTexure;

    //顶点数据
    private float[] vertexData = {
            -0.1f, 0.1f,
            -0.1f, -0.1f,
            0.1f, 0.1f,
            0.1f, -0.1f
    };
    private FloatBuffer vertexBuffer;


    //纹理坐标
    private final float[] textureData = {
            1f, 0,
            1, 1,
            0f, 0f,
            0f, 1f

    };

    private FloatBuffer textureBuffer;


    private int umatrix;
    private float[] matrix = new float[16];

    private int screenWidth;
    private int screenHeight;

    public TranslateRenderer(Context context) {
        this.mContext = context;
        //顶点数据转成ByteBuffer  乘4是因为floa是4个字节
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

        Matrix.setIdentityM(matrix, 0);

    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtil.i(TAG, "onSurfaceCreated");

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_pic_translate_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_pic_shader);

        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afPosition = GLES20.glGetAttribLocation(program, "af_Position");
            sTexure = GLES20.glGetUniformLocation(program, "s_Texture");

            umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");


            //处理纹理
            int[] textureIds = new int[1];
            //生成纹理
            GLES20.glGenTextures(textureIds.length, textureIds, 0);
            texureId = textureIds[0];
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texureId);

            //设置环绕方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_opgl_test);
            if (bitmap == null) return;
            //设置图片
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        LogUtil.i(TAG, "onSurfaceChanged");
        this.screenWidth = width;
        this.screenHeight = height;
        //控件的位置和大小 x,y,width,height
        GLES20.glViewport(0, 0, width, height);
    }


    private float setp = 0.01f;

    @Override
    public void onDrawFrame(GL10 gl10) {
        LogUtil.i(TAG, "onDrawFrame");
        //清屏-清空fbo
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0, 1);

        //1、使用源程序
        GLES20.glUseProgram(program);

        GLES20.glViewport(0, 0, screenWidth, screenHeight);


        if (System.currentTimeMillis() - lastTime > 10) {
            lastTime = System.currentTimeMillis();

            vertexBuffer.put(0, vertexBuffer.get(0) + setp);
            vertexBuffer.put(2, vertexBuffer.get(2) + setp);
            vertexBuffer.put(4, vertexBuffer.get(4) + setp);
            vertexBuffer.put(6, vertexBuffer.get(6) + setp);
            vertexBuffer.position(0);

        }

//        GLES20.glUniformMatrix4fv(umatrix, 1, false, matrix, 0);
        GLES20.glUniformMatrix4fv(umatrix, 1, false, matrix, 0);

        GLES20.glEnableVertexAttribArray(avPosition);
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(afPosition);
        GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        //绘制
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);
    }

    private long lastTime = 0;
}
