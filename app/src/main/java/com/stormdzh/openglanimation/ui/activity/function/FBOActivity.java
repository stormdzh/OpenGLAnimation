package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.customview.DGLSurfaceView;
import com.stormdzh.openglanimation.renderer.FBORenderer;
import com.stormdzh.openglanimation.renderer.VBORenderer;

/**
 * @Description: vbo
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class FBOActivity extends Activity implements View.OnClickListener {

    private GLSurfaceView mGLSurfaceView;
    private FBORenderer mFBORenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        //设置使用OpenGL2.0版本
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mFBORenderer=new FBORenderer(this));
        //设置渲染模式：requestRender方法触发会渲染
        mGLSurfaceView.setRenderMode(DGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);

        mGLSurfaceView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    }
}
