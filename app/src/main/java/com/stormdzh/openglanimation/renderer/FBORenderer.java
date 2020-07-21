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
 * @Description: 图片-FBO
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:28
 */
public class FBORenderer implements GLSurfaceView.Renderer {
    private String TAG = "LineRenderer";

    private Context mContext;
    private int program;
    private int texureId;
    private int imgtexureId;
    private int sampler;

    private int avPosition;
    private int afPosition;
    private int fboId;

    private FboRender mFboRender;

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

            1f, 0,
            1, 1,
            0f, 0f,
            0f, 1f

    };

    private FloatBuffer textureBuffer;


    public FBORenderer(Context context) {
        this.mContext = context;
        mFboRender=new FboRender(context);
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

        mFboRender.onCreate();
        LogUtil.i(TAG, "onSurfaceCreated");

        String vertexSource = ShaderUtil.readRawText(mContext, R.raw.vertex_pic_shader);
        String fragmentSource = ShaderUtil.readRawText(mContext, R.raw.fragment_pic_shader);

        program = ShaderUtil.creteProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afPosition = GLES20.glGetAttribLocation(program, "af_Position");
            sampler = GLES20.glGetUniformLocation(program, "sTexture");





            int []textureIds = new int[1];
            GLES20.glGenTextures(1, textureIds, 0);
            texureId = textureIds[0];

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texureId);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(sampler, 0);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);




            //---------------------创建FBO------------------------------------
            int fbos[] = new int[1];
            GLES20.glGenBuffers(1, fbos, 0);
            fboId=fbos[0];
            //绑定FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

            //设置FBO分配内存大小
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1080, 2119, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            //把纹理绑定到FBO
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texureId, 0);
            //检查FBO绑定是否成功
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                LogUtil.i("glbug","fbo 绑定失败");
            } else {
                LogUtil.i("glbug","fbo 绑定成功");
            }
            //解绑FBO
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//            ---------------------创建FBO------------------------------------




            //处理图片纹理
            int[] imgtextureIds = new int[1];
            //生成纹理
            GLES20.glGenTextures(imgtextureIds.length, imgtextureIds, 0);
            imgtexureId = imgtextureIds[0];
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imgtexureId);

            //设置环绕方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_opgl_test);
            if (bitmap == null) return;
            // 加载纹理到 OpenGL，读入 Bitmap 定义的位图数据，并把它复制到当前绑定的纹理对象
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }


        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mFboRender.onChange(width,height);
        LogUtil.i(TAG, "onSurfaceChanged");
        //控件的位置和大小 x,y,width,height
        GLES20.glViewport(0, 0, width, height);

        LogUtil.i("glbug","fbo width:"+width+" height:"+height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        LogUtil.i(TAG, "onDrawFrame");

//        开启fbo
        if(useFbo) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);
        }else{
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        }

        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0, 1);

        //1、使用源程序
        GLES20.glUseProgram(program);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imgtexureId);

        //2、使顶点属性数组有效
        GLES20.glEnableVertexAttribArray(avPosition);
        //3、给顶点属性赋值
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(afPosition);
        GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        if(useFbo) {
            //这行代码才会使离屏绘制渲染出来
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            mFboRender.onDraw(texureId);
        }
    }


    private boolean useFbo=true;
}
