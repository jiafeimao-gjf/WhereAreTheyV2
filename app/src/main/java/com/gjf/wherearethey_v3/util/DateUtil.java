package com.gjf.wherearethey_v3.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时期格式化显示类
 * Created by gjf on 2018/5/19.
 */
public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static String getNowDateTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyy年MM月dd日，HH时mm分ss秒");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowDateTime(Date date) {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyy年MM月dd日，HH时mm分ss秒");
        return s_format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowTimeDetail() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss.SSS");
        return s_format.format(new Date());
    }

}
