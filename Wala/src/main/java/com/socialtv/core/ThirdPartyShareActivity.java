package com.socialtv.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.BindingMobileActivity;
import com.socialtv.personcenter.entity.ExternalAccounts;
import com.socialtv.personcenter.entity.Login;
import com.socialtv.share.AuthorizeListener;
import com.socialtv.share.OAuthThirdParty;
import com.socialtv.share.OAuthToken;
import com.socialtv.share.sina.SinaOAuthThirdPartyFactory;
import com.socialtv.share.tencent.TencentOAuthThirdParty;
import com.socialtv.share.weixin.ShareWeiXin;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.tencent.weibo.sdk.android.api.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunjian on 13-12-27.
 */
public class ThirdPartyShareActivity extends DialogFragmentActivity {

    private static final String POST_URL = "identity/externalsignin.json";
    private final static String BINDING_ACCOUNT = "identity/external_account.json";
    protected final static long appId = 801178482;
    private final static String appSecket = "95022a1e0f8eefa78fe30ac5d60271e9";
    private final static String redirectUrl = "http://51wala.com";

    @Inject
    private Activity activity;

    protected int BIND_ACCOUNT = 3000;

    OAuthThirdParty sinaOAuthThirdParty;

    protected TencentOAuthThirdParty tencentOAuthThirdParty;

    protected ShareWeiXin mWeiXin;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        tencentOAuthThirdParty = new TencentOAuthThirdParty(activity, appId, appSecket, redirectUrl);
        mWeiXin = new ShareWeiXin(activity);
        sinaOAuthThirdParty = SinaOAuthThirdPartyFactory.build(activity);
        sinaOAuthThirdParty.onCreate(bundle);
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
        if (BIND_ACCOUNT == requestCode) {
            if (resultCode == RESULT_OK) {
//        		gotoPersonCenter(data.getLongExtra("userId", -1));
            }
        } else {
            if (data != null) {
                sinaOAuthThirdParty.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void sinaShareText(final String text) {
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (isBind) {
            sinaOAuthThirdParty.shareText(text);
        } else {
            doSinaAuthorize(new DoShare() {
                @Override
                public void share() {
                    sinaOAuthThirdParty.shareText(text);
                }
            });
        }

    }

    public void sinaSharePic(final ImageView imageView, final String text) {
        ToastUtils.show(activity, "操作已成功");
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (isBind) {
            sinaOAuthThirdParty.sharePic(imageView, text);
        } else {
            doSinaAuthorize(new DoShare() {
                @Override
                public void share() {
                    sinaOAuthThirdParty.sharePic(imageView, text);
                }
            });
        }

    }

    public void sinaSharePic(final Bitmap bitmap, final String text) {
        ToastUtils.show(activity, "操作已成功");
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (isBind) {
            sinaOAuthThirdParty.sharePic(bitmap, text);
        } else {
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

    public void shareWeiXinCircleText(final String text) {
        mWeiXin.shareWeiXinCircleText(text);
    }

    public void shareWeiXinPic(final Bitmap bitmap) {
        mWeiXin.shareWeiXinPic(bitmap);
    }

    public void shareWeiXinPic(final ImageView mImageView, final String text) {
        mWeiXin.shareWeiXinPic(mImageView, text);
    }

    /**
     * 微信朋友圈
     * @param webPage
     * @param title
     * @param description
     * @param bitmap
     */
    public void shareWeiXinWebPage(final String webPage, final String title, final String description, final Bitmap bitmap) {
        mWeiXin.shareWeiXinWebPage(webPage, title, description, bitmap);
    }

    /**
     * 微信好友
      * @param webPage
     * @param title
     * @param description
     * @param bitmap
     */
    public void shareWeiXinFriendWebPage(final String webPage, final String title, final String description, final Bitmap bitmap) {
        mWeiXin.shareWeiXinFriendWebPage(webPage, title, description, bitmap);
    }

    public void tencentShareText(final String text) {
        String accessToken = Util.getSharePersistent(this, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken) ? true : false;
        if (isBind) {
            tencentOAuthThirdParty.shareText(text);
        } else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.shareText(text);
                }
            });
        }

    }

    public void tencentSharePic(final ImageView imageView, final String text) {
        ToastUtils.show(this, "操作已成功");
        String accessToken = Util.getSharePersistent(this, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken) ? true : false;
        if (isBind) {
            tencentOAuthThirdParty.sharePic(imageView, text);
        } else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.sharePic(imageView, text);
                }
            });
        }
    }

    public void tencentSharePic(final Bitmap bitmap, final String text) {
        ToastUtils.show(this, "操作已成功");
        String accessToken = Util.getSharePersistent(this, "ACCESS_TOKEN");
        boolean isBind = accessToken != null && !"".equals(accessToken) ? true : false;
        if (isBind) {
            tencentOAuthThirdParty.sharePic(bitmap, text);
        } else {
            doTencentAuthorize(new DoShare() {
                @Override
                public void share() {
                    tencentOAuthThirdParty.sharePic(bitmap, text);
                }
            });
        }
    }

    /**
     * 这个是绑定时的认证
     */
    protected void sinaAuthorize(final DoAuthorize authorize) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
                thirdBindingAccount(oAuthToken, "SINA", authorize);
            }
        });
    }

    /**
     * 这个是绑定时的认证
     */
    protected void tencentAuthorize(final DoAuthorize authorize) {
        tencentOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
                thirdBindingAccount(oAuthToken, "QQ", authorize);
            }
        });
    }

    private void thirdBindingAccount(final OAuthToken token, final String type, final DoAuthorize authorize) {
        new AbstractRoboAsyncTask<ExternalAccounts>(this){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected ExternalAccounts run(Object data) throws Exception {
                final GsonRequest<ExternalAccounts> request = new GsonRequest<ExternalAccounts>(Request.Method.POST, BINDING_ACCOUNT);
                Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("type", type);
                bodys.put("uid", token.getUid());
                bodys.put("token", token.getToken());
                bodys.put("expiredTime", token.getExpiredTime() + "");
                request.setHeaders(bodys);
                request.setClazz(ExternalAccounts.class);
                return (ExternalAccounts) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(ExternalAccounts externalAccounts) throws Exception {
                super.onSuccess(externalAccounts);
                if (externalAccounts != null) {
                    if (externalAccounts.getResponseCode() == 0) {
                        if (authorize != null) {
                            authorize.authorize(externalAccounts);
                        }
                    } else {
                        ToastUtils.show(activity, externalAccounts.getResponseMessage());
                    }
                }
            }
        }.execute();
    }


    /**
     * 这个认证是登录时的认证
     * @param doShare
     */
    protected void doTencentAuthorize(final DoShare doShare) {
        tencentOAuthThirdParty.authorize(new AuthorizeListener() {

            @Override
            public void onComplete(OAuthToken oAuthToken) {
                if (LoginUtil.isLogin(activity)) {
                    if (doShare != null) {
                        doShare.share();
                    }
                } else {
                    thirdLoignToServer("QQ", oAuthToken);
                }
            }
        });
    }

    /**
     * 这个认证是登录时的认证
     * @param doShare
     */
    protected void doSinaAuthorize(final DoShare doShare) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
                if (LoginUtil.isLogin(activity)) {
                    if (doShare != null) {
                        doShare.share();
                    }
                } else {
                    thirdLoignToServer("SINA", oAuthToken);
                }
            }
        });
    }

    private void thirdLoignToServer(final String type, final OAuthToken token) {
        finish();
        new AbstractRoboAsyncTask<Login>(activity) {

            @Override
            protected Login run(Object data) throws Exception {
                return (Login) HttpUtils.doRequest(getRequst(type, token)).result;
            }

            @Override
            protected void onSuccess(Login login) throws Exception {
                super.onSuccess(login);
                if (login != null) {
                    if (login.getResponseCode() == 0) {
                        if (login != null) {
                            saveUserInfo(login);
                        }
                        sendBroadcast(new Intent(IConstant.USER_LOGIN));
                        if ("BIND_MOBILE".equals(login.getRender())) {
                            startActivity(new Intents(activity, BindingMobileActivity.class).add(IConstant.IS_BACK, true).toIntent());
                        }
                    } else {
                        ToastUtils.show(activity, login.getResponseMessage());
                    }
                }
            }

            private Request getRequst(String type, OAuthToken token) {
                String uid = token.getUid();
                String tokens = token.getToken();
                String expiredTime = String.valueOf(token.getExpiredTime());
                GsonRequest<Login> request = new GsonRequest<Login>(Request.Method.POST, POST_URL);
                Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("type", type);
                bodys.put("uid", uid);
                bodys.put("token", tokens);
                bodys.put("expiredTime", expiredTime);
                request.setHeaders(bodys);
                request.setClazz(Login.class);
                return request;
            }
        }.execute();
    }

    private void saveUserInfo(Login login) {
        LoginUtil.saveUserId(this, login.getUser().getUserId());
        LoginUtil.saveLoginState(this, login.getResponseCode());
        LoginUtil.saveUserNickName(this, login.getUser().getNickname());
        LoginUtil.saveUserAvatar(this, login.getUser().getAvatar());
        LoginUtil.saveUserScore(this, login.getUser().getScore());
        if (login.getExternalAccount() != null && !login.getExternalAccount().isEmpty()) {
            for (ExternalAccounts accounts : login.getExternalAccount()) {
                if ("QQ".equals(accounts.getType())) {
                    Util.saveSharePersistent(this, "ACCESS_TOKEN", accounts.getToken());
                    Util.saveSharePersistent(this, "EXPIRES_IN", String.valueOf(System.currentTimeMillis() / 1000 * 30 * 60 * 60 * 24));
                    Util.saveSharePersistent(this, "REFRESH_TOKEN", "");
                    Util.saveSharePersistent(this, "OPEN_ID", accounts.getUid());
                    Util.saveSharePersistent(this, "NAME", accounts.getNickname());
                    Util.saveSharePersistent(this, "NICK", accounts.getNickname());
                    Util.saveSharePersistent(this, "AUTHORIZETIME",
                            String.valueOf(System.currentTimeMillis() / 1000l));
                    Util.saveSharePersistent(this, "CLIENT_ID",String.valueOf(appId));
                } else {
                    CacheRepository.getInstance().fromContext(this).putString(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN, accounts.getToken()).putLong(OAuthThirdParty.SINA, OAuthThirdParty.EXPIRED_TIME, System.currentTimeMillis() / 1000 * 30 * 60 * 60 * 24).putString(OAuthThirdParty.SINA, OAuthThirdParty.UID, accounts.getUid());
                }
            }
        }
    }

    public interface DoShare {
        public void share();
    }

    public interface  DoAuthorize {
        public void authorize(ExternalAccounts accounts);
    }
}
