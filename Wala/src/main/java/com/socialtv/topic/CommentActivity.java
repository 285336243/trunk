package com.socialtv.topic;

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
 * 评论页面
 */
@ContentView(R.layout.comment)
public class CommentActivity extends DialogFragmentActivity {

    @InjectView(R.id.comment_edit)
    private EditText editText;

    @Inject
    private TopicServices services;

    @Inject
    private Activity activity;

    @InjectExtra(value = IConstant.TOPIC_ID, optional = true)
    private String topicId;

    private CancelDialog dialog;

    private boolean isPublish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setTitle("评论");
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
                if (isPublish) {
                    isPublish = false;
                    sendCommentToServer();
                }
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
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", topicId);
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity){

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createCommentRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                isPublish = true;
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
