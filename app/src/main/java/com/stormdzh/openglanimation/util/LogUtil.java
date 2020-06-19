package com.stormdzh.openglanimation.util;

import android.util.Log;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2020-06-16 18:36
 */
public class LogUtil {

    public static boolean openLog = true;

    public static void i(String tag, String msg) {
        if (!openLog) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!openLog) {
            return;
        }
        Log.e(tag, msg);
    }
    public static void d(String tag, String msg) {
        if (!openLog) {
            return;
        }
        Log.d(tag, msg);
    }
}
