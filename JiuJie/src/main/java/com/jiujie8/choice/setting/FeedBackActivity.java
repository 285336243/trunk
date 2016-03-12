package com.jiujie8.choice.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.core.ProgressDialogTask;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.util.IConstant;


import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


/**
 * 用户反馈
 */
@ContentView(R.layout.feedback_layout)
public class FeedBackActivity extends DialogFragmentActivity {
    private static final String SUBMIT_SUGGESTION = "/identity/user/advice";
    @InjectView(R.id.comment_edit)
    private EditText editText;

    @Inject
    private Activity activity;

    private CancelDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setLogo(R.drawable.btn_back_choice);
        getSupportActionBar().setTitle("提点意见");
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
        menu.add(Menu.NONE, 0, 0, "意见")
                .setIcon(R.drawable.btn_sub_choice)
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isFinishActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isFinishActivity() {
        if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
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
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("advice", message);
        //---------------提交意见------------------------------------
        new ProgressDialogTask<Response>(activity) {

            @Override
            protected Response run(Object data) throws Exception {
                GsonRequest<Response> mRequest = HttpUtils.createPostRequest(Response.class, SUBMIT_SUGGESTION, bodys);
                return (Response) HttpUtils.doRequest(mRequest).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                ToastUtils.show(activity, "谢谢提出宝贵意见");
            }
        }.start("正在提交");
    }


}
