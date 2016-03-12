package com.mzs.guaji.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mzs.guaji.util.ToastUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by wlanjie on 14-1-22.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String WEIXID_APP_ID = "wxccda523aedc48b8e";
    private IWXAPI mWXAPI;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        setContentView(view);
        mWXAPI = WXAPIFactory.createWXAPI(this, WEIXID_APP_ID, false);
        mWXAPI.handleIntent(getIntent(), this);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWXAPI.handleIntent(intent, this);
        ToastUtil.showToast(this, "onNewIntent");
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消发送";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                break;
            default:
                result = "发送返回";
                break;
        }

        ToastUtil.showToast(this, result);
    }
}
