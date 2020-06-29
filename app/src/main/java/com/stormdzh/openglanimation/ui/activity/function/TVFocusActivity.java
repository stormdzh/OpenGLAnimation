package com.stormdzh.openglanimation.ui.activity.function;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.customview.boder.DzhGLSurfaceView;
import com.stormdzh.openglanimation.customview.focus.FocusHLMgr;
import com.stormdzh.openglanimation.util.LogUtil;

/**
 * @Description: 测试边框
 * @Author: dzh
 * @CreateDate: 2020-06-16 17:03
 */
public class TVFocusActivity extends Activity implements View.OnFocusChangeListener {
    private String TAG = "boder";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tv_focus);
        initView();
        initBorder();

    }

    private void initView() {

        findViewById(R.id.tvLine101).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine102).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine103).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine104).setOnFocusChangeListener(this);

        findViewById(R.id.tvLine201).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine202).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine203).setOnFocusChangeListener(this);

        findViewById(R.id.tvLine301).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine302).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine303).setOnFocusChangeListener(this);

        findViewById(R.id.tvLine401).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine402).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine403).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine404).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine405).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine406).setOnFocusChangeListener(this);
        findViewById(R.id.tvLine407).setOnFocusChangeListener(this);
    }

    private FocusHLMgr mFocusHLMgr;
    private DzhGLSurfaceView borderView;
    private View posView;

    private void initBorder() {
        borderView = new DzhGLSurfaceView(this);
        posView = new View(this);
        mFocusHLMgr = new FocusHLMgr(posView, borderView);
        mFocusHLMgr.attach(this, posView, borderView);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        FocusHLMgr mgr = FocusHLMgr.getMgr(v.getContext());
        if (hasFocus) {
            if (mgr != null) {
                LogUtil.i("adu", "onFocusChange width:" + v.getWidth() + "  height:" + v.getHeight());
                mgr.viewGotFocus(v);
            }
        } else {
            if (mgr != null) {
                mgr.viewLostFocus(v);
            }
        }
    }
}