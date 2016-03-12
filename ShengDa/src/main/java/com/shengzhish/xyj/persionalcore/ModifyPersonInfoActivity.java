package com.shengzhish.xyj.persionalcore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.http.MultipartRequest;
import com.shengzhish.xyj.persionalcore.entity.Login;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;

import roboguice.inject.InjectView;

/**
 * 修改个人信息
 *
 *
 */
public class ModifyPersonInfoActivity extends SetUserHeadPhoto {

    private final static String REGIST_URI = "user/profile.json";

    @InjectView(R.id.back_imageview)
    private View backImageView;

    @InjectView(R.id.person_photo_layout)
    private View personPhotoLayout;

    @InjectView(R.id.person_sex_modify_layout)
    private View sexLayout;

    @InjectView(R.id.person_nikename_edittext)
    private EditText nikeName;

    @InjectView(R.id.modigy_complete)
    private View modigyComplete;

    @InjectView(R.id.person_photo)
    private ImageView userHeaderView;

    @InjectView(R.id.person_nikename_edittext)
    private EditText nameEditText;

    @InjectView(R.id.person_sex_textview)
    private TextView sexTextView;


    private String heahView;
    private String name;
    private String sex;
    private Dialog popDialog;
    private String userSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_infor_modify);
        popDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        popDialog.setCanceledOnTouchOutside(false);
        backImageView.setOnClickListener(onClickListeners);
        modigyComplete.setOnClickListener(onClickListeners);
        sexLayout.setOnClickListener(onClickListeners);
        personPhotoLayout.setOnClickListener(userPhotoClickListeners);
        mAvatarImage = userHeaderView;
        heahView = getIntent().getStringExtra(IConstant.USER_PHOTO);
        name = getIntent().getStringExtra(IConstant.USER_NAME);
        sex = getIntent().getStringExtra(IConstant.USER_SEX);
        if (!TextUtils.isEmpty(heahView)) {
            ImageLoader.getInstance().displayImage(heahView, userHeaderView, ImageUtils.imageLoader(this, 1000));
        }
        if (!TextUtils.isEmpty(name)) {
            nikeName.setText(name);
            CharSequence text = nikeName.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable)text;
                Selection.setSelection(spanText, text.length());}
        }
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("f")) {
                sexTextView.setText("女");
            } else {
                sexTextView.setText("男");
            }
        }
    }

    private void collectionData() {
        final String userNamed = nikeName.getText().toString().trim();
        final String userSexget = sexTextView.getText().toString().trim();
        if(TextUtils.isEmpty(userNamed)){
            ToastUtils.show(this, "昵称不能为空");
            return;
        }

        if (userSexget.equals("男")) {
            userSex = "m";
        } else if (userSexget.equals("女")) {
            userSex = "f";
        }

        new ProgressDialogTask<Login>(this) {

            @Override
            protected Login run(Object data) {
                MultipartRequest<Login> request = new MultipartRequest<Login>(REGIST_URI);
                request.setClazz(Login.class);
                request.addMultipartStringEntity("nickname", userNamed);
                if (!TextUtils.isEmpty(userSex)) {
                    request.addMultipartStringEntity("gender", userSex);
                }

                if (saveFile != null) {
                    request.addMultipartFileEntity("avatar", saveFile);
                }
                return (Login) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Login update) throws Exception {
                super.onSuccess(update);
                if (update != null) {
                    if (update.getResponseCode() == 0) {
                        ToastUtils.show(ModifyPersonInfoActivity.this, "修改成功");
                        sendLoginBroadcast();
                        finish();
                    } else {
                        ToastUtils.show(ModifyPersonInfoActivity.this, update.getResponseMessage());
                    }
                }
            }
        }.start("正在提交修改信息");
    }

    private View.OnClickListener onClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_imageview:
                    finish();
                    break;
                case R.id.modigy_complete:
                    collectionData();
                    //修改完成。。。。。。
                    break;
                case R.id.person_sex_modify_layout:
                    setUserHeadPhoto();
                    break;
                case R.id.choice_boy:
                    sexTextView.setText("男");
                    cancelDialog();
                    break;
                case R.id.choice_girl:
                    sexTextView.setText("女");
                    cancelDialog();
                    break;
                case R.id.choice_cancel:
                    cancelDialog();
                    break;

            }
        }
    };

    private void setUserHeadPhoto() {
        popDialog.setContentView(R.layout.user_choice_sex);
        View textBoy = popDialog.findViewById(R.id.choice_boy);
        View textGirl = popDialog.findViewById(R.id.choice_girl);
        View textCancel = popDialog.findViewById(R.id.choice_cancel);
        textBoy.setOnClickListener(onClickListeners);
        textGirl.setOnClickListener(onClickListeners);
        textCancel.setOnClickListener(onClickListeners);
        if (!popDialog.isShowing())
            popDialog.show();
    }

    private void cancelDialog() {
        if (popDialog.isShowing() || popDialog != null)
            popDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }
    private void sendLoginBroadcast() {
        Intent eIntent = new Intent();
        eIntent.setAction(IConstant.MODIFY_PERSON_INFORMATION);
        sendBroadcast(eIntent);

    }


}
