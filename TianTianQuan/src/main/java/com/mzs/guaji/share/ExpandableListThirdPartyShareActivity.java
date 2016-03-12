package com.mzs.guaji.share;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mzs.guaji.R;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.BindingAccount;
import com.mzs.guaji.entity.ExternalSignin;
import com.mzs.guaji.share.sina.SinaOAuthThirdPartyFactory;
import com.mzs.guaji.share.tencent.TencentOAuthThirdParty;
import com.mzs.guaji.share.weixin.ShareWeiXin;
import com.mzs.guaji.ui.BindAccountActivity;
import com.mzs.guaji.util.AppManager;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.util.Log;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-8.
 */
public class ExpandableListThirdPartyShareActivity extends ExpandableListActivity implements Response.ErrorListener{
    protected int BIND_ACCOUNT = 3000;

    Context context = ExpandableListThirdPartyShareActivity.this;
    OAuthThirdParty sinaOAuthThirdParty;
    OAuthThirdParty tencentOAuthThirdParty;
    protected WeiboAPI mWeiboAPI;
    protected CacheRepository mRepository;
    public static final String DOMAIN = "http://social.api.ttq2014.com/";
    public long page = 1;
    public long count = 20;
    public GuaJiAPI mApi;
    protected boolean isFootShow = false;
    protected boolean isLastItemVisible = false;
    private ShareWeiXin mWeiXin;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mWeiXin = new ShareWeiXin(context);
        AppManager.getAppManager().addActivity(this);
        mApi = GuaJiAPI.newInstance(this);
        mRepository = CacheRepository.getInstance().fromContext(context);
        sinaOAuthThirdParty = SinaOAuthThirdPartyFactory.build(this, getResources().getString(R.string.sina_app_key), getResources().getString(R.string.sina_app_secret), getResources().getString(R.string.sina_redirect_uri));
        sinaOAuthThirdParty.onCreate(bundle);
        long appId = Long.valueOf(Util.getConfig().getProperty("APP_KEY"));
        String appSecket = Util.getConfig().getProperty("APP_KEY_SEC");
        tencentOAuthThirdParty = new TencentOAuthThirdParty(context, appId, appSecket);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sinaOAuthThirdParty.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(BIND_ACCOUNT == requestCode){
            if(resultCode == RESULT_OK){
                gotoPersonCenter(data.getLongExtra("userId", -1));
            }
        }else{
            sinaOAuthThirdParty.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void sinaShareText(final String text) {
        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if(isBind){
            sinaOAuthThirdParty.shareText(text);
        }else{
            doSinaAuthorize(new DoShare() {
                @Override
                public void share() {
                    sinaOAuthThirdParty.shareText(text);
                }
            });
        }

    }

    public void sinaSharePic(final ImageView imageView, final String text) {
        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if(isBind) {
            sinaOAuthThirdParty.sharePic(imageView, text);
        }else {
            doSinaAuthorize(new DoShare() {
                @Override
                public void share() {
                    sinaOAuthThirdParty.sharePic(imageView, text);
                }
            });
        }

    }

    public void sinaSharePic(final Bitmap bitmap, final String text) {
        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if(isBind) {
            sinaOAuthThirdParty.sharePic(bitmap, text);
        }else {
            doSinaAuthorize(new DoShare() {
                @Override
                public void share() {
                    sinaOAuthThirdParty.sharePic(bitmap, text);
                }
            });
        }
    }

    public void shareWeiXinText(final String text) {
        mWeiXin.shareWeiXinText(text);
    }

    public void shareWeiXinPic(final Bitmap bitmap, final String text) {
        mWeiXin.shareWeiXinPic(bitmap);
    }

    public void shareWeiXinPic(final ImageView mImageView, final String text) {
        mWeiXin.shareWeiXinPic(mImageView, text);
    }

    public void unRegisterWeiXin() {
        mWeiXin.unRegisterApp();
    }

    protected View.OnClickListener sinaLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doSinaAuthorize(null);
        }
    };

    protected View.OnClickListener tencentLoginClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            doTencentAuthorize(null);
        }
    };

    protected void doSinaAuthorize(final DoShare doShare) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
//            	ToastUtil.showToast(context, "正在同步资料");
                Log.d(this.getClass().getName(), "token=" + oAuthToken.getToken() + " uid=" + oAuthToken.getUid() + " expiredTime=" + oAuthToken.getExpiredTime());
                if(LoginUtil.isLogin(context)){
                    postSinaBindingAccount("SINA", oAuthToken.getUid(), oAuthToken.getToken(), String.valueOf(oAuthToken.getExpiredTime()));
                    if(doShare != null){
                        doShare.share();
                    }
                }else{
                    createGuaJiAccount(oAuthToken, "SINA");
                }
            }
        });
    }

    protected void bindAccountComplete(String type){
    }

    private void createGuaJiAccount(OAuthToken oAuthToken, String type) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("type", type);
        param.put("uid", oAuthToken.getUid());
        param.put("token", oAuthToken.getToken());
        param.put("expiredTime", String.valueOf(oAuthToken.getExpiredTime()));
        mApi.requestPostData(DOMAIN+"identity/externalsignin.json", ExternalSignin.class, param, new Response.Listener<ExternalSignin>() {
            @Override
            public void onResponse(ExternalSignin response) {
                if(response.getResponseCode() == 0){
                    if(response.getNeedAccount() == 1){
                        Intent intent = new Intent(context, BindAccountActivity.class);
                        intent.putExtra("avatar", response.getAvatar());
                        intent.putExtra("welcomeText", response.getWelcomeText());
                        intent.putExtra("nickname", response.getNickname());
                        intent.putExtra("externalAccountId", response.getExternalAccountId());
                        startActivityForResult(intent, BIND_ACCOUNT);
                    }else{
                        LoginUtil.saveLoginState(context, response.getResponseCode());
                        LoginUtil.saveUserId(context, response.getUserId());
                        gotoPersonCenter(response.getUserId());
                    }
                }else{
                    Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, this);
    }

    /**
     * 腾讯认证
     * @param doShare
     */
    protected void doTencentAuthorize(final DoShare doShare) {
        ToastUtil.showToast(context, "正在同步资料");
        tencentOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
                if(LoginUtil.isLogin(context)) {
                    postSinaBindingAccount("QQ", oAuthToken.getUid(), oAuthToken.getToken(), String.valueOf(oAuthToken.getExpiredTime()));
                    if(doShare != null) {
                        doShare.share();
                    }
                }else {
                    createGuaJiAccount(oAuthToken, "QQ");
                }
            }
        });
    }

    public void tencentShareText(final String text) {
        String accessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken)  ? true : false;
//        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.TENCENT, OAuthThirdParty.ACCESS_TOKEN);
        if(isBind) {
            tencentOAuthThirdParty.shareText(text);
        }else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.shareText(text);
                }
            });
        }

    }

    public void tencentSharePic(final ImageView imageView, final String text) {
        String accessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken)  ? true : false;
//        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.TENCENT, OAuthThirdParty.ACCESS_TOKEN);
        if(isBind) {
            tencentOAuthThirdParty.sharePic(imageView, text);
        }else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.sharePic(imageView, text);
                }
            });
        }
    }

    public void tencentSharePic(final Bitmap bitmap, final String text) {
        String accessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken)  ? true : false;
//        boolean isBind = CacheRepository.getInstance().fromContext(context).isBind(OAuthThirdParty.TENCENT, OAuthThirdParty.ACCESS_TOKEN);
        if(isBind) {
            tencentOAuthThirdParty.sharePic(bitmap, text);
        }else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.sharePic(bitmap, text);
                }
            });
        }
    }

    private void gotoPersonCenter(long userId){
        Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
        Intent mIntent = new Intent();
        mIntent.putExtra("userId", userId);
        mIntent.setAction(BroadcastActionUtil.LOGIN_ACTION);
        sendBroadcast(mIntent);
        finish();
    }


    private interface DoShare{
        public void share();
    }

    /**
     * 绑定微博
     * @param uid
     * @param token
     * @param expiredTime
     */
    private void postSinaBindingAccount(final String type, String uid, String token, String expiredTime) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("type", type);
        headers.put("uid", uid);
        if(TextUtils.isEmpty(token)) {
            return;
        }
        headers.put("token", token);
        headers.put("expiredTime", expiredTime);
        mApi.requestPostData(getBindingAccountRequest(), BindingAccount.class, headers, new Response.Listener<BindingAccount>() {
            @Override
            public void onResponse(BindingAccount response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if("SINA".equals(type)) {
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINAID, response.getExternalAccountId());
                            mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINANICKNAME, response.getExternalNickname());
                        }else if("QQ".equals(type)) {
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQID, response.getExternalAccountId());
                            mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQNICKNAME, response.getExternalNickname());
                        }
                        bindAccountComplete(type);
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, this);
    }

    private String getBindingAccountRequest() {
        return DOMAIN + "identity/add_external_account.json";
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
