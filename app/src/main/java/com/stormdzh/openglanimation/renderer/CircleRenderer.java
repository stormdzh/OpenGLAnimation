package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
 * @Description: 圆形-着色器
 * 问题：线条的宽度改怎么处理呢？
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class CircleRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;

    private int avPosition;
    private int afColor;
    private int avMatrix;

    float[] projectMatrix = new float[16];

    //顶点数据
    private float[] vertexData = new float[120 * 3 * 3];
    private FloatBuffer vertexBuffer;


    public CircleRenderer(Context context) {
        this.mContext = context;
        float x = 0;
        float y = 0;
        float z = 0;
        float r = 0.5f;
        int index = -1;
        for (int i = 3; i <= 360; i = i + 3) {
            double d1 = i * Math.PI / 180;
            vertexData[++index] = 0;
            vertexData[++index] = 0;
            vertexData[++index] = 0;

            vertexData[++index] = (float) (x + r * Math.cos(d1 - 3 * Math.PI / 180));
            vertexData[++index] = (float) (y + r * Math.sin(d1 - 3 * Math.PI / 180));
            vertexData[++index] = 0;

            vertexData[++index] = (float) (x + r * Math.cos(d1));
            vertexData[++index] = (float) (y + r * Math.sin(d1));
            vertexData[++index] = 0;
        }


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

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_triangle_matrix_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_triangle_shader);
        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            avMatrix = GLES20.glGetUniformLocation(program, "u_Matrix");
            afColor = GLES20.glGetUniformLocation(program, "af_Color");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        LogUtil.i(TAG, "onSurfaceChanged");
        //控件的位置和大小 x,y,width,height
        GLES20.glViewport(0, 0, width, height);

        //处理变形
        final float aspectRatio = width > height ? width * 1f / height : height * 1f / width;
        if (width > height) {
            Matrix.orthoM(projectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
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
        GLES20.glVertexAttribPointer(avPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        //给颜色赋值
        GLES20.glUniform4f(afColor, 1f, 0, 0, 1);
        GLES20.glUniformMatrix4fv(avMatrix, 1, false, projectMatrix, 0);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 360);


        GLES20.glDisableVertexAttribArray(avPosition);
    }
}
