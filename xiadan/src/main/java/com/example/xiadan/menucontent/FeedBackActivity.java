package com.example.xiadan.menucontent;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.xiadan.R;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.BaseActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by 51wanh on 2015/1/15.
 */
public class FeedBackActivity extends BaseActivity {
    private static final String FEEDBACK_URL = "page/api/v1/opinion";
    private static final int FEED_BACK_RESULT = 0x2a2;



    /**
     * 发送反馈
     */
    private View sendView;

    /**
     *  反馈内容
     */
    private EditText editText;
    private String returnString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feed_back);
        actionBar.setTitle("我要吐槽");
        activity=FeedBackActivity.this;
        editText=(EditText)findViewById(R.id.opinion_feedback_content);
        sendView=findViewById(R.id.opinion_feedback_send);

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.show(activity, "内容不能为空");
                    return;
                }
                ProgressDlgUtil.showProgressDlg(activity, "努力吐槽中......");
                final Map<String, String> map = new HashMap<String, String>();
                // [必填] 登陆验证码
                map.put("sid", SaveSettingUtil.getUserSid(activity));
                // [必填] 反馈内容
                map.put("content", content);
                HttpClientUtil.getInstance().doPostAsyn(FEEDBACK_URL, map, new HttpClientUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        returnString = result;
                        Message msg = Message.obtain();
                        msg.what = FEED_BACK_RESULT;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    Handler mHandler = new Handler() {
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
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    this.showCancelDialog();
                } else {
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final String content = editText.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                this.showCancelDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void showCancelDialog() {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
