package com.shengzhish.xyj.share.sina;

import android.app.Activity;

import com.shengzhish.xyj.share.OAuthThirdParty;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaOAuthThirdPartyFactory {

    private final static String APPKEY = "3092422087";
    private final static String SECRET = "97ce442dc3758ff0b6e4548a8e11e35e";
    private final static String REDIRECTURL = "https://api.weibo.com/oauth2/default.html";

    public static OAuthThirdParty build(Activity activity){
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, APPKEY);
        if(mWeiboShareAPI.isWeiboAppInstalled()){
            return new SinaSsoOAuthThirdParty(mWeiboShareAPI, activity,APPKEY,SECRET,REDIRECTURL);
        }else{
            return new SinaWebOAuthThirdParty(mWeiboShareAPI, activity, APPKEY, SECRET, REDIRECTURL);
        }
    }
}
