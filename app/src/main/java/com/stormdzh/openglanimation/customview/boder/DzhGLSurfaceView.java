package com.stormdzh.openglanimation.customview.boder;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Description: surfaceView
 * @Author: dzh
 * @CreateDate: 2020-06-23 17:05
 */
public class DzhGLSurfaceView extends GLSurfaceView {

    private DzhRenderer mDzhRenderer;

    public DzhGLSurfaceView(Context context) {
        this(context, null);
    }

    public DzhGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //透明的glSurfaceView
    public void setTranslucent() {
        // 设置背景透明，否则一般加载时间长的话会先黑一下，但是也有问题，就是在它之上无法再有View了，因为它是top的，用的时候需要注意，必要的时候将其设置为false
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        setTranslucent();
        mDzhRenderer = new DzhRenderer(getContext());
        setRenderer(mDzhRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFocusView(View focusView) {
        mDzhRenderer.setFocusView(focusView);
    }
}
