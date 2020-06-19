package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.customview.DGLSurfaceView;
import com.stormdzh.openglanimation.renderer.LineRenderer;
import com.stormdzh.openglanimation.renderer.PointRenderer;

/**
 * @Description: 点
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class PointActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        //设置使用OpenGL2.0版本
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new PointRenderer(this));
        //设置渲染模式：requestRender方法触发会渲染
        mGLSurfaceView.setRenderMode(DGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);
    }
}
