package com.shengzhish.xyj.share.tencent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.shengzhish.xyj.core.CacheRepository;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.share.AuthorizeListener;
import com.shengzhish.xyj.share.OAuthThirdParty;
import com.shengzhish.xyj.share.OAuthToken;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.connect.auth.QQToken;
import com.tencent.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by wlanjie on 14-1-2.
 */
public class TencentOAuthThirdParty implements OAuthThirdParty{

    private final static String APP_ID = "1101823490";

    private final Tencent tencent;

    private final Activity activity;

    private Weibo weibo;

    public TencentOAuthThirdParty(Activity activity) {
        this.activity = activity;
        tencent = Tencent.createInstance(APP_ID, activity);
    }

    public void authorize(final AuthorizeListener listener) {
        if (!tencent.isSessionValid()) {
            tencent.login(activity, "all", new SimpleUiListener(){
                @Override
                public void doComplete(JSONObject jsonObject) {
                    super.doComplete(jsonObject);
                    Log.v("person", "===  登陆 ===  ");
                    try {
                        listener.onComplete(new OAuthToken(jsonObject.getString("access_token"), jsonObject.getString("openid"), jsonObject.getLong("expires_in") + System.currentTimeMillis() / 1000L));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    protected void save(Oauth2AccessToken token) {
        CacheRepository.getInstance().fromContext(activity).putString(OAuthThirdParty.QQ, OAuthThirdParty.TOKEN, token.getToken()).putLong(OAuthThirdParty.QQ, OAuthThirdParty.EXPIRED_TIME, token.getExpiresTime()).putString(OAuthThirdParty.QQ, OAuthThirdParty.UID, token.getUid());
    }

    public boolean ready(){
        if(tencent ==null || !tencent.isSessionValid()) {
            tencent.logout(activity);
            weibo = null;
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void shareText(String text) {

    }

    @Override
    public void sharePic(ImageView imageView, String text) {
        weibo.sendPicText(text,null,null);
    }

    @Override
    public void sharePic(Bitmap bitmap, String text) {

    }

    @Override
    public synchronized void sharePic(File file, String text) {
        if(weibo == null){
            weibo = new Weibo(activity,tencent.getQQToken());
        }
        IUiListener listener = new IUiListener() {
            @Override
            public void onComplete(Object o) {

                try {
                    JSONObject json =(JSONObject)o;
                    int ret = json.getInt("ret");
                    if (ret == 0) {
                        ToastUtils.show(activity, "发送成功");
                    } else {
                        ToastUtils.show(activity, "发送失败");
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        };
        weibo.sendPicText(text,file.getAbsolutePath(),listener);
    }

    @Override
    public void sharePage(String text, ImageView imageView, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {

    }

    @Override
    public void sharePage(String text, Bitmap bitmap, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {

    }

    @Override
    public void quit() {
        if(tencent!=null && tencent.isSessionValid()){
            tencent.logout(activity);
        }
    }


    private class SimpleUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            doComplete((JSONObject) o);
        }

        public void doComplete(JSONObject jsonObject) {

        }

        @Override
        public void onError(UiError uiError) {
            System.out.println("uiErro = " + uiError);
        }

        @Override
        public void onCancel() {
            System.out.println("onCancel");
        }
    }

    public interface CompleteCallBack {
        public void doComplete(JSONObject jsonObject);
    }
}
