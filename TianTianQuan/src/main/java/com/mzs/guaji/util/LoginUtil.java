package com.mzs.guaji.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 用户信息工具类
 * @author lenovo
 *
 */
public class LoginUtil {

    public static final String CONFIG = "CONFIG";
    public static final String WINDOW_FIRST_OPEN = "WINDOW_FIRST_OPEN";

	public static final String LOGIN_STATE_NAME = "IS_LOGIN";
    public static final String QQID = "QQID";
    public static final String QQNICKNAME = "QQNICKNAME";
    public static final String SINAID = "SINAID";
    public static final String SINANICKNAME = "SINANICKNAME";
    public static final String ISLOGIN = "IS_LOGIN";
    public static final String SPLASHVIDEO = "SPLASH_VIDEO";
    public static final String SPLASHVIDEO_NAME = "SPLASHVIDEO_NAME";
	/**
	 * 保存登录的状态
	 * @param context
	 * @param loginState
	 */
	public static void saveLoginState(Context context, long loginState) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putLong("isLogin", loginState);
		mEditor.commit();
	}
	
	/**
	 * 保存绑定的手机号码
	 * @param context
	 * @param mobileNumber
	 */
	public static void saveMobileNumber(Context context, String mobileNumber) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putString("mobile", mobileNumber.substring(0, 3)+"****"+mobileNumber.substring(7));
		mEditor.commit();
	}
	
	/**
	 * 获取绑定的手机号码
	 * @param context
	 * @return
	 */
	public static String getMobileNumber(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		return mPreferences.getString("mobile", "");
	}
	
	/**
	 * 清除保存的手机号码
	 * @param context
	 */
	public static void clearMobileNumber(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putString("mobile", "");
		mEditor.commit();
	}
	
	/**
	 * 保存用户头像
	 * @param context
	 * @param
	 */
	public static void saveUserAvatar(Context context, String avatar) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putString("avatar", avatar);
		mEditor.commit();
	}
	
	/**
	 * 获取用户头像
	 * @param context
	 * @return
	 */
	public static String getUserAvatar(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		return mPreferences.getString("avatar", "");
	}
	
	/**
	 * 清除保存用户保存的头像
	 * @param context
	 */
	public static void clearUserAvatar(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putString("avatar", "");
		mEditor.commit();
	}
	
	/**
	 * 判断是否登陆
	 * @param context
	 * @return
	 */
	public static boolean isLogin(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		long isLoginCode = mPreferences.getLong("isLogin", -1);
		if(0 == isLoginCode) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 清除登录状态
	 * @param context
	 */
	public static void clearLoginState(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putLong("userId", -1);
        mEditor.putLong("isLogin", -1);
		mEditor.commit();
	}
	
	/**
	 * 保存用户ID
	 * @param context
	 * @param userId
	 */
	public static void saveUserId(Context context, long userId) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putLong("userId", userId);
		mEditor.commit();
	}
	
	/**
	 * 获取用户ID
	 * @param context
	 * @return
	 */
	public static long getUserId(Context context) {
		SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
		return mPreferences.getLong("userId", -1);
	}

    /**
     * 保存新浪绑定ID
     * @param context
     * @param sinaAccountId
     */
    public static void saveSinaAccountId(Context context, long sinaAccountId) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreferences.edit();
        mEditor.putLong("sinaAccountId", sinaAccountId);
        mEditor.commit();
    }

    /**
     * 获取新浪绑定ID
     * @param context
     * @return
     */
    public static long getSinaAccountId(Context context) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getLong("sinaAccountId", -1);
    }

    /**
     * 保存sina名称
     * @param context
     * @param sinaNickname
     */
    public static void saveSinaNickname(Context context, String sinaNickname) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreferences.edit();
        mEditor.putString("sinaNickname", sinaNickname);
        mEditor.commit();
    }

    /**
     * 获取sina名称
     * @param context
     * @return
     */
    public static String getSinaNickname(Context context) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString("sinaNickname", "");
    }

    /**
     * 保存腾讯名称
     * @param context
     * @param qqNickname
     */
    public static void saveQQNickname(Context context, String qqNickname) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreferences.edit();
        mEditor.putString("sinaNickname", qqNickname);
        mEditor.commit();
    }

    /**
     * 获取腾讯名称
     * @param context
     * @return
     */
    public static String getQQNickname(Context context) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString("qqNickname", "");
    }

    /**
     * 保存腾讯绑定ID
     * @param context
     * @param qqAccountId
     */
    public static void saveQQAccountId(Context context, long qqAccountId) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreferences.edit();
        mEditor.putLong("qqAccountId", qqAccountId);
        mEditor.commit();
    }

    /**
     * 获取腾讯绑定ID
     * @param context
     * @return
     */
    public static long getQQAccountId(Context context) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getLong("qqAccountId", -1);
    }

    public static void saveFirstLogin(Context context, long firstLogin) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        Editor mEditor = mPreferences.edit();
        mEditor.putLong("firstLogin", firstLogin);
        mEditor.commit();
    }

    public static long getFirstLogin(Context context) {
        SharedPreferences mPreferences = context.getSharedPreferences(LOGIN_STATE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getLong("firstLogin", 0);
    }
}
