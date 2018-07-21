package com.gjf.wherearethey_v2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 共享信息类
 * @author gjf
 * @version 1.1
 */
public class SharedUtil {

	private static SharedUtil mUtil;
	private static SharedPreferences mShared;
	
	public static SharedUtil getInstance(Context ctx,String filename) {
		if (mUtil == null) {
			mUtil = new SharedUtil();
		}
		mShared = ctx.getSharedPreferences(filename, Context.MODE_PRIVATE);
		return mUtil;
	}

	public void writeShared(String key, String value) {
		SharedPreferences.Editor editor = mShared.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public String readShared(String key, String defaultValue) {
		return mShared.getString(key, defaultValue);
	}
	
}
