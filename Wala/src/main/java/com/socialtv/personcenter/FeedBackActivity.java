package com.socialtv.personcenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.topic.CancelDialog;
import com.socialtv.util.Httpbo;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


/**
 * 用户反馈
 */
@ContentView(R.layout.comment)
public class FeedBackActivity extends DialogFragmentActivity {
    @InjectView(R.id.comment_edit)
    private EditText editText;

    @Inject
    private Activity activity;

    private CancelDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setTitle("反馈");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new CancelDialog(this, new CancelDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismissListener() {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "评论")
                .setIcon(R.drawable.btn_topsend_tomato)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isFinishActivity();
                break;
            case 0:
                sendCommentToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            isFinishActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isFinishActivity() {
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            finish();
        }
    }

    private void sendCommentToServer() {
        String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            ToastUtils.show(activity, "内容为空哦 ！");
            return;
        }
        String url = "system/feedback.json";
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("message", message);
        Httpbo<Response> postAction = new Httpbo<Response>();
        postAction.postHttp(activity, Response.class, url, "正在提交", bodys);
        postAction.setListener(new Httpbo.OnCompleteListener<Response>() {
            @Override
            public void onComplete(Response response) {
                if (response.getResponseCode() == 0) {
                    ToastUtils.show(activity, "谢谢提出宝贵意见");
                    finish();
                } else {
                    ToastUtils.show(activity, response.getResponseMessage());
                }
            }
        });

    }


}
