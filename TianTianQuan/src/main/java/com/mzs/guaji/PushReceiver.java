package com.mzs.guaji;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;
import com.mzs.guaji.ui.MainActivity;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.util.Log;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = PushReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            startNotification(context, bundle);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}

    private void startNotification(Context context, Bundle mBundle) {
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
//        if(mBundle != null) {
//            String extra = mBundle.getString("cn.jpush.android.EXTRA");
//            if(!TextUtils.isEmpty(extra)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extra);
//                    if(null != extraJson && extraJson.length() > 0) {
//                        String value = extraJson.getString("page");
//                        if("group".equals(value)) {
//                            startCircle(context, extraJson, extraJson.getString("type"));
//                        }else if ("user".equals(value)) {
//                            Intent mIntent = new Intent(context, OthersInformationActivity.class);
//                            mIntent.putExtra("userId", extraJson.getLong("id"));
//                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(mIntent);
//                        }else if ("topic".equals(value)) {
//                            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
//                            mIntent.putExtra("topicId", extraJson.getLong("id"));
//                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(mIntent);
//                        }else if ("webview".equals(value)) {
//                            Intent mIntent = new Intent(context, WebViewActivity.class);
//                            mIntent.putExtra("url", extraJson.getString("url"));
//                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(mIntent);
//                        }else if ("entryform".equals(value)) {
//                            startEntryForm(context, extraJson, extraJson.getString("type"));
//                        }else if ("game".equals(value)) {
//
//                        }
//                    }
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private void startEntryForm(Context context, JSONObject extraJson, String type) {
        try {
            if("GSTX_ENTRY".equals(type)) {
                Intent applyIntent = new Intent(context, GSTXApplyActivity.class);
                applyIntent.putExtra("id", extraJson.getLong("id"));
                applyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(applyIntent);
            }else if("AQBWZ_ENTRY".equals(type)) {
                Intent mIntent = new Intent(context, AQBWApplyActivity.class);
                mIntent.putExtra("id", extraJson.getLong("id"));
//                mIntent.putExtra("clause", mEntryForm.getClause());
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mIntent);
            }else if("FNMS_ENTRY".equals(type)) {
                Intent mIntent = new Intent(context, FNMSApplyActivity.class);
                mIntent.putExtra("id", extraJson.getLong("id"));
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mIntent);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCircle(Context context, JSONObject extraJson, String type) {
        try {
            if("OFFICIAL".equals(type)) {
                Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
//            mIntent.putExtra("img", mEpg.getGroup().getImg());
                mIntent.putExtra("id", extraJson.getLong("id"));
//            mIntent.putExtra("name", mEpg.getGroup().getName());
                context.startActivity(mIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
