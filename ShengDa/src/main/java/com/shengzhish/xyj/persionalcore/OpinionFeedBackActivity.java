package com.shengzhish.xyj.persionalcore;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-6-18.
 */
@ContentView(R.layout.opinion_feedback)
public class OpinionFeedBackActivity extends DialogFragmentActivity {

    private final static String URI = "system/feedback.json";

    @Inject
    private Activity activity;

    @InjectView(R.id.opinion_feedback_back)
    private View backView;

    @InjectView(R.id.opinion_feedback_send)
    private View sendView;

    @InjectView(R.id.opinion_feedback_content)
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                new ProgressDialogTask<Response>(activity){
                    /**
                     * Sub-classes may override but should always call super to ensure the
                     * progress dialog is dismissed
                     *
                     * @param response
                     */
                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                ToastUtils.show(activity, "发布成功");
                                finish();
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }

                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Response run(Object data) throws Exception {
                        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, URI);
                        request.setClazz(Response.class);
                        Map<String, String> bodys = new HashMap<String, String>();
                        bodys.put("message", content);
                        request.setHeaders(bodys);
                        return (Response) HttpUtils.doRequest(request).result;
                    }
                }.start("正在发布");
            }
        });
    }

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
