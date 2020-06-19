package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.customview.DGLSurfaceView;
import com.stormdzh.openglanimation.renderer.YuvRenderer;

import java.io.InputStream;

/**
 * @Description: yuv图片
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03  图片渲染有问题，应该是格式问题
 */
public class YuvPicActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private YuvRenderer mYuvRenderer;
    private int w = 500;
    private int h = 333;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        //设置使用OpenGL2.0版本
        mGLSurfaceView.setEGLContextClientVersion(2);
        mYuvRenderer = new YuvRenderer(this);
        mGLSurfaceView.setRenderer(mYuvRenderer);
        //设置渲染模式：requestRender方法触发会渲染
        mGLSurfaceView.setRenderMode(DGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);

        start();
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    InputStream is = getResources().getAssets().open("pic_yuv.yuv");

                    byte[] y = new byte[w * h];
                    byte[] u = new byte[w * h / 4];
                    byte[] v = new byte[w * h / 4];

                    int ready = is.read(y);
                    int readu = is.read(u);
                    int readv = is.read(v);
                    u = convert(u);
                    v = convert(v);
                    if (ready > 0 && readu > 0 && readv > 0) {
                        mYuvRenderer.setYUVData(w, h, y, u, v);
                        mGLSurfaceView.requestRender();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private byte[] convert(byte[] u) {
        //yuvj420p和yuv420p格式上是一致的，只是颜色空间上的不同。yuvj使用的是Jpeg转换公式，范围是0-255；yuv的范围是16-240

        for (int i = 0; i < u.length; i++) {
            byte b = u[i];
            int rb = 0;
            if (b > 16) {
                rb = b - 16;
            } else if (b < -16) {
                rb = b + 16;
            }
//            u[i] = (byte) (rb / 219f * 255);
            u[i] = 0;

        }
        return u;
    }
}
