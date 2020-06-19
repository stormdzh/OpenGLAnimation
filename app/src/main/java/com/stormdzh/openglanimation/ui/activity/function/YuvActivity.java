package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.customview.DGLSurfaceView;
import com.stormdzh.openglanimation.renderer.YuvRenderer;

import java.io.InputStream;

/**
 * @Description: yuv视频
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class YuvActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private YuvRenderer mYuvRenderer;
    private InputStream is;
    private int w = 640;
    private int h = 360;

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
                    is = getResources().getAssets().open("yuv_video.yuv");

                    byte[] y = new byte[w * h];
                    byte[] u = new byte[w * h / 4];
                    byte[] v = new byte[w * h / 4];

                    while (true) {
                        int ready = is.read(y);
                        int readu = is.read(u);
                        int readv = is.read(v);
                        if (ready > 0 && readu > 0 && readv > 0) {
                            mYuvRenderer.setYUVData(w, h, y, u, v);
                            mGLSurfaceView.requestRender();
                            Thread.sleep(50);
                        } else {
                            Log.d("dzh", "已经完成");
                            break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
