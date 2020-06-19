package com.stormdzh.openglanimation.customview.gt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

import com.stormdzh.openglanimation.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint("NewApi")
public class BorderGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    //绑定的renderer
    private SceneRenderer mRenderer;

    //整个控件的大小
    private int SIZE_WIDTH;
    private int SIZE_HEIGHT;

    //呼吸背景
    private RenderBorderBg renderBorderBg;
    //气泡
    private RenderBorderPP renderBorderPP;
    //焦点view
    private RenderBorderView renderBorderView;
    //焦点控件
    private View focusView;


    //是否设置控件
    private volatile boolean isSetView = false;
    //是否绘制
    private volatile boolean isDrawView = false;
    private volatile boolean setDrawView = false;
    private long drawCont = 0;

    public BorderGLSurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        init(context);
    }


    public BorderGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.setEGLContextClientVersion(2);
        mContext = context;
        setTranslucent();
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    private class SceneRenderer implements Renderer {

        public void onDrawFrame(GL10 gl) {

            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
                    | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glViewport(0, 0, SIZE_WIDTH, SIZE_HEIGHT);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            checkFPS();

            drawFocusView();


        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            SIZE_WIDTH = width;
            SIZE_HEIGHT = height;


            renderBorderView.onInit(width, height);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.focus_border_bg);
            renderBorderBg.onInit(width, height, bitmap);
            bitmap.recycle();
            bitmap = null;

            Bitmap ppBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.focus_border_pp);
            renderBorderPP.onInit(mContext, width, height, ppBitmap);
            ppBitmap.recycle();
            ppBitmap = null;

        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_CULL_FACE);

            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            renderBorderView = new RenderBorderView();
            renderBorderBg = new RenderBorderBg();
            renderBorderPP = new RenderBorderPP();


        }

    }

    int fps = 0;
    long sTime = 0;

    private void checkFPS() {
        if (System.currentTimeMillis() - sTime < 1000) {
            fps++;
        } else {
            sTime = System.currentTimeMillis();
//            Logger.d("borderGL","fps:" + fps);
            fps = 0;
        }
    }


    //透明的glSurfaceView
    public void setTranslucent() {
        // 设置背景透明，否则一般加载时间长的话会先黑一下，但是也有问题，就是在它之上无法再有View了，因为它是top的，用的时候需要注意，必要的时候将其设置为false
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }


    public void setFocusView(View view) {
        focusView = view;
        drawCont = 0;
        if (view != null) {
            isSetView = true;
            isDrawView = true;
            setDrawView = true;
        } else {
            isSetView = false;
            isDrawView = false;
            setDrawView = false;
        }
    }

    //10 帧 刷新一次View 优化性能 比如第一次图片未加载出来 原view已渲染 10帧后刷新view 就显示出加载完图片的view 了
    private void drawFocusView() {
        //替换view
        if (isSetView) {
            isSetView = false;
            renderBorderBg.setRunderView(focusView);
            renderBorderView.setRunderView(focusView);
            renderBorderPP.setRunderView(focusView);
        }
        //更新View texture
        if (setDrawView && drawCont % 10 == 0) {
            renderBorderView.updateRunderViewTexture();
        }
        //渲染
        if (isDrawView) {
            drawCont++;
            renderBorderBg.onDraw();
            renderBorderView.onDraw();
            renderBorderPP.onDraw();
        }
    }


    public void release() {
        if (renderBorderView != null) {
            renderBorderView.onDestroy();
        }
    }


}
