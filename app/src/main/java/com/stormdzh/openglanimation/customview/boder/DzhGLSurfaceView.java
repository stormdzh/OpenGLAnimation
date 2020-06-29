package com.stormdzh.openglanimation.customview.boder;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.stormdzh.openglanimation.util.DeviceUtil;
import com.stormdzh.openglanimation.util.LogUtil;

/**
 * @Description: surfaceView
 * @Author: dzh
 * @CreateDate: 2020-06-23 17:05
 */
public class DzhGLSurfaceView extends GLSurfaceView {

    private DzhRenderer mDzhRenderer;

    private int bubbleWidth;//一边气泡的宽度
    private float bubbleWidthPer;//气泡占据顶点坐标的百分比
    private float bubbleHeightPer;//气泡占据顶点坐标的百分比

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
        bubbleWidth = DeviceUtil.dip2px(context, 15);
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

    /**
     * 释放
     */
    public void destroy() {

    }

    public void setBorderSize(View itemView, int x, int y, int width, int height, float scale) {

        //设置跟布局的尺寸
        ViewGroup.LayoutParams rootLayoutParams = getLayoutParams();
        rootLayoutParams.width = (int) (width  + bubbleWidth * 2);
        rootLayoutParams.height = (int) (height  + bubbleWidth * 2);
        setLayoutParams(rootLayoutParams);

        bubbleWidthPer = (float) bubbleWidth / (width  + bubbleWidth);
        bubbleHeightPer = (float) bubbleWidth / (height  + bubbleWidth);
        LogUtil.i("adu", "width:" + width + "  height:" + height + "  bubbleWidth:" + bubbleWidth);
        LogUtil.i("adu", "bubbleWidthPer:" + bubbleWidthPer + "  bubbleHeightPer:" + bubbleHeightPer);

        mDzhRenderer.setBublePer(bubbleWidthPer, bubbleHeightPer);

    }

    public void setBorderSize(int x, int y) {
        //设置控件的位置
        setX(x - bubbleWidth);
        setY(y - bubbleWidth);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
//        LogUtil.i("adu", "surfaceDestroyed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
//        LogUtil.i("adu", "surfaceCreated");
        onResume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
//        LogUtil.i("adu", "surfaceChanged");
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
