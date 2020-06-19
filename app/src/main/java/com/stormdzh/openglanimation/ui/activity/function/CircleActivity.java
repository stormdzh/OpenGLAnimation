package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.customview.DGLSurfaceView;
import com.stormdzh.openglanimation.renderer.CircleRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 圆形
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class CircleActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    //定点着色器
    public static String VL = "attribute vec4 vPosition;\n" +
            "uniform mat4 u_Matrix;"
            + "void main() {\n"
            + "  gl_Position = u_Matrix*vPosition;\n"
            + "}";
    //片段着色器
    public static String FL = "precision mediump float;\n" +
            "uniform vec4 u_Color;"
            + "void main() {\n"
            + "  gl_FragColor = u_Color;\n"
            + "}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        //设置使用OpenGL2.0版本
        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setRenderer(new RenderListener());
        mGLSurfaceView.setRenderer(new CircleRenderer(this));
        //设置渲染模式：requestRender方法触发会渲染
//        mGLSurfaceView.setRenderMode(DGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView.setRenderMode(DGLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(mGLSurfaceView);
    }


    class RenderListener implements GLSurfaceView.Renderer {

        FloatBuffer verticalsBuffer;
        float[] verticals = new float[120 * 3 * 3];
        float[] projectMatrix = new float[16];

        private int mProgram;
        private int mPositionHandle;
        private int mColorHandle;
        private int mMatricHandle;

        public RenderListener() {
            float x = 0;
            float y = 0;
            float z = 0;
            float r = 0.5f;
            int index = -1;
            for (int i = 3; i <= 360; i = i + 3) {
                double d1 = i * Math.PI / 180;
                verticals[++index] = 0;
                verticals[++index] = 0;
                verticals[++index] = 0;

                verticals[++index] = (float) (x + r * Math.cos(d1 - 3 * Math.PI / 180));
                verticals[++index] = (float) (y + r * Math.sin(d1 - 3 * Math.PI / 180));
                verticals[++index] = 0;

                verticals[++index] = (float) (x + r * Math.cos(d1));
                verticals[++index] = (float) (y + r * Math.sin(d1));
                verticals[++index] = 0;
            }
            verticalsBuffer = ByteBuffer.allocateDirect(verticals.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(verticals);
            verticalsBuffer.position(0);

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mProgram = GLES20.glCreateProgram();

            int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vertexShader, VL);
            GLES20.glCompileShader(vertexShader);

            int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fragmentShader, FL);
            GLES20.glCompileShader(fragmentShader);

            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);

            GLES20.glLinkProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
            mMatricHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
            //处理变形
            final float aspectRatio = width > height ? width * 1f / height : height * 1f / width;
            if (width > height) {
                Matrix.orthoM(projectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
            } else {
                Matrix.orthoM(projectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    12, verticalsBuffer);
            GLES20.glUniform4fv(mColorHandle, 1, new float[]{0, 1, 1, 5}, 0);
            GLES20.glUniformMatrix4fv(mMatricHandle, 1, false, projectMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 120 * 3);
            GLES20.glDisableVertexAttribArray(mPositionHandle);

        }
    }
}
