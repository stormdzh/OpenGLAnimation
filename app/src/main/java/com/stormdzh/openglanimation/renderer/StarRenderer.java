package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.util.LogUtil;
import com.stormdzh.openglanimation.util.shader.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 星星-着色器
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class StarRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;

    private int avPosition;
    private int afColor;

    //顶点数据
    private float[] vertexData = {

            //中间
            -0.5f, 0f,
            -0.5f, -0.5f,
            0.5f, 0f,

            0.5f, 0f,
            -0.5f, -0.5f,
            0.5f, -0.5f,

            //上
            0f, 0.5f,
            -0.5f, 0f,
            0.5f, 0f,

            //左
            -0.5f, 0f,
            -1.0f, 0f,
            -0.5f, -0.5f,

            //左下
            -0.5f, -0.5f,
            -0.75f, -1f,
            -0f, -0.5f,

            //右下
            0f, -0.5f,
            0.75f, -1f,
            0.5f, -0.5f,

            //右边
            0.5f, -0.5f,
            1f, 0f,
            0.5f, 0f


    };
    private FloatBuffer vertexBuffer;


    public StarRenderer(Context context) {
        this.mContext = context;
        //顶点数据转成ByteBuffer  乘4是因为floa是4个字节
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtil.i(TAG, "onSurfaceCreated");

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_triangle_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_triangle_shader);
        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afColor = GLES20.glGetUniformLocation(program, "af_Color");
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
        GLES20.glClearColor(0, 0, 0, 1);

        //1、使用源程序
        GLES20.glUseProgram(program);

        //2、使顶点属性数组有效
        GLES20.glEnableVertexAttribArray(avPosition);

        //3、给顶点属性赋值
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        //给颜色赋值
        GLES20.glUniform4f(afColor, 1f, 0f, 0f, 1);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);
    }
}
