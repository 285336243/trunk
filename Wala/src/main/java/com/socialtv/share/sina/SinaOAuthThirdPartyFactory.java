package com.socialtv.share.sina;

import android.app.Activity;
import android.util.Log;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.socialtv.share.OAuthThirdParty;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaOAuthThirdPartyFactory {

    private final static String APPKEY = "846593478";
    private final static String SECRET = "01246f582ffcf8750f4c8af4284cd5fd";
    private final static String REDIRECTURL = "http://tv.lookbang.com/wb_callback";

    public static OAuthThirdParty build(Activity activity){
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, APPKEY);
        if(mWeiboShareAPI.isWeiboAppInstalled()){
//            Log.v("person","Sso  OAuth " );
            return new SinaSsoOAuthThirdParty(mWeiboShareAPI, activity,APPKEY,SECRET,REDIRECTURL);
        }else{
//            Log.v("person","Web   OAut " );
            return new SinaWebOAuthThirdParty(mWeiboShareAPI, activity, APPKEY, SECRET, REDIRECTURL);
        }
    }
}
