package com.socialtv.personcenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.http.MultipartRequest;
import com.socialtv.personcenter.entity.UserResponse;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.io.File;

import roboguice.inject.InjectView;

/**
 * 个人信息编辑
 */
public class PersonInforEdit extends SetUserHeadPhoto {
    private final static int CHANGE = 0;
    private static final String SUBMMIT_URI = "user/profile.json";

    @InjectView(R.id.person_photo)
    private ImageView personPhoto;

    @InjectView(R.id.person_nicheng)
    private EditText personNickName;

    @InjectView(R.id.person_genderss)
    private TextView personGenderss;

    @InjectView(R.id.person_signatusss)
    private EditText personSigna;

    @InjectView(R.id.person_gender_layout)
    private View personGendLayout;

    @InjectView(R.id.person_head_layout)
    private View personHeadLayout;

    @InjectView(R.id.change_fengmian)
    private View changeFengmian;

    private String userSex;
    private Dialog popDialog;
    private ModifyPersonDialog modifyPersonDialog;
    private boolean textChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_infor_edit_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("编辑");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        modifyPersonDialog = new ModifyPersonDialog(this, new ModifyPersonDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismissListener() {
                finish();
            }
        });

        popDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        popDialog.setCanceledOnTouchOutside(false);
        personGendLayout.setOnClickListener(listens);
        personHeadLayout.setOnClickListener(userPhotoClickListeners);
        changeFengmian.setOnClickListener(userPhotoClickListeners);
        mAvatarImage = personPhoto;
        Bundle bundle = getIntent().getExtras();
        String headPhoto = bundle.getString(IConstant.PERSON_HEAD);
        String gender = bundle.getString(IConstant.PERSON_GENDER);
        String name = bundle.getString(IConstant.PERSON_NIKCNAME);
        String siganation = bundle.getString(IConstant.PERSON_SIGNATION);
        if (!TextUtils.isEmpty(headPhoto)) {
            ImageLoader.getInstance().displayImage(headPhoto, personPhoto, ImageUtils.avatarImageLoader());
        }
        if (!TextUtils.isEmpty(name)) {
            personNickName.setText(name);
            CharSequence text = personNickName.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
        if (!TextUtils.isEmpty(gender)) {
            if (gender.equals("f")) {
                personGenderss.setText("女");
            } else {
                personGenderss.setText("男");
            }
        }
        if (!TextUtils.isEmpty(siganation)) {
            personSigna.setText(siganation);
            CharSequence text = personSigna.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }

        personNickName.addTextChangedListener(textWatcher);
        personGenderss.addTextChangedListener(textWatcher);
        personSigna.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, CHANGE, 0, "修改个人信息")
                .setIcon(R.drawable.btn_topsend_tomato)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;

    }

    private void subPersonData() {
        final String userNamed = personNickName.getText().toString().trim();
        final String userSexget = personGenderss.getText().toString().trim();
        final String signation = personSigna.getText().toString().trim();
        if (TextUtils.isEmpty(userNamed)) {
            ToastUtils.show(this, "昵称不能为空");
            return;
        }
        if (userSexget.equals("男")) {
            userSex = "m";
        } else if (userSexget.equals("女")) {
            userSex = "f";
        }
        new ProgressDialogTask<UserResponse>(this) {

            @Override
            protected UserResponse run(Object data) {
                MultipartRequest<UserResponse> request = new MultipartRequest<UserResponse>(SUBMMIT_URI);
                request.setClazz(UserResponse.class);
                request.addMultipartStringEntity("nickname", userNamed);
                if (!TextUtils.isEmpty(signation)) {
                    request.addMultipartStringEntity("signature", signation);
                }
                if (!TextUtils.isEmpty(userSex)) {
                    request.addMultipartStringEntity("gender", userSex);
                }
                if (backgroundFile != null) {
                    request.addMultipartFileEntity("bgImg", backgroundFile);
                }
                if (gravatarFile != null) {
                    request.addMultipartFileEntity("avatar", gravatarFile);
                }

                return (UserResponse) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(UserResponse update) throws Exception {
                super.onSuccess(update);
                if (update != null) {
                    if (update.getResponseCode() == 0) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(PersonInforEdit.this, update.getResponseMessage());
                    }
                }
            }
        }.start("正在提交修改信息");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (textChange || backgroundFile != null || gravatarFile != null) {
                    if (!modifyPersonDialog.isShowing())
                        modifyPersonDialog.show();
                } else {
                    finish();
                }
                break;
            case CHANGE:
                subPersonData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener listens = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.person_gender_layout:
                    choiceSelfGender();
                    break;
                case R.id.choice_boy:
                    personGenderss.setText("男");
                    cancelDialog();
                    break;
                case R.id.choice_girl:
                    personGenderss.setText("女");
                    cancelDialog();
                    break;
                case R.id.choice_cancel:
                    cancelDialog();
                    break;
                default:
                    break;
            }

        }
    };

    private void choiceSelfGender() {
        popDialog.setContentView(R.layout.user_choice_gender);
        View textBoy = popDialog.findViewById(R.id.choice_boy);
        View textGirl = popDialog.findViewById(R.id.choice_girl);
        View textCancel = popDialog.findViewById(R.id.choice_cancel);
        textBoy.setOnClickListener(listens);
        textGirl.setOnClickListener(listens);
        textCancel.setOnClickListener(listens);
        if (!popDialog.isShowing())
            popDialog.show();
    }

    private void cancelDialog() {
        if (popDialog.isShowing() || popDialog != null)
            popDialog.dismiss();
    }

    /**
     * 设置头像点击事件
     */
    View.OnClickListener userPhotoClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.person_head_layout:
                    confirmHead = true;
                    break;
                case R.id.change_fengmian:
                    confirmHead = false;
                    break;
            }
            setUserHeadPhoto();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteFile(saveFile);
        deleteFile(gravatarFile);
        deleteFile(backgroundFile);
    }

    private void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textChange = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
