package com.socialtv.personcenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.InviteCode;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-8-13.
 */
@ContentView(R.layout.invite_code)
public class InviteCodeActivity extends DialogFragmentActivity {

    @InjectView(R.id.invite_code_view_flipper)
    private ViewFlipper flipper;

    @InjectView(R.id.invite_code_edit)
    private EditText editText;

    @InjectView(R.id.invite_code_hint)
    private TextView hintText;

    @InjectView(R.id.invite_code_skip)
    private View skipView;

    @InjectView(R.id.invite_code_submit)
    private View submitView;

    @InjectView(R.id.invite_code_user_avatar)
    private ImageView avatarImage;

    @InjectView(R.id.invite_code_user_self_avatar)
    private ImageView selfAvatarImage;

    @InjectView(R.id.invite_code_success_hint)
    private TextView successHint;

    @InjectView(R.id.invite_code_i_know)
    private View iKnowView;

    @Inject
    private Activity activity;

    @Inject
    private UserService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = getSharedPreferences(IConstant.MESSAGE, MODE_PRIVATE);
        final String hint = preferences.getString(IConstant.INVITE_SCORE, "");
        hintText.setText(hint);

        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(IConstant.GUIDE));
                finish();
            }
        });

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = editText.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.show(activity, "邀请码不能为空");
                    return;
                }
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                final Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("code", code);
                new ProgressDialogTask<InviteCode>(activity){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected InviteCode run(Object data) throws Exception {
                        return (InviteCode) HttpUtils.doRequest(service.createInviteCode(bodys)).result;
                    }

                    /**
                     * Sub-classes may override but should always call super to ensure the
                     * progress dialog is dismissed
                     *
                     * @param inviteCode
                     */
                    @Override
                    protected void onSuccess(InviteCode inviteCode) throws Exception {
                        super.onSuccess(inviteCode);
                        if (inviteCode != null) {
                            if (inviteCode.getResponseCode() == 0) {
                                successHint.setText(inviteCode.getNotice());
                                if (inviteCode.getUsers() != null && inviteCode.getUsers().size() > 1) {
                                    ImageLoader.getInstance().displayImage(inviteCode.getUsers().get(0).getAvatar(), avatarImage, ImageUtils.imageLoader(activity, 1000));
                                    ImageLoader.getInstance().displayImage(inviteCode.getUsers().get(1).getAvatar(), selfAvatarImage, ImageUtils.imageLoader(activity, 1000));
                                }
                                flipper.showNext();
                                sendBroadcast(new Intent(IConstant.GUIDE));
                                sendBroadcast(new Intent(IConstant.USER_LOGIN));
                            } else {
                                ToastUtils.show(activity, inviteCode.getResponseMessage());
                            }
                        }
                    }
                }.start("正在提交");
            }
        });

        iKnowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
