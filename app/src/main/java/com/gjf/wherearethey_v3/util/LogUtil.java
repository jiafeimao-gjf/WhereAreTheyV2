package com.gjf.wherearethey_v3.util;

import android.util.Log;

public class LogUtil {
    private static String TAG = "LocationShare-";

    public static void setTAG(String TAG) {
        LogUtil.TAG = TAG;
    }

    public static void i(String tag, String msg) {
        Log.i(TAG + tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG + tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        Log.w(TAG + tag, msg, t);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG + tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(TAG + tag, msg, t);
    }

    public static void d(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }

    public static void v(String tag, String msg) {
        Log.v(TAG + tag, msg);
    }
}
