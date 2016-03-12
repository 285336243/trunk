package com.mzs.guaji.share.sina;

import android.app.Activity;

import com.mzs.guaji.share.OAuthThirdParty;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaOAuthThirdPartyFactory {


    public static OAuthThirdParty build(Activity activity, String appKey, String appSecret, String redirectUrl){
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, appKey);
        if(mWeiboShareAPI.isWeiboAppInstalled()){
            return new SinaSsoOAuthThirdParty(mWeiboShareAPI, activity,appKey,appSecret,redirectUrl);
        }else{
            return new SinaWebOAuthThirdParty(mWeiboShareAPI, activity, appKey, appSecret, redirectUrl);
        }
    }
}
