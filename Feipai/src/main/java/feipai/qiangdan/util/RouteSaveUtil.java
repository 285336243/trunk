package feipai.qiangdan.util;

import android.content.Context;

import feipai.qiangdan.core.CacheRepository;

/**
 * Created by 51wanh on 2015/1/25.
 */
public class RouteSaveUtil {
    public static CacheRepository caReContext(Context context) {
        return CacheRepository.getInstance().fromContext(context);
    }

    /**
     *
     * @param context 上下文
     * @param sid 用户登陆验证码
     */
    public static void saveFromAdd(Context context, String sid) {
        caReContext(context).putString(IConstant.USER_ROUTE, IConstant.USER_FROM_ADDRESS, sid);
    }

    /**
     *
     * @param context context 上下文
     * @return 用户登陆验证码
     */
    public static String getFromAdd(Context context) {

        return caReContext(context).getString(IConstant.USER_ROUTE, IConstant.USER_FROM_ADDRESS);
    }
    /**
     * 清除个人信息
     *
     * @param context
     */
    public static void removeFromAdd(Context context) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences(IConstant.USER_ROUTE, Context.MODE_PRIVATE).edit().remove(IConstant.USER_FROM_ADDRESS).commit();
    }
}
