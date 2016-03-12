package com.socialtv.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by wlanjie on 14-6-20.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String WEIXID_APP_ID = "wxd67983c458eeea7c";
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
        String result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                ToastUtils.show(this, result);
                sendResultRequest();
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

    private void sendResultRequest() {
        new AbstractRoboAsyncTask<Response>(getApplicationContext()) {
            @Override
            protected Response run(Object data) throws Exception {
                GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, URI);
                request.setClazz(Response.class);
                return (Response) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
            }
        }.execute();
    }
}
