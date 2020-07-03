package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.customview.stickers.AspectFrameLayout;
import com.stormdzh.openglanimation.customview.stickers.CameraUtil;
import com.stormdzh.openglanimation.customview.stickers.Sprite;
import com.stormdzh.openglanimation.customview.stickers.SpriteMsg;
import com.stormdzh.openglanimation.customview.stickers.StickersGLSurfaceView;
import com.stormdzh.openglanimation.customview.stickers.StickersRenderer;

/**
 * @Description: 贴纸
 * @Author: dzh
 * @CreateDate: 2020-07-03 10:17
 */
public class StickersActivity extends Activity implements View.OnClickListener {

    private StickersGLSurfaceView surfaceView;
    private AspectFrameLayout mFrameLayout;

    private String studentKey, simleKey;
    private int x = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers);
        surfaceView = findViewById(R.id.surfaceView);
        mFrameLayout = findViewById(R.id.mFrameLayout);
        findViewById(R.id.tvSmile).setOnClickListener(this);
        findViewById(R.id.tvStudent).setOnClickListener(this);
        initCamera();
        initSurfaceView();
    }

    private void initCamera() {
        CameraUtil.getInstance().getCamera();
        CameraUtil.getInstance().setContext(this.getApplicationContext());

        CameraUtil.getInstance().setICameraSizeCallBack(new CameraUtil.ICameraSize() {
            @Override
            public void onPreviewSize(final int width, final int height) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float videoAspect = (float) width / height;
                        mFrameLayout.setAspectRatio(videoAspect);
                    }
                });


            }
        });

        CameraUtil.getInstance().setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
    }

    private void initSurfaceView() {
        surfaceView.setRenderCallBack(new StickersRenderer.RenderCallBack() {
            @Override
            public void onCreate() {

            }

            @Override
            public void onChange() {

            }

            @Override
            public void onTextureLoad(SurfaceTexture surfaceTexture) {
                CameraUtil.getInstance().initCamera(surfaceView.getHolder(), surfaceTexture);
            }

            @Override
            public void onDraw() {
                if (!TextUtils.isEmpty(studentKey)) {
                    SpriteMsg msg = new SpriteMsg();
                    if (x > 1280) {
                        x = 0;
                    }
                    x += 2;
                    msg.location_x += x;
                    msg.location_y = 100;
                    surfaceView.updateSprite(studentKey, msg);
                }
            }

            @Override
            public void onDestory() {
                CameraUtil.getInstance().releaseCamera();
            }
        });
    }

    @Override
    public void finish() {
        CameraUtil.getInstance().releaseCamera();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSmile:
                addSmile();

                break;
            case R.id.tvStudent:
                addStudent();
                break;

        }
    }

    private void addStudent() {
        SpriteMsg msg;
        msg = new SpriteMsg();
        msg.type = Sprite.TYPE_STUDENT;
        msg.location_y = 100;
        studentKey = surfaceView.addSprite(msg);
    }


    private int simleX=200;
    private void addSmile() {

        SpriteMsg msg;
        msg = new SpriteMsg();
        msg.type = Sprite.TYPE_SMILE;
        msg.location_x=simleX;
        msg.location_y = 200;
        simleKey = surfaceView.addSprite(msg);
        simleX+=200;
    }
}
