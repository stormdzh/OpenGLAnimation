package com.stormdzh.openglanimation.customview.focus;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.customview.boder.DzhGLSurfaceView;
import com.stormdzh.openglanimation.util.DeviceUtil;
import com.stormdzh.openglanimation.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * @description 焦点控制
 * @author: dzh
 * @CreateDate: 2020-05-21 11:43
 */
public class FocusHLMgr implements ViewTreeObserver.OnScrollChangedListener {
    private static final String TAG = "FocusHLMgr";
    private static WeakHashMap<Context, WeakReference<FocusHLMgr>> SMgrMap = new WeakHashMap<Context, WeakReference<FocusHLMgr>>();
    private View mFocusHLT;   //计算位置的控件
    private View mLastFocus;  //最后拿到焦点的控件
    private DzhGLSurfaceView mBoderLightView;
    private Rect mTmpR = new Rect();
    private Rect mTmpP = new Rect();
    private Handler mHandler;
    private int mCheckLoop;
    private static final int MaxCheckLoop = 3;
    private static final int Move2LastFocus = 1;
    private static final int MoveDelay = 20;
    private int mLastX, mLastY;
    private float mScale = 1.1f;
    private ValueAnimator mAnim = ValueAnimator.ofFloat(1.0f, mScale);
    private int mPl, mPt;
    private boolean mHintAllFocus; //不处理焦点
    private int ainmTime=150;
    Handler.Callback mCb = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            FocusHLMgr.this.move2LastFocus(false);
            return true;
        }
    };

    /**
     * 构建焦点对象
     *
     * @param aFocusHLT      位置控件
     * @param boderLightView 边框控件
     */
    public FocusHLMgr(View aFocusHLT, DzhGLSurfaceView boderLightView) {
        mFocusHLT = aFocusHLT;
        this.mBoderLightView = boderLightView;

        SMgrMap.put(aFocusHLT.getContext(), new WeakReference<FocusHLMgr>(this));

        mHandler = new Handler(Looper.myLooper(), mCb);
        mAnim.setDuration(150);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator anim) {
                float scaleF = ((Float) anim.getAnimatedValue()).floatValue();
                ViewGroup.LayoutParams lp = mFocusHLT.getLayoutParams();
                int nw = (int) (scaleF * mTmpR.width()) + mTmpP.left + mTmpP.right;
                int nh = (int) (scaleF * (mTmpR.height())) + mTmpP.top + mTmpP.bottom;
                if (lp.width != nw || lp.height != nh) {
                    lp.width = nw;
                    lp.height = nh;
                    mFocusHLT.requestLayout();
                }

                int curX = (int) (mTmpR.left - mTmpR.width() * (scaleF - 1.0f) / 2 + mPl - mTmpP.left);
                int curY = (int) (mTmpR.top - mTmpR.height() * (scaleF - 1.0f) / 2 + mPt - mTmpP.top);
                mFocusHLT.setX(curX);
                mFocusHLT.setY(curY);

                if (mHintAllFocus) return;
                if (mScale == scaleF) { //最后一次的时候添加气泡动画
                    if (mBoderLightView != null) {
//                        mBoderLightView.setVisibility(View.VISIBLE);
                        mBoderLightView.setFocusView(mLastFocus);
                        // TODO: 2020-06-28 修改位置
                        mBoderLightView.setBorderSize(mLastFocus, curX, curY, nw, nh, mScale);
                    }
                }
            }
        });
    }

    View scaleView;
    /**
     * 获取焦点
     *
     * @param view view
     */
    public void viewGotFocus(View view, View scaleView) {
        mHintAllFocus = false;
        mLastFocus = view;
        this.scaleView = scaleView;
        mScale = getScale(scaleView);
        scaleItemView(mScale,scaleView);
        mAnim.setFloatValues(1, mScale);
        move2LastFocus(true);
        postCheckMsg();
    }

    /**
     * 失去焦点
     *
     * @param view view
     */
    public void viewLostFocus(View view, View scaleView) {
        if (mLastFocus == view) {
            mBoderLightView.onPause();
            mBoderLightView.setVisibility(View.INVISIBLE);
            view.clearAnimation();
        }
        if (this.scaleView == scaleView) {
            scaleItemView(1.0f,scaleView);
        }
    }

    /**
     * 获取焦点
     *
     * @param view view
     */
    public void viewGotFocus(View view) {
        viewGotFocus(view,1.1f);
    }
    /**
     * 获取焦点
     *
     * @param view view
     */
    public void viewGotFocus(View view, float scale) {
        if(mBoderLightView!=null) {
            mBoderLightView.setVisibility(View.INVISIBLE);
        }
        mHintAllFocus = false;
        mLastFocus = view;
        this.mScale=scale;
        mScale = getScale(view);
        scaleItemView(mScale,mLastFocus);
        mAnim.setFloatValues(1, mScale);
        move2LastFocus(true);
        postCheckMsg();
    }

    /**
     * 缩放控件
     *
     * @param scale scale
     */
    private void scaleItemView(float scale, View view) {
        if (view == null) return;
        ViewCompat.animate(view)
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(ainmTime)
                .start();
    }

    /**
     * 失去焦点
     *
     * @param view view
     */
    public void viewLostFocus(View view) {
        if(mBoderLightView!=null) {
            mBoderLightView.setVisibility(View.INVISIBLE);
        }
        if (mLastFocus == view) {
            mBoderLightView.onMPause();
            mBoderLightView.setVisibility(View.INVISIBLE);
            scaleItemView(1.0f,mLastFocus);
            view.clearAnimation();
        }
    }

    /**
     * 移动到最后获取到焦点的view
     *
     * @param aFocusChanged aFocusChanged
     */
    private void move2LastFocus(boolean aFocusChanged) {
        if (!mLastFocus.hasFocus()) {
            return;
        }
        try {
            mLastFocus.getDrawingRect(mTmpR);
            ViewGroup pv = (ViewGroup) mFocusHLT.getParent();

            pv.offsetDescendantRectToMyCoords(mLastFocus, mTmpR);

            // .9图片才可以调用该方法
            NinePatchDrawable np = (NinePatchDrawable) mFocusHLT.getBackground();
            if(np!=null) {
                np.getPadding(mTmpP);
            }
            View v = mLastFocus.findViewById(R.id.di_img);
            if (v != null) {
                v.getDrawingRect(mTmpR);
                ((ViewGroup) mLastFocus).offsetDescendantRectToMyCoords(v, mTmpR);
                mPl = mTmpR.left;
                mPt = mTmpR.top;

                v.getDrawingRect(mTmpR);
                pv.offsetDescendantRectToMyCoords(v, mTmpR);
            }

            if (aFocusChanged) {
                mAnim.start();
            }

            float value = ((Float) mAnim.getAnimatedValue()).floatValue();
            int curX = (int) (mTmpR.left - mTmpR.width() * (value - 1.0f) / 2 + mPl - mTmpP.left);
            int curY = (int) (mTmpR.top - mTmpR.height() * (value - 1.0f) / 2 + mPt - mTmpP.top);
            mFocusHLT.setX(curX);
            mFocusHLT.setY(curY);

            //修复气泡层动画
            if (mBoderLightView != null) {
                // TODO: 2020-06-28 修改位置
//                mBoderLightView.setFocusView(mLastFocus);
                mBoderLightView.setBorderSize(curX, curY);
//                mBoderLightView.setX(curX);
//                mBoderLightView.setY(curY);
            }

            if (curX != mLastX || curY != mLastY) {
                postCheckMsg();
                mCheckLoop = 0;
            } else {
                if (++mCheckLoop < MaxCheckLoop) {
                    postCheckMsg();
                }else{
                    if(mBoderLightView!=null){
                        mBoderLightView.setFocusView(mLastFocus);
                        mBoderLightView.setVisibility(View.VISIBLE);
                    }
                }
            }
            mLastX = curX;
            mLastY = curY;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取实例
     *
     * @param context 上下文
     * @return FocusHLMgr
     */
    static public FocusHLMgr getMgr(Context context) {
        WeakReference<FocusHLMgr> ref = SMgrMap.get(context);
        if (ref != null) {
            return ref.get();
        } else {
            return null;
        }
    }

    /**
     * 发送检查消息
     */
    private void postCheckMsg() {
        mHandler.removeMessages(Move2LastFocus);
        mHandler.sendEmptyMessageDelayed(Move2LastFocus, MoveDelay);
    }

    //全局监听
    @Override
    public void onScrollChanged() {
        if (mLastFocus != null) {
            postCheckMsg();
            move2LastFocus(false);
        }
    }

    //获取放大倍数
    private float getScale(View aFV) {
        if(mLastFocus==null) return mScale;
        float scale;

        float width = aFV.getWidth();
        if (width * mScale > (width + DeviceUtil.dip2px( aFV.getContext(),20))) {
            scale = (width + (float) DeviceUtil.dip2px(aFV.getContext(),20)) / width;
        } else {
            scale = mScale;
        }
        LogUtil.i("adu","缩放比例："+scale);
        return scale;
    }

    /**
     * 添加边框层
     *
     * @param activity   activity
     * @param aFocusHLT  位置控件
     * @param borderView 边框控件
     */
    public void attach(Activity activity, View aFocusHLT, View borderView) {
        if (borderView == null) return;
        borderView.setVisibility(View.INVISIBLE);
        FrameLayout mAndroidContentLayout = activity.findViewById(android.R.id.content);
        if (mAndroidContentLayout != null) {
            mAndroidContentLayout.addView(aFocusHLT);
            mAndroidContentLayout.addView(borderView);
        }
    }

    /**
     * 移除
     *
     * @param aFocusHLT  aFocusHLT
     * @param borderView borderView
     */
    public void detach(View aFocusHLT, DzhGLSurfaceView borderView) {
        if (borderView != null) {
            if (borderView.getParent() != null) {
                ((ViewGroup) borderView.getParent()).removeView(borderView);
            }
            borderView.destroy();
        }

        if (aFocusHLT != null) {
            if (aFocusHLT.getParent() != null) {
                ((ViewGroup) aFocusHLT.getParent()).removeView(aFocusHLT);
            }
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 移除焦点控件
     *
     * @param aFocusHLT aFocusHLT
     */
    public void removeMgr(View aFocusHLT) {
        if (aFocusHLT == null) return;
        if (SMgrMap != null && SMgrMap.containsKey(aFocusHLT.getContext())) {
            SMgrMap.remove(aFocusHLT.getContext());
        }
    }

    /**
     * 无条件隐藏边框
     */
    public void hintAllFocus() {
        if (mHintAllFocus) return;
        mHintAllFocus = true;
        if (mBoderLightView == null)
            return;
        if (mLastFocus != null) {
            scaleItemView(1.0f,mLastFocus);
            mLastFocus.clearAnimation();
        }

        mBoderLightView.onPause();
        mBoderLightView.setVisibility(View.INVISIBLE);
    }

    /**
     * 获取边框控件
     *
     * @return mBoderLightView
     */
    public View getBoderView() {
        return mBoderLightView;
    }

    /**
     * pause
     */
    public void onPause() {
        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
        if (mHandler != null && mHandler.hasMessages(Move2LastFocus)) {
            mHandler.removeMessages(Move2LastFocus);
        }
        if (mFocusHLT != null) {
            mFocusHLT.getViewTreeObserver().removeOnScrollChangedListener(this);
        }

        if(mBoderLightView!=null){
            mBoderLightView.onPause();
        }
    }


    /**
     * resume
     */
    public void onResume() {
        if (mFocusHLT != null) {
            mFocusHLT.getViewTreeObserver().addOnScrollChangedListener(this);
        }
        mBoderLightView.onResume();
    }
}
