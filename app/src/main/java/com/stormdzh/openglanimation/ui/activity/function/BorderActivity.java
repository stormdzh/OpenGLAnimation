package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.customview.boder.DzhGLSurfaceView;
import com.stormdzh.openglanimation.util.DeviceUtil;

/**
 * @Description: 测试边框
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class BorderActivity extends Activity {
    private String TAG = "boder";

    private DzhGLSurfaceView mGLSurfaceView;
    private TextView tvTest;


    private Rect mTmpR = new Rect();
    private float scale = 1.1f;
    private float bubbleWidth=20; //dp 飘气泡的宽度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_border);
        mGLSurfaceView = findViewById(R.id.mGLSurfaceView);
        tvTest = findViewById(R.id.tvTest);

        tvTest.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGLSurfaceView.setVisibility(View.VISIBLE);
                int[] location = new int[2];
                tvTest.getLocationOnScreen(location);
                int curX = location[0];
                int curY = location[1];
                Log.i(TAG, "curX:" + curX + "  curY:" + curY);

                tvTest.getDrawingRect(mTmpR);
                Log.i(TAG, "w:" + mTmpR.width() + "  h:" + mTmpR.height());

                //设置mGLSurfaceView打大小
                setSize(mTmpR, scale);
                //设置位置
                setLocation(curX, curY-getStatusBarHeight(getBaseContext()), mTmpR, scale);

                mGLSurfaceView.setFocusView(tvTest);


            }
        }, 500);
    }

    private void setLocation(int curX, int curY, Rect mTmpR, float scale) {
        float dw = mTmpR.width() * (scale - 1);
        float dh = mTmpR.height() * (scale - 1);

        mGLSurfaceView.setX(curX - dw / 2-DeviceUtil.dip2px(this,bubbleWidth));
        mGLSurfaceView.setY(curY - dh / 2-DeviceUtil.dip2px(this,bubbleWidth));
    }

    private void setSize(Rect mTmpR, float scale) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mGLSurfaceView.getLayoutParams();
        layoutParams.width = (int) (mTmpR.width() * scale)+ DeviceUtil.dip2px(this,bubbleWidth)*2;
        layoutParams.height = (int) (mTmpR.height() * scale+DeviceUtil.dip2px(this,bubbleWidth)*2);
        mGLSurfaceView.setLayoutParams(layoutParams);
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
