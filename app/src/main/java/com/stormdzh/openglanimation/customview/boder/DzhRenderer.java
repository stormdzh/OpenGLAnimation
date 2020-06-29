package com.stormdzh.openglanimation.customview.boder;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.stormdzh.openglanimation.util.LogUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 边框renderer
 * @Author: dzh
 * @CreateDate: 2020-06-23 17:35
 */
public class DzhRenderer implements GLSurfaceView.Renderer {

    private BgRender BgRender;
    private BubbleRenderer mBubbleRenderer;
    private FocusViewRenderer mFocusViewRenderer;

    private int mWidth, mHeight;
    private Context mContext;

    public DzhRenderer(Context context) {
        this.mContext = context;

        BgRender = new BgRender(mContext);
        mBubbleRenderer = new BubbleRenderer(mContext);
        mFocusViewRenderer = new FocusViewRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

//        LogUtil.i("adu", "render surfaceChanged");
        GLES20.glClearColor(0, 0, 0, 0.1f);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glDisable(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        BgRender.onSurfaceCreated(gl, config);
        mBubbleRenderer.onSurfaceCreated(gl, config);
        mFocusViewRenderer.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtil.i("adu", "render onSurfaceChanged  width:"+width+"   height:"+height);
        this.mWidth = width;
        this.mHeight = height;
        GLES20.glViewport(0, 0, mWidth, mHeight);
        BgRender.onSurfaceChanged(gl, width, height);
        mBubbleRenderer.onSurfaceChanged(gl, width, height);
        mFocusViewRenderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        LogUtil.i("adu","render onDrawFrame");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
                | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glViewport(0, 0, mWidth, mHeight);
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.1f);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        BgRender.onDrawFrame(gl);
        mBubbleRenderer.onDrawFrame(gl);
        mFocusViewRenderer.onDrawFrame(gl);

    }

    public void setFocusView(View focusView) {
        mFocusViewRenderer.setRunderView(focusView);

    }

    public void setBublePer(float bubbleWidthPer, float bubbleHeightPer) {
        mFocusViewRenderer.setBublePer(bubbleWidthPer,bubbleHeightPer);
    }
}
