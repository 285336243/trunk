package com.jiujie8.choice.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.publicentity.ExternalAccounts;
import com.jiujie8.choice.publicentity.Login;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.share.AuthorizeListener;
import com.jiujie8.choice.share.OAuthThirdParty;
import com.jiujie8.choice.share.OAuthToken;
import com.jiujie8.choice.share.ShareWeiXin;
import com.jiujie8.choice.share.sina.SinaOAuthThirdPartyFactory;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.LoginUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunjian on 13-12-27.
 */
public class ThirdPartyShareActivity extends DialogFragmentActivity {

    private static final String THIRD_PARTY_LOGIN = "identity/account/externalSignIn";

    protected int BIND_ACCOUNT = 3000;

    OAuthThirdParty sinaOAuthThirdParty;

    protected ShareWeiXin mWeiXin;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mWeiXin = new ShareWeiXin(activity);
        sinaOAuthThirdParty = SinaOAuthThirdPartyFactory.build(activity);
        sinaOAuthThirdParty.onCreate(bundle);
    }

    public void weiXinLogin() {
        mWeiXin.weiXinLogin();
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
        if (!isBind) {
            sinaOAuthThirdParty.shareText(text);
        } else {
            sinaOAuthThirdParty.authorize(new AuthorizeListener() {
                @Override
                public void onComplete(OAuthToken oAuthToken) {
                    sinaOAuthThirdParty.shareText(text);
                }
            });
        }

    }

    public void sinaSharePic(final ImageView imageView, final String text) {
        ToastUtils.show(activity, "操作已成功");
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (!isBind) {
            sinaOAuthThirdParty.sharePic(imageView, text);
        } else {
            sinaOAuthThirdParty.authorize(new AuthorizeListener() {
                @Override
                public void onComplete(OAuthToken oAuthToken) {
                    sinaOAuthThirdParty.sharePic(imageView, text);
                }
            });
        }

    }

    public void sinaSharePic(final Bitmap bitmap, final String text) {
        ToastUtils.show(activity, "操作已成功");
        boolean isBind = CacheRepository.getInstance().fromContext(activity).isBind(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN);
        if (!isBind) {
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

    /**
     * 这个是绑定时的认证
     */
    protected void sinaAuthorize(final DoAuthorize authorize) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
//                thirdBindingAccount(oAuthToken, "SINA", authorize);
            }
        });
    }

//    private void thirdBindingAccount(final OAuthToken token, final String type, final DoAuthorize authorize) {
//        new AbstractRoboAsyncTask<ExternalAccounts>(this){
//            /**
//             * Execute task with an authenticated account
//             *
//             * @param data
//             * @return result
//             * @throws Exception
//             */
//            @Override
//            protected ExternalAccounts run(Object data) throws Exception {
//                final GsonRequest<ExternalAccounts> request = new GsonRequest<ExternalAccounts>(Request.Method.POST, BINDING_ACCOUNT);
//                Map<String, String> bodys = new HashMap<String, String>();
//                bodys.put("type", type);
//                bodys.put("uid", token.getUid());
//                bodys.put("token", token.getToken());
//                bodys.put("expiredTime", token.getExpiredTime() + "");
//                request.setBodys(bodys);
//                request.setClazz(ExternalAccounts.class);
//                return (ExternalAccounts) HttpUtils.doRequest(request).result;
//            }
//
//            @Override
//            protected void onSuccess(ExternalAccounts externalAccounts) throws Exception {
//                super.onSuccess(externalAccounts);
//                if (externalAccounts != null) {
//                    if (externalAccounts.getResponseCode() == 0) {
//                        if (authorize != null) {
//                            authorize.authorize(externalAccounts);
//                        }
//                    } else {
//                        ToastUtils.show(activity, externalAccounts.getResponseMessage());
//                    }
//                }
//            }
//        }.execute();
//    }
    /**
     * 这个认证是登录时的认证
     * @param doShare
     */
    protected void doSinaAuthorize(final DoShare doShare) {
        sinaOAuthThirdParty.authorize(new AuthorizeListener() {
            @Override
            public void onComplete(OAuthToken oAuthToken) {
//                if (LoginUtil.isLogin(activity)) {
//                    if (doShare != null) {
//                        doShare.share();
//                    }
//                } else {
                    thirdLoignToServer("SINA", oAuthToken);
//                }
            }
        });
    }

    private Request getThirdPartyLoginRequst(String type, OAuthToken token) {
        final String uid = token.getUid();
        final String tokens = token.getToken();
        final String expiredTime = String.valueOf(token.getExpiredTime());
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("expireTime", expiredTime);
        bodys.put("token", tokens);
        bodys.put("type", type);
        bodys.put("uid", uid);
        final GsonRequest<Login> mRequest = HttpUtils.createPostRequest(Login.class, THIRD_PARTY_LOGIN, bodys);
        return mRequest;
    }

    protected void thirdLoignToServer(final String type, final OAuthToken token) {
        finish();
        new AbstractRoboAsyncTask<Login>(activity) {

            @Override
            protected Login run(Object data) throws Exception {
                return (Login) HttpUtils.doRequest(getThirdPartyLoginRequst(type, token)).result;
            }

            @Override
            protected void onSuccessCallback(Login login) {
                saveUserInfo(login);
            }
        }.execute();
    }

    private void saveUserInfo(Login mLogin) {
        LoginUtil.saveUserInfo(mLogin);
    }

    public interface DoShare {
        public void share();
    }

    public interface  DoAuthorize {
        public void authorize(ExternalAccounts accounts);
    }
}
