package com.stormdzh.openglanimation.customview.stickers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.stormdzh.openglanimation.util.LogUtil;

import java.util.UUID;

/**
 * @Description: 贴纸
 * @Author: dzh
 * @CreateDate: 2020-07-03 10:19
 */
public class StickersGLSurfaceView extends GLSurfaceView {
    private final String TAG="StickersRenderer";

    private StickersRenderer mStickersRenderer;

    public StickersGLSurfaceView(Context context) {
        this(context, null);
    }

    public StickersGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(3);
        mStickersRenderer=new StickersRenderer(context,this);
        setRenderer(mStickersRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        LogUtil.e(TAG, "egl version==>=" + checkGLVersion());

    }

    public void setRenderCallBack(final StickersRenderer.RenderCallBack renderCallBack) {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                mStickersRenderer.setRenderCallBack(renderCallBack);
            }
        });

    }

    private boolean checkGLVersion() {
        ActivityManager am = (ActivityManager) this.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        return ci.reqGlEsVersion >= 0x30000;
    }

    public String addSprite(final SpriteMsg msg) {
        final String temp = UUID.randomUUID().toString();
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                SpriteManager.getInstance().addSprite(msg, temp, getContext().getApplicationContext());
            }
        });
        return temp;
    }

    public void updateSprite(final String key, final SpriteMsg msg) {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                SpriteManager.getInstance().updateSprite(key, msg);
            }
        });
    }
}
