package com.stormdzh.openglanimation.customview.gt;

import android.graphics.RectF;
import android.view.View;

public class FocusViewTools {
    public static float FocuseViewScale = 1.2f;
    public static int[] location;
    public static RectF showRectF = new RectF();
    public static float width,scaleMoveX,height,scaleMoveY;

    public static RectF getFocusRect(View focusView, float pixVerticesW, float pixVerticesH) {
        return getRect(focusView, FocuseViewScale, pixVerticesW, pixVerticesH);
    }


    //获取缩放后的view 位置
    public static RectF getRect(View focusView, float scale, float pixVerticesW, float pixVerticesH) {

        location = new int[2];

        focusView.getLocationOnScreen(location);
        showRectF.left = -1 + (location[0] * pixVerticesW);
        showRectF.right = showRectF.left + focusView.getMeasuredWidth() * pixVerticesW;
        showRectF.top = 1 - (location[1] * pixVerticesH);
        showRectF.bottom = showRectF.top - (focusView.getMeasuredHeight() * pixVerticesH);


        //焦点view 放大后的位置
        width = showRectF.right - showRectF.left;
        scaleMoveX = (width * scale - width);
        height = showRectF.top - showRectF.bottom;
        scaleMoveY = (height * scale - height);
        showRectF.left = showRectF.left - scaleMoveX / 2;
        showRectF.right = showRectF.right + scaleMoveX / 2;
        showRectF.top = showRectF.top - scaleMoveY / 2;
        showRectF.bottom = showRectF.bottom - scaleMoveY / 2;

        return showRectF;
    }
}
