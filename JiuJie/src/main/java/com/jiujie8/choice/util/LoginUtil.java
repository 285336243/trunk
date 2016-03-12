package com.jiujie8.choice.util;

import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.core.CacheRepository;
import com.jiujie8.choice.publicentity.Login;
import com.jiujie8.choice.publicentity.Token;
import com.jiujie8.choice.publicentity.User;

/**
 * Created by wlanjie on 14/12/4.
 */
public class LoginUtil {

    private final static CacheRepository createRepository() {
        return CacheRepository.getInstance().fromContext(ChoiceApplication.getContext());
    }

    /**
     * 判断是否登录
     *
     * @return true已登录
     */
    public final static boolean isLogin() {
        final long expiredTime = createRepository().getLong(IConstant.USER_LOGIN, IConstant.USER_EXPIRED_TIME);
        return expiredTime != -1 && System.currentTimeMillis() < expiredTime * 1000 ? true : false;
    }

    /**
     * 保存用户信息
     *
     * @param mLogin
     */
    public final static void saveUserInfo(final Login mLogin) {
        final User mUser = mLogin.getUser();
        if (mUser != null) {
            createRepository().putString(IConstant.USER_LOGIN, IConstant.USER_ID, mUser.getId());
            createRepository().putString(IConstant.USER_LOGIN, IConstant.USER_NICKNAME, mUser.getNickname());
            createRepository().putString(IConstant.USER_LOGIN, IConstant.USER_AVATAR, mUser.getAvatar());
            createRepository().putString(IConstant.USER_LOGIN, IConstant.USER_GENDER, mUser.getGender());
        }
        final Token mToken = mLogin.getToken();
        if (mToken != null) {
            createRepository().putLong(IConstant.USER_LOGIN, IConstant.USER_EXPIRED_TIME, mToken.getExpireTime());
            createRepository().putString(IConstant.USER_LOGIN, IConstant.USER_TOKEN, mToken.getValue());
        }
    }

    /**
     * 获取userId
     *
     * @return
     */
    public final static String getUserId() {
        return createRepository().getString(IConstant.USER_LOGIN, IConstant.USER_ID);
    }

    /**
     * 清除用户信息
     */
    public static void clear() {
        createRepository().clear(IConstant.USER_LOGIN);
    }
}
