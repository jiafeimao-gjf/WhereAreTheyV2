package com.gjf.wherearethey_v2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 共享信息类
 * @author gjf
 * @version 1.1
 */
public class SharedUtil {


	public static String STATIC_PRIVACY_SP = "share_privacy";
	public static String STATIC_PRIVACY_SP_KEY = "is_allow";

	private static SharedUtil mUtil;
	private static SharedPreferences mShared;

	private Context context = null;

	private SharedUtil(Context context) {
		this.context = context;

	}

	public static void initSP(Context ctx) {
		if (mUtil == null) {
			mUtil = new SharedUtil(ctx);
		}
	}

	public static SharedUtil getInstance(String filename) {
		mShared = mUtil.getSharedPreferences(filename);
		return mUtil;
	}

	private SharedPreferences getSharedPreferences(String filename) {
		return context.getSharedPreferences(filename, Context.MODE_PRIVATE);
	}

	public void writeStringShared(String key, String value) {
		SharedPreferences.Editor editor = mShared.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public void writeBooleanShared(String key, boolean value) {
		SharedPreferences.Editor editor = mShared.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public String readStringShared(String key, String defaultValue) {
		return mShared.getString(key, defaultValue);
	}

	public boolean readBooleanShared(String key, boolean defaultValue) {
		return mShared.getBoolean(key, defaultValue);
	}
	
}
