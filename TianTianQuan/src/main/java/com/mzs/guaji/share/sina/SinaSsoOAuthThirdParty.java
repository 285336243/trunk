package com.mzs.guaji.share.sina;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mzs.guaji.share.AuthorizeListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaSsoOAuthThirdParty extends SinaWebOAuthThirdParty {

    private SsoHandler ssoHandler;

    public SinaSsoOAuthThirdParty(IWeiboShareAPI mWeiboShareAPI, Activity activity, String appKey, String appSecret, String redirectUrl) {
        super(mWeiboShareAPI, activity, appKey, appSecret, redirectUrl);
        ssoHandler = new SsoHandler(activity, weiboAuth);
    }

    @Override
    public void authorize(AuthorizeListener authorizeListener) {
        super.authorizeListener = authorizeListener;
        ssoHandler.authorize(new SsoAuthListener());
    }

    class SsoAuthListener extends WebAuthListener {

        @Override
        public void onComplete(Bundle values) {
            token = Oauth2AccessToken.parseAccessToken(values);
            if (token != null && token.isSessionValid()) {
                save(token);
                callback(token);
            } else {
                String code = values.getString("code");
                Toast.makeText(context, "新浪登录失败: code=" + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ssoHandler.authorizeCallBack(requestCode,resultCode,data);
    }
}
