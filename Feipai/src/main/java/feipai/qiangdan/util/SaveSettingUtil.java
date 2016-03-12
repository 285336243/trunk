package feipai.qiangdan.util;

import android.content.Context;


import feipai.qiangdan.core.CacheRepository;


/**
 * 保存用户设定数据
 */
public class SaveSettingUtil {
    public static CacheRepository caReContext(Context context) {
        return CacheRepository.getInstance().fromContext(context);
    }

    /**
     * 保存震动状态
     *
     * @param context      保存的activity对象
     * @param vibrateState 设定的震动状态，true为震动，false为不震动
     */
    public static void saveVibrateState(Context context, boolean vibrateState) {
        caReContext(context).putBoolean(IConstant.USER_SETTING, IConstant.SETTING_VIBRATE, vibrateState);
    }

    /**
     * 是否设为震动
     *
     * @param context 保存的activity对象
     * @return 是否设为震动，true为震动，false为不震动
     */
    public static boolean isVibrate(Context context) {
        boolean isSaveVibrate = caReContext(context).getBoolean(IConstant.USER_SETTING, IConstant.SETTING_VIBRATE);
        if (isSaveVibrate) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context    保存的activity对象
     * @param soundState 设定的声音状态，true为有声音提示，false为没有声音
     */
    public static void saveSoundState(Context context, boolean soundState) {
        caReContext(context).putBoolean(IConstant.USER_SETTING, IConstant.SETTING_SOUND, soundState);
    }

    /**
     * @param context 保存的activity对象
     * @return 是否设为有声音提示，true为有声音提示，false为没有声音提示
     */
    public static boolean isSound(Context context) {
        boolean isSaveSound = caReContext(context).getBoolean(IConstant.USER_SETTING, IConstant.SETTING_SOUND);
        if (isSaveSound) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param context 上下文
     * @param sid 用户登陆验证码
     */
    public static void saveUserSid(Context context, String sid) {
        caReContext(context).putString(IConstant.USER_INFO, IConstant.USER_SID, sid);
    }

    /**
     *
     * @param context context 上下文
     * @return 用户登陆验证码
     */
    public static String getUserSid(Context context) {

        return caReContext(context).getString(IConstant.USER_INFO, IConstant.USER_SID);
    }
    /**
     * 清除个人信息
     *
     * @param context
     */
    public static void removeLoginSid(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(IConstant.USER_INFO, Context.MODE_PRIVATE).edit().remove(IConstant.USER_SID).commit();
    }
    /**
     * 保存登录的状态
     *
     * @param context
     * @param loginState
     *//*
    public static void saveLoginState(Context context, long loginState) {
        caReContext(context).putLong(IConstant.USER_SETTING, IConstant.LOGIN_STATE, loginState);
    }

    *//**
     * 判断是否登陆
     *
     * @param context
     * @return
     *//*
    public static boolean isLogin(Context context) {
        final long isLogin = caReContext(context).getLong(IConstant.USER_SETTING, IConstant.LOGIN_STATE);
        if (0 == isLogin) {
            return true;
        } else {
            return false;
        }
    }

    *//**
     * 保存用户头像
     *
     * @param context
     * @param
     *//*
    public static void saveUserAvatar(Context context, String avatar) {
        caReContext(context).putString(IConstant.USER_SETTING, IConstant.USER_PHOTO, avatar);
    }

    *//**
     * 获取用户头像
     *
     * @param context
     * @return
     *//*
    public static String getUserAvatar(Context context) {

        return caReContext(context).getString(IConstant.USER_SETTING, IConstant.USER_PHOTO);
    }

    *//**
     * 保存用户昵称
     *
     * @param context
     * @param
     *//*
    public static void saveUserNickName(Context context, String nikename) {
        caReContext(context).putString(IConstant.USER_SETTING, IConstant.USER_NAME,
                nikename);
    }

    *//**
     * 获取用户昵称
     *
     * @param context
     * @return
     *//*
    public static String getUserNickName(Context context) {

        return caReContext(context).getString(IConstant.USER_SETTING, IConstant.USER_NAME);
    }

    *//**
     * 保存用户ID
     *
     * @param context
     * @param userId
     *//*
    public static void saveUserId(Context context, String userId) {
        caReContext(context).putString(IConstant.USER_SETTING, IConstant.USER_ID, userId);
    }


    *//**
     * 获取用户ID
     *
     * @param context
     * @return
     *//*
    public static String getUserId(Context context) {

        return caReContext(context).getString(IConstant.USER_SETTING, IConstant.USER_ID);
    }

    *//**
     * 保存用户score
     *
     * @param context
     * @param userId
     *//*
    public static void saveUserScore(Context context, String userId) {
        caReContext(context).putString(IConstant.USER_SETTING, IConstant.USER_SCORE, userId);
    }


    *//**
     * 获取用户score
     *
     * @param context
     * @return
     *//*
    public static String getUserScore(Context context) {

        return caReContext(context).getString(IConstant.USER_SCORE, IConstant.USER_ID);
    }

    public static void saveUserMobile(final Context context, final String mobile) {
        if (!TextUtils.isEmpty(mobile))
            caReContext(context).putString(IConstant.USER_SETTING, IConstant.USER_MOBILE, mobile.substring(0, 3) + "****" + mobile.substring(7));
    }

    public static String getUserMobile(final Context context) {
        return caReContext(context).getString(IConstant.USER_SETTING, IConstant.USER_MOBILE);
    }

    public static void clearMobile(final Context context) {
        caReContext(context).removeString(IConstant.USER_SETTING, IConstant.USER_MOBILE);
    }*/

    /**
     * 清除设定信息
     */
    public static void clearSetting(Context context) {
        caReContext(context).clear(IConstant.USER_SETTING);
    }

}
