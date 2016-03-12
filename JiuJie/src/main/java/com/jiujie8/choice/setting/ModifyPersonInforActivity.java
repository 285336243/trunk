package com.jiujie8.choice.setting;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.choice.CancelEditDialog;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.util.HttpHelp;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


/**
 * 修改个人信息
 */
@ContentView(R.layout.layout_modify_personinfor)
public class ModifyPersonInforActivity extends SetUserHeadPhoto {

    private static final String MODIFY_URL = "identity/user/update";
    @InjectView(R.id.modify_user_header)
    private ImageView user_header;
    @InjectView(R.id.modify_user_name)
    private EditText user_name;
    @InjectView(R.id.select_gender_togbtn)
    private ToggleButton selectGenderTogbtn;

    private String mGender;
    private String headAddress;
    private Bundle userBundle;
    private boolean textChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setLogo(R.drawable.btn_back_choice);
        getSupportActionBar().setTitle("修改个人资料");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        User user = (User) getIntent().getSerializableExtra(IConstant.USER);
        if (user != null)
            setUserData(user);
        mAvatarImage = user_header;
        user_header.setOnClickListener(userPhotoClickListeners);
        user_name.addTextChangedListener(textWatcher);
    }

    private void setGender() {
        selectGenderTogbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //选中    性别：女
                    mGender = "female";
                    //显示字体  左边
                    selectGenderTogbtn.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                } else {
                    //未选中   性别：男
                    mGender = "male";
                    //显示字体   右边
                    selectGenderTogbtn.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                }
            }
        });
    }


    private void setUserData(User user) {
        //用于保存性别数据，以备返回
        headAddress = user.getAvatar();
        mGender = user.getGender();
        //设置数据到控件中
        ImageLoader.getInstance().displayImage(headAddress, user_header, ImageUtils.avatarImageLoader());
        user_name.setText(user.getNickname());
        CharSequence text = user_name.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }

        //判断性别，如果为女，则将性别开关设为女，默认为男
        if ("female".equals(mGender)) {
            selectGenderTogbtn.setChecked(true);
            //在左边显示字体
            selectGenderTogbtn.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        setGender();
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
                if (textChange || saveFile != null) {
                    CancelEditDialog mDialog = new CancelEditDialog();
                    mDialog.show(getSupportFragmentManager(), "blur");
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            case 0:
                //提交数据
                submitDataToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitDataToServer() {
        final String userNamed = user_name.getText().toString().trim();
        if (TextUtils.isEmpty(userNamed)) {
            ToastUtils.show(this, "昵称不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", userNamed);
        //mGender不设置，为空值，则不上传
        if (mGender != null) {
            map.put("gender", mGender);
        }
        if (saveFile != null) {
            map.put("avatar", saveFile);
        }
        collectUserdata(userNamed, mGender, saveFile);
        HttpHelp.getInstance().postMutilHttp(this, Response.class, MODIFY_URL, "正在提交修改信息", map, new HttpHelp.OnCompleteListener<Response>() {
            @Override
            public void onComplete(Response response) {
                if (IConstant.STATE_OK.equals(response.getCode())) {
                    ToastUtils.show(activity, "修改成功");
                    Intent mIntent = new Intent();
                    mIntent.putExtras(userBundle);
                    setResult(RESULT_OK, mIntent);
                    finish();
                } else {
                    ToastUtils.show(activity, response.getMessage());
                }
            }
        });

    }

    private void collectUserdata(String userNamed, String mGender, File saveFile) {
        User user = new User();
        user.setNickname(userNamed);
        if (mGender != null) {
            user.setGender(mGender);
        }
        //如果设置了头像，保存头像文件路径，否则保存原url地址
        if (saveFile != null) {
            user.setAvatar(saveFile.getAbsolutePath());
        } else {
            user.setAvatar(headAddress);
        }
        userBundle = new Bundle();
        userBundle.putSerializable(IConstant.USER_MODIFY, user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 /*       if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }*/
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
