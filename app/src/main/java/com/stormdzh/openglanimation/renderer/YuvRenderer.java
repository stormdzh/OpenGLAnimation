package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.util.LogUtil;
import com.stormdzh.openglanimation.util.shader.ShaderUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: YUV-着色器
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class YuvRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;

    //顶点数据
    private float[] vertexData = {
            1f, 1f,
            -1f, 1f,
            1f, -1f,
            -1f, -1f
    };
    private FloatBuffer vertexBuffer;

    private FloatBuffer textureBuffer;
    //纹理坐标
    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
    };

    private int[] texture_yuv;

    private int program;

    private int vPosition;
    private int fPosition;

    private int sampler_y;
    private int sampler_u;
    private int sampler_v;

    private int u_matrix;


    public YuvRenderer(Context context) {
        this.mContext = context;
        //顶点数据转成ByteBuffer  乘4是因为floa是4个字节
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);


        textureBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureBuffer.position(0);
//        Matrix.setIdentityM(matrix, 0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtil.i(TAG, "onSurfaceCreated");

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_yuv_simple_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_yuv_shader);
        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            vPosition = GLES20.glGetAttribLocation(program, "v_Position");
            fPosition = GLES20.glGetAttribLocation(program, "f_Position");
            u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix");

            sampler_y = GLES20.glGetUniformLocation(program, "sampler_y");
            sampler_u = GLES20.glGetUniformLocation(program, "sampler_u");
            sampler_v = GLES20.glGetUniformLocation(program, "sampler_v");


            texture_yuv = new int[3];
            GLES20.glGenTextures(3, texture_yuv, 0);
            for (int i = 0; i < 3; i++) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[i]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        LogUtil.i(TAG, "onSurfaceChanged");
        //控件的位置和大小 x,y,width,height
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        LogUtil.i(TAG, "onDrawFrame");
        //清屏-清空fbo
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1, 0, 0, 1);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        if (w > 0 && h > 0) {
            //1、使用源程序
            GLES20.glUseProgram(program);

            //2、使顶点属性数组有效
            GLES20.glEnableVertexAttribArray(vPosition);

            //3、给顶点属性赋值
            GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);


            GLES20.glEnableVertexAttribArray(fPosition);
            GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);


            if (y != null) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
                GLES20.glUniform1i(sampler_y, 0);
            }

            if (u != null) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[1]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                        u);
                GLES20.glUniform1i(sampler_u, 1);
            }

            if (v != null) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[2]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                        v);
                GLES20.glUniform1i(sampler_v, 2);
            }

            if (y != null)
                y.clear();
            if (u != null)
                u.clear();
            if (v != null)
                v.clear();

            y = null;
            u = null;
            v = null;
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    int w;
    int h;

    Buffer y;
    Buffer u;
    Buffer v;

    public void setYUVData(int w, int h, byte[] by, byte[] bu, byte[] bv) {

        this.w = w;
        this.h = h;
        this.y = ByteBuffer.wrap(by);
        this.u = ByteBuffer.wrap(bu);
        this.v = ByteBuffer.wrap(bv);
    }
}
