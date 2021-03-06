package com.stormdzh.openglanimation.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.util.LogUtil;
import com.stormdzh.openglanimation.util.shader.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 图片-着色器
 * 问题：线条的宽度改怎么处理呢？
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class AlphaPicRenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;
    private int texureId;

    private int avPosition;
    private int afPosition;
    private int sTexure;
    private int c_alpha;

    //顶点数据
    private float[] vertexData = {
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f

    };
    private FloatBuffer vertexBuffer;


    //纹理坐标
    private final float[] textureData = {

//            0f, 1f,
//            0f, 0f,
//            1, 1,
//            1f, 0


            1f, 0,
            1, 1,
            0f, 0f,
            0f, 1f

    };

    private FloatBuffer textureBuffer;


    public AlphaPicRenderer(Context context) {
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
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        LogUtil.i(TAG, "onSurfaceCreated");

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_pic_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_pic_alpha_shader);

        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afPosition = GLES20.glGetAttribLocation(program, "af_Position");
            sTexure = GLES20.glGetUniformLocation(program, "s_Texture");
            c_alpha = GLES20.glGetUniformLocation(program, "c_alpha");


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
        //控件的位置和大小 x,y,width,height
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        LogUtil.i(TAG, "onDrawFrame");

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0, 1);

        //1、使用源程序
        GLES20.glUseProgram(program);
        //2、使顶点属性数组有效
        GLES20.glEnableVertexAttribArray(avPosition);
        //3、给顶点属性赋值
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(afPosition);
        GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        if (System.currentTimeMillis() - lastTime > 50) {
            lastTime = System.currentTimeMillis();
            GLES20.glUniform1f(c_alpha,alpha);
            if (alpha >= 1) {
                alpha = 0.01f;
            } else {
                alpha += 0.01f;
            }
        }

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);
    }


    private long lastTime = 0;
    private float alpha = 0.01f;
}
