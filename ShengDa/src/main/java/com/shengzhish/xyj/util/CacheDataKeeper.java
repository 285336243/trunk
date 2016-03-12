package com.shengzhish.xyj.util;

import android.content.Context;

import com.shengzhish.xyj.core.CacheRepository;

/**
 * 缓存类
 *
 * @author lenovo
 */
public class CacheDataKeeper {

    public static CacheRepository caReContext(Context context) {
        return CacheRepository.getInstance().fromContext(context);
    }


    /**
     * 判断是否有活动数据
     *
     * @param context
     * @return
     */
    public static boolean isCatchActivityResponse(Context context) {
        final String json = getActivityResponse(context);
        if (json != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存活动数据
     *
     * @param context
     * @param
     */
    public static void saveActivityResponse(Context context, String json) {
        caReContext(context).putString(IConstant.DATAKEEPER, IConstant.ACTIVITY_RESPONSE, json);
    }

    /**
     * 获取活动数据
     *
     * @param context
     * @return
     */
    public static String getActivityResponse(Context context) {

        return caReContext(context).getString(IConstant.DATAKEEPER, IConstant.ACTIVITY_RESPONSE);
    }


    /**
     * 判断是否有动态数据
     *
     * @param context
     * @return
     */
    public static boolean isCatchDynamicResponse(Context context) {
        final String jsonData = getDynamicResponse(context);
        if (jsonData != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存动态数据
     *
     * @param context
     * @param
     */
    public static void saveDynamicResponse(Context context, String json) {
        caReContext(context).putString(IConstant.DATAKEEPER, IConstant.DYNAMIC_RESPONSE, json);
    }

    /**
     * 获取动态数据
     *
     * @param context
     * @return
     */
    public static String getDynamicResponse(Context context) {

        return caReContext(context).getString(IConstant.DATAKEEPER, IConstant.DYNAMIC_RESPONSE);
    }

    /**
     * 判断是否有个人数据
     *
     * @param context
     * @return
     */
    public static boolean isCatchPersonResponse(Context context) {
        final String jsonData = getPersonResponse(context);
        if (jsonData != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存个人数据
     *
     * @param context
     * @param
     */
    public static void savePersonResponse(Context context, String json) {
        caReContext(context).putString(IConstant.DATAKEEPER, IConstant.PERSON_RESPONSE, json);
    }

    /**
     * 获取个人数据
     *
     * @param context
     * @return
     */
    public static String getPersonResponse(Context context) {

        return caReContext(context).getString(IConstant.DATAKEEPER, IConstant.PERSON_RESPONSE);
    }

    /**
     * 清除动态信息
     *
     * @param context
     */
    public static void removeDynamicCacheData(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(IConstant.DATAKEEPER, Context.MODE_PRIVATE).edit().remove(IConstant.DYNAMIC_RESPONSE).commit();
    }
    /**
     * 清除活动信息
     *
     * @param context
     */
    public static void removeActivityCacheData(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(IConstant.DATAKEEPER, Context.MODE_PRIVATE).edit().remove(IConstant.ACTIVITY_RESPONSE).commit();
    }

    /**
     * 清除个人信息
     *
     * @param context
     */
    public static void removePersonCacheData(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(IConstant.DATAKEEPER, Context.MODE_PRIVATE).edit().remove(IConstant.PERSON_RESPONSE).commit();
    }

    /**
     * 清除缓存信息
     */
    public static void clear(Context context) {
        caReContext(context).clear(IConstant.DATAKEEPER);
    }


}
