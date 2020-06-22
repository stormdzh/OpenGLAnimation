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
 * @Description: 点-着色器
 * 问题：线条的宽度改怎么处理呢？
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class PointRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;

    private int avPosition;
    private int afColor;
    private int pSize;

    //顶点数据
    private float[] vertexData = {
            0f, 0f,
            0.5f, 0.5f
    };
    private FloatBuffer vertexBuffer;


    public PointRenderer(Context context) {
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

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_point_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_triangle_shader);
        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afColor = GLES20.glGetUniformLocation(program, "af_Color");
            pSize = GLES20.glGetUniformLocation(program, "p_Size");
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
        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0, 1);

        //1、使用源程序
        GLES20.glUseProgram(program);

        //2、使顶点属性数组有效
        GLES20.glEnableVertexAttribArray(avPosition);
        //3、给顶点属性赋值
        //第一个参数指定从索引0开始取数据，与顶点着色器中layout(location=0)对应。
        //第二个参数指定顶点属性大小,目前shi二维的只有x、y,所以是2
        //第三个参数指定数据类型。
        //第四个参数定义是否希望数据被标准化（归一化），只表示方向不表示大小。
        //第五个参数是步长（Stride），指定在连续的顶点属性之间的间隔。上面传0和传4效果相同，如果传1取值方式为0123、1234、2345……
        //第六个参数表示我们的位置数据在缓冲区起始位置的偏移量。
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        //给颜色赋值
        GLES20.glUniform4f(afColor, 1f, 0, 0, 1);

        GLES20.glUniform1f(pSize, 20.0f);

        //绘制
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexData.length / 2);
    }
}
