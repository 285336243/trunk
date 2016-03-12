package com.shengzhish.xyj.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.persionalcore.entity.ExternalSigninResponse;
import com.shengzhish.xyj.persionalcore.entity.User;
import com.shengzhish.xyj.share.AuthorizeListener;
import com.shengzhish.xyj.share.OAuthThirdParty;
import com.shengzhish.xyj.share.OAuthToken;
import com.shengzhish.xyj.share.sina.SinaOAuthThirdPartyFactory;
import com.shengzhish.xyj.share.tencent.TencentOAuthThirdParty;
import com.shengzhish.xyj.share.weixin.ShareWeiXin;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunjian on 13-12-27.
 */
public class ThirdPartyShareActivity extends DialogFragmentActivity {

    private final static String EXTERNALSIGNIN = "identity/externalsignin.json";

    @Inject
    private Activity activity;

    protected int BIND_ACCOUNT = 3000;

    OAuthThirdParty sinaOAuthThirdParty;
    private CacheRepository mRepository = CacheRepository.getInstance().fromContext(this);
    protected TencentOAuthThirdParty tencentOAuthThirdParty;
    Context context = ThirdPartyShareActivity.this;
    private ShareWeiXin mWeiXin;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        tencentOAuthThirdParty = new TencentOAuthThirdParty(activity);
        mWeiXin = new ShareWeiXin(context);
        sinaOAuthThirdParty = SinaOAuthThirdPartyFactory.build(activity);
        sinaOAuthThirdParty.onCreate(bundle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sinaOAuthThirdParty.onNewIntent(intent);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            sinaOAuthThirdParty.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void sinaSharePic(final Bitmap bitmap, final String text) {
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (isBind) {
            sinaOAuthThirdParty.sharePic(bitmap, text);
        } else {
            sinaOAuthThirdParty.authorize(new AuthorizeListener() {
                @Override
                public void onComplete(OAuthToken oAuthToken) {
                    sinaOAuthThirdParty.sharePic(bitmap, text);
                }
            });
        }
    }

    public void quit() {
        if (sinaOAuthThirdParty != null) {
            sinaOAuthThirdParty.quit();
        }
        if (tencentOAuthThirdParty != null) {
            tencentOAuthThirdParty.quit();
        }
    }

    public void shareWeiXinText(final String text) {
        mWeiXin.shareWeiXinText(text);
    }

    public void shareWeiXinPic(final Bitmap bitmap) {
        mWeiXin.shareWeiXinPic(bitmap);
    }

    /**
     * 分享网页链接
     * @param bitmap 传入缩略图
     * @param url 链接地址
     * @param title 标题
     * @param description 描述
     */
    public void shareWeiXinWebPge(Bitmap bitmap, String url, String title,String description) {
        mWeiXin.shareWeiXinWebPge(bitmap, url,title, description);
    }

    public void shareWeiXinPic(final ImageView mImageView, final String text) {
        mWeiXin.shareWeiXinPic(mImageView, text);
    }
//
//    protected View.OnClickListener sinaLoginOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            doSinaAuthorize(null);
//        }
//    };
//
//    protected View.OnClickListener tencentLoginClickListener = new View.OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            doTencentAuthorize(null);
//        }
//    };

    protected void doQQAuthorize(final DoShare doShare) {
        tencentOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
//                sendBroadcast(new Intent(IConstant.WEIBO_SUCCESEE));
                postToServer("QQ", oAuthToken.getUid(), oAuthToken.getToken(), String.valueOf(oAuthToken.getExpiredTime()));
                if (doShare != null) {
                    doShare.share();
                }
            }
        });
    }

    protected void doSinaAuthorize(final DoShare doShare) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {

                postToServer("SINA", oAuthToken.getUid(), oAuthToken.getToken(), String.valueOf(oAuthToken.getExpiredTime()));
                if (doShare != null) {
                    doShare.share();
                }
            }
        });
    }

    public void qqSharePic(final File file, final String text) {
        if (tencentOAuthThirdParty.ready()) {
            tencentOAuthThirdParty.sharePic(file, text);
        } else {
            tencentOAuthThirdParty.authorize(new AuthorizeListener() {
                @Override
                public void onComplete(OAuthToken oAuthToken) {
                    tencentOAuthThirdParty.sharePic(file, text);
                }
            });
        }
    }

    public interface DoShare {
        public void share();
    }

    protected void postToServer(final String type, final String openId, final String accessToken, final String expiredTime) {
        new AbstractRoboAsyncTask<ExternalSigninResponse>(activity) {
            @Override
            protected void onSuccess(ExternalSigninResponse externalSigninResponse) throws Exception {
                super.onSuccess(externalSigninResponse);
                if (externalSigninResponse != null) {
                    if (externalSigninResponse.getResponseCode() == 0) {
                        ToastUtils.show(activity, "同步成功...");
//                        finish();
                        User user = externalSigninResponse.getUser();
                        LoginUtilSh.saveLoginState(activity, externalSigninResponse.getResponseCode());
                        LoginUtilSh.saveUserNickName(activity, user.getNickname());
                        LoginUtilSh.saveUserId(activity, user.getUserId());
                        LoginUtilSh.saveUserAvatar(activity, user.getAvatar());
                        Intent eIntent = new Intent();
                        eIntent.setAction(IConstant.PERSON_LOGIN);
                        sendBroadcast(eIntent);
//                        finish();
                    } else {
                        ToastUtils.show(activity, externalSigninResponse.getResponseMessage() + "kaka");

                    }
                } else {
                    ToastUtils.show(activity, "网络出错");
                }
            }

            @Override
            protected ExternalSigninResponse run(Object data) throws Exception {
                ToastUtils.show(activity, "正在与服务器同步账号...");
                finish();
                GsonRequest<ExternalSigninResponse> request = new GsonRequest<ExternalSigninResponse>(Request.Method.POST, EXTERNALSIGNIN);
                request.setClazz(ExternalSigninResponse.class);
                Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("type", type);
                bodys.put("uid", openId);
                bodys.put("token", accessToken);
                bodys.put("expiredTime", expiredTime);
                Log.v("person", "type = " + type + ", openId = " + openId + ", accessToken = " + accessToken + ", expiredTime = " + expiredTime);
                request.setHeaders(bodys);
                return (ExternalSigninResponse) HttpUtils.doRequest(request).result;
            }
        }.execute();

    }
}
