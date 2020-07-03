package com.stormdzh.openglanimation.customview.stickers;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.stormdzh.openglanimation.util.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 贴纸
 * @Author: dzh
 * @CreateDate: 2020-07-03 10:32
 */
public class StickersRenderer implements GLSurfaceView.Renderer {

    static final String TAG = "StickersRenderer";
    private float[] vertexPoints = new float[]{
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, 1f, 0f
    };

    private static final float[] TEX_VERTEX = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };

    private final float[] uMVPMatrix = new float[16];
    private final float[] uTexMatrix = new float[16];

    final String vertextglsl = "#version 300 es \n" +
            "layout (location = 0) in vec4 aposition;" +
            "layout (location = 1) in vec4 aTextureCoord;" +
            "out vec2 vTexCoord;" +
            "uniform mat4 uMVPMatrix;" +
            "uniform mat4 uTexMatrix;" +


            "void main(){ " +
            "gl_Position=aposition*uMVPMatrix;" +
            " vTexCoord = (uTexMatrix*aTextureCoord).xy;\n" +

            "}";
    final String fragmeneShader = "#version 300 es \n" +
            "#extension GL_OES_EGL_image_external_essl3 : require\n" +
            "precision mediump float; " +
            "uniform float alphac;" +
            "out vec4 vFragColor ;" +
            "uniform samplerExternalOES  yuvTexSampler;\n" +
            "in vec2 vTexCoord;\n " +
            "void main() { " +
            "vFragColor  = vec4(texture(yuvTexSampler,vTexCoord).rgb,alphac);" +
            "}";

    int alphaLocation;
    int textureId;
    RenderCallBack renderCallBack;
    FloatBuffer vertextbuffer;
    FloatBuffer texturebuffer;
    private int uMVPMatrixLocation;
    private int uTexMatrixLocation;
    SurfaceTexture mSurfaceTexture;
    GLSurfaceView glSurfaceView;
    Context ctx;
    int pid;
    int width, height;
    int x = -400;


    public interface RenderCallBack {
        void onCreate();

        void onChange();

        void onTextureLoad(SurfaceTexture surfaceTexture);

        void onDraw();

        void onDestory();
    }

    public StickersRenderer(Context context, GLSurfaceView surfaceView) {
        this.ctx = context;
        this.glSurfaceView = surfaceView;

        vertextbuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertextbuffer.put(vertexPoints);
        vertextbuffer.position(0);

        texturebuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturebuffer.put(TEX_VERTEX);
        texturebuffer.position(0);
    }

    public boolean loadSurfaceTexture(int textureId) {
        //根据纹理ID创建SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                // 渲染帧数据
                glSurfaceView.requestRender();
            }
        });
        //SurfaceTexture作为相机预览输出
        Log.d(TAG, "camera not null");
        if (renderCallBack != null) {
            renderCallBack.onTextureLoad(mSurfaceTexture);
        }

        Log.d(TAG, "camera mSurfaceTexture");

        glSurfaceView.requestRender();
        //开启相机预览
        return true;
    }

    public void setRenderCallBack(RenderCallBack renderCallBack) {
        this.renderCallBack = renderCallBack;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES30.glClearColor(0f, 0f, 0f, 0f);
        int vertextid = ShaderUtils.createShader(GLES30.GL_VERTEX_SHADER, vertextglsl);
        int fragmentid = ShaderUtils.createShader(GLES30.GL_FRAGMENT_SHADER, fragmeneShader);


        pid = ShaderUtils.createProgram(vertextid, fragmentid);
        uMVPMatrixLocation = GLES30.glGetUniformLocation(pid, "uMVPMatrix");
        uTexMatrixLocation = GLES30.glGetUniformLocation(pid, "uTexMatrix");
        alphaLocation = GLES30.glGetUniformLocation(pid, "alphac");
        textureId = TextureUtils.loadOESTexture();
        loadSurfaceTexture(textureId);

        if (renderCallBack != null) {
            renderCallBack.onCreate();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        LogUtil.d(TAG, "onSurfaceChanged  width:"+width+"   height:"+height);
        SpriteManager.getInstance().setCoordinateConvert(width, height);
        this.width = width;
        this.height = height;
        GLES30.glViewport(0, 0, width, height);
        if (renderCallBack != null) {
            renderCallBack.onChange();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(pid);
        GLES30.glViewport(0, 0, width, height);
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(uTexMatrix);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        Matrix.setIdentityM(uMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, uMVPMatrix, 0);
        GLES30.glUniformMatrix4fv(uTexMatrixLocation, 1, false, uTexMatrix, 0);
        //绑定外部纹理到纹理单元0
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertextbuffer);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, texturebuffer);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        x += 20;
        if (x > width) {
            x = -400;
        }
        SpriteManager.getInstance().drawEach();
        if (renderCallBack != null) {
            renderCallBack.onDraw();
        }
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
    }
}
