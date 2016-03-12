package feipai.qiangdan.other;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.MyInfoBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/1/15.
 */
public class FeedBackActivity extends DialogFragmentActivity {
    private static final String FEEDBACK_URL = "api/v1/account/submitopinion";
    private static final int FEED_BACK_RESULT = 0x142;
    @Inject
    private Activity activity;

    @InjectView(R.id.return_back)
    private View returnBack;

    @InjectView(R.id.opinion_feedback_send)
    private View sendView;

    @InjectView(R.id.opinion_feedback_content)
    private EditText editText;
    private String returnString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feed_back);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    showCancelDialog();
                } else {
                    finish();
                }
            }
        });
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.show(activity, "内容不能为空");
                    return;
                }
                ProgressDlgUtil.showProgressDlg(activity, "努力反馈中......");
                final HashMap<String, String> map = new HashMap<String, String>();
                map.put("sid", SaveSettingUtil.getUserSid(activity));
                map.put("content", content);
                volley_post(FEEDBACK_URL, map);
/*                HttpClientUtil.getInstance().doPostAsyn(FEEDBACK_URL, map, new HttpClientUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        returnString = result;
                        Message msg = Message.obtain();
                        msg.what = FEED_BACK_RESULT;
                        mHandler.sendMessage(msg);
                    }
                });*/
            }
        });
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        // status: 1 ， 操作结果(1:成功)
        ToastUtils.show(activity, "发送成功，谢谢您的反馈");
        finish();
    }

/*    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FEED_BACK_RESULT:
                    ProgressDlgUtil.stopProgressDlg();
                    if (returnString.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(activity, returnString);
                    } else {
//                        Gson gson = new Gson();
//                        MyInfoBean data = gson.fromJson(returnString, MyInfoBean.class);
                        // status: 1 ， 操作结果(1:成功)
                        ToastUtils.show(activity, "发送成功，谢谢您的反馈");
                        finish();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final String content = editText.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                showCancelDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.edit_continue_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }


}
