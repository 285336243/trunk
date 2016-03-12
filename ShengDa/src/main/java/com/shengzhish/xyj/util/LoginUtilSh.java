package com.shengzhish.xyj.util;

import android.content.Context;

import com.shengzhish.xyj.core.CacheRepository;

/**
 * 用户信息工具类
 *
 * @author lenovo
 */
public class LoginUtilSh {

    public static CacheRepository caReContext(Context context) {
        return CacheRepository.getInstance().fromContext(context);
    }


    /**
     * 保存登录的状态
     *
     * @param context
     * @param loginState
     */
    public static void saveLoginState(Context context, long loginState) {
        caReContext(context).putLong(IConstant.USER_LOGIN, IConstant.LOGIN_STATE, loginState);
    }

    /**
     * 判断是否登陆
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        final long isLogin = caReContext(context).getLong(IConstant.USER_LOGIN, IConstant.LOGIN_STATE);
        if (0 == isLogin) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存用户头像
     *
     * @param context
     * @param
     */
    public static void saveUserAvatar(Context context, String avatar) {
        caReContext(context).putString(IConstant.USER_LOGIN, IConstant.USER_PHOTO, avatar);
    }

    /**
     * 获取用户头像
     *
     * @param context
     * @return
     */
    public static String getUserAvatar(Context context) {

        return caReContext(context).getString(IConstant.USER_LOGIN, IConstant.USER_PHOTO);
    }

    /**
     * 保存用户昵称
     *
     * @param context
     * @param
     */
    public static void saveUserNickName(Context context, String nikename) {
        caReContext(context).putString(IConstant.USER_LOGIN, IConstant.USER_NAME,
                nikename);
    }

    /**
     * 获取用户昵称
     *
     * @param context
     * @return
     */
    public static String getUserNickName(Context context) {

        return caReContext(context).getString(IConstant.USER_LOGIN, IConstant.USER_NAME);
    }

    /**
     * 保存用户ID
     *
     * @param context
     * @param userId
     */
    public static void saveUserId(Context context, String userId) {
        caReContext(context).putString(IConstant.USER_LOGIN, IConstant.USER_ID, userId);
    }


    /**
     * 获取用户ID
     *
     * @param context
     * @return
     */
    public static String getUserId(Context context) {

        return caReContext(context).getString(IConstant.USER_LOGIN, IConstant.USER_ID);
    }

    /**
     * 清除用户信息
     */
    public static void clear(Context context) {
        caReContext(context).clear(IConstant.USER_LOGIN);
    }


}
