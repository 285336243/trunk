package com.jiujie8.choice.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.ThirdPartyShareActivity;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.http.BaseRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.share.OAuthToken;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by wlanjie on 14-6-20.
 */
public class WXEntryActivity extends ThirdPartyShareActivity implements IWXAPIEventHandler {

    private static final String WEIXID_APP_ID = "wx26eaee4a1208e2ab";
    private static final String SECRET = "343413ebd23282cde6d72f4c5eae7805";
    private static final String REQUEST_ASSESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private static final String REQUEST_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    private IWXAPI mWXAPI;
    private final static String URI = "event/share.json?type=INVITE_FRIEND";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mWXAPI = WXAPIFactory.createWXAPI(this, WEIXID_APP_ID, false);
        mWXAPI.handleIntent(getIntent(), this);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWXAPI.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            SendAuth.Resp mResp = (SendAuth.Resp) baseResp;
            requestToken(mResp);
            return;
        }
        String result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                ToastUtils.show(this, result);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消发送";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                ToastUtils.show(this, result);
                break;
            default:
                result = "发送返回";
                break;
        }
    }

    private void requestToken(SendAuth.Resp mResp) {
        final String requestTokenUrl = String.format(REQUEST_ASSESS_TOKEN, WEIXID_APP_ID, SECRET, mResp.token);
        new AbstractRoboAsyncTask<WeiXinToken>(this) {

            @Override
            protected WeiXinToken run(Object data) throws Exception {
                BaseRequest<WeiXinToken> mRequest = new BaseRequest<WeiXinToken>(Request.Method.GET, requestTokenUrl){};
                mRequest.setClazz(WeiXinToken.class);
                return (WeiXinToken) HttpUtils.doRequest(mRequest).result;
            }

            @Override
            protected void onSuccess(WeiXinToken weiXinToken) throws Exception {
                if (weiXinToken != null) {
                    final String refreshTokenUrl = String.format(REQUEST_REFRESH_TOKEN, WEIXID_APP_ID, weiXinToken.getRefresh_token());
                    new AbstractRoboAsyncTask<WeiXinToken>(activity) {

                        @Override
                        protected WeiXinToken run(Object data) throws Exception {
                            BaseRequest<WeiXinToken> mRequest = new BaseRequest<WeiXinToken>(Request.Method.GET, refreshTokenUrl){};
                            mRequest.setClazz(WeiXinToken.class);
                            return (WeiXinToken) HttpUtils.doRequest(mRequest).result;
                        }

                        @Override
                        protected void onSuccess(WeiXinToken weiXinToken) throws Exception {
                            super.onSuccess(weiXinToken);
                            if (weiXinToken != null) {
                                System.out.println("refresh_token = " + weiXinToken.getRefresh_token());
                                System.out.println("token = " + weiXinToken.getAccess_token());
                                System.out.println("id = " + weiXinToken.getOpenid());
                                OAuthToken mToken = new OAuthToken(weiXinToken.getAccess_token(), weiXinToken.getOpenid(), weiXinToken.getExpires_in());
                                thirdLoignToServer("WECHAT", mToken);
                            }
                        }

                        @Override
                        protected void onSuccessCallback(WeiXinToken weiXinToken) {

                        }
                    }.execute();

                }
            }

            @Override
            protected void onSuccessCallback(WeiXinToken weiXinToken) {

            }
        }.execute();
    }
}
