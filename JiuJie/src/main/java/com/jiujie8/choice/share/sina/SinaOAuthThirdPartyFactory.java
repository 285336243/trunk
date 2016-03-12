package com.jiujie8.choice.share.sina;

import android.app.Activity;

import com.jiujie8.choice.share.OAuthThirdParty;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaOAuthThirdPartyFactory {

    private final static String APPKEY = "1469571715";
    private final static String SECRET = "af811f64e15c112761661cbfe6c03ecb";
    private final static String REDIRECTURL = "http://www.jiujie8.com";

    public static OAuthThirdParty build(Activity activity){
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, APPKEY);
        if(mWeiboShareAPI.isWeiboAppInstalled()){
            return new SinaSsoOAuthThirdParty(mWeiboShareAPI, activity,APPKEY,SECRET,REDIRECTURL);
        }else{
            return new SinaWebOAuthThirdParty(mWeiboShareAPI, activity, APPKEY, SECRET, REDIRECTURL);
        }
    }
}
