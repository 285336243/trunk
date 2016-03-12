package com.socialtv;

import android.content.Context;
import android.content.Intent;

import com.socialtv.home.HomeActivity;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by wlanjie on 14/11/11.
 */
public class MessageReceiver extends XGPushBaseReceiver {
    //注册
    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult xgPushRegisterResult) {

    }

    //反注册
    @Override
    public void onUnregisterResult(Context context, int errorCode) {

    }

    //设置标签
    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {

    }

    //删除标签
    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {

    }

    //消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

    }

    //通知被点击
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null)
            return;
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            //通知被点击
            clickNotification(context);
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            //通知被清除
        }
    }

    //通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult message) {

    }

    private void clickNotification(Context context) {
        Intent mIntent = new Intent(context, HomeActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }
}
