package com.socialtv.topic;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-1.
 * 回复评论页面
 */
@ContentView(R.layout.reply)
public class ReplyActivity extends DialogFragmentActivity {

    @InjectView(R.id.reply_nickname)
    private TextView textView;

    @InjectView(R.id.reply_edit)
    private EditText editText;

    @InjectExtra(value = IConstant.TOPIC_ID, optional = true)
    private String topicId;

    @InjectExtra(value = IConstant.USER_NAME, optional = true)
    private String nickname;

    @Inject
    private Activity activity;

    @Inject
    private TopicServices services;

    private CancelDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setTitle("评论");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView.setText("回复 " + nickname );

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
                sendReplyToServer();
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

    private void sendReplyToServer() {
        String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", topicId);
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity){

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createReplyRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
            }
        }.start("正在发表回复");
    }
}
