package com.mzs.guaji.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.ToastUtil;

import java.util.Calendar;

/**
 * Created by wlanjie on 14-1-7.
 */
public class FNMSApplyActivity extends SetAvatarActivity {

    private EditText mNameText;
    private LinearLayout mSexLayout;
    private TextView mSexText;
    private LinearLayout mBirthDataLayout;
    private TextView mBirthDataText;
    private EditText mNowLocationText;
    private LinearLayout mNowSalaryLayout;
    private TextView mNowSalaryText;
    private LinearLayout mIsDisabilityLayout;
    private TextView mIsDisabilityText;
    private EditText mMobileNumberText;
    private EditText mEmailText;
    private EditText mLifeSpecialExperienceText;
    private EditText mJobSpecialExperienceText;
    private Button mSubmitButton;
    private String sex= "";
    private int mYear;
    private int mMonth;
    private int mDay;
    private String salary = "";
    private String isDisability = "";
    private long id;
    private String gender = "";
    private int disability;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fnms_apply_layout);
        id = getIntent().getLongExtra("id", -1);
        TextView mBackText = (TextView) findViewById(R.id.fnms_back);
        mBackText.setOnClickListener(mBackClickListener);
        mAvatarLayout = (RelativeLayout) findViewById(R.id.fnms_avatar_layout);
        mAvatarLayout.setOnClickListener(mAvatarClickListener);
        mAvatarImage = (ImageView) findViewById(R.id.fnms_avatar);
        mNameText = (EditText) findViewById(R.id.fnms_edit_name);
        mSexLayout = (LinearLayout) findViewById(R.id.fnms_sex_layout);
        mSexLayout.setOnClickListener(mSexClickListener);
        mSexText = (TextView) findViewById(R.id.fnms_sex);
        mBirthDataLayout = (LinearLayout) findViewById(R.id.fnms_birth_data_layout);
        mBirthDataLayout.setOnClickListener(mBirthDataClickListener);
        mBirthDataText = (TextView) findViewById(R.id.fnms_birth_data);
        mNowLocationText = (EditText) findViewById(R.id.fnms_now_location);
        mNowSalaryLayout = (LinearLayout) findViewById(R.id.fnms_now_salary_layout);
        mNowSalaryLayout.setOnClickListener(mSalaryClickListener);
        mNowSalaryText = (TextView) findViewById(R.id.fnms_now_salary_text);
        mIsDisabilityLayout = (LinearLayout) findViewById(R.id.fnms_is_disability_layout);
        mIsDisabilityLayout.setOnClickListener(mIsDisabilityClickListener);
        mIsDisabilityText = (TextView) findViewById(R.id.fnms_is_disability_text);
        mMobileNumberText = (EditText) findViewById(R.id.fnms_edit_mobile_number);
        mEmailText = (EditText) findViewById(R.id.fnms_edit_email);
        mLifeSpecialExperienceText = (EditText) findViewById(R.id.fnms_life_special_experience_edit);
        mJobSpecialExperienceText = (EditText) findViewById(R.id.fnms_job_special_experience_edit);
        mSubmitButton = (Button) findViewById(R.id.fnms_submit_consent_button);
        mSubmitButton.setOnClickListener(mSubmitClickListener);
    }

    /**
     * 设置图片点击事件
     */
    View.OnClickListener mAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSetAvatarDialog();
        }
    };

    /**
     * 性别点击事件
     */
    View.OnClickListener mSexClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            final String[] mSexs = new String[]{"男", "女"};
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(mSexs, 1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            sex = mSexs[whichButton];
                        }
                    })
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if("".equals(sex)) {
                                mSexText.setText(mSexs[1]);
                            }else {
                                mSexText.setText(sex);
                            }
                        }
                    })
                    .create();
            mBuilder.show();
        }
    };

    /**
     * 出生年月点击事件,点击弹出选择出生年月对话框
     */
    View.OnClickListener mBirthDataClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mPickerDialog = new DatePickerDialog(context, mDateSetListener, mYear, mMonth, mDay);
            mPickerDialog.setCanceledOnTouchOutside(false);
            mPickerDialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDataText();
        }
    };

    private void updateDataText() {
        mBirthDataText.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
    }

    /**
     * 目前薪资点击事件
     */
    View.OnClickListener mSalaryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String[] types = new String[]{"1万以下", "1万~3万", "3万~5万", "5万~8万", "8万~10万", "10万以上"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            salary = types[whichButton];
                        }
                    })
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if("".equals(salary)) {
                                mNowSalaryText.setText(types[0]);
                            }else {
                                mNowSalaryText.setText(salary);
                            }
                        }
                    }).create();
            mBuilder.show();
        }
    };

    /**
     * 是否残疾点击事件
     */
    View.OnClickListener mIsDisabilityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            final String[] misDisabilitys = new String[]{"是", "否"};
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(misDisabilitys, 1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isDisability = misDisabilitys[whichButton];
                        }
                    })
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if("".equals(isDisability)) {
                                mIsDisabilityText.setText(misDisabilitys[1]);
                            }else {
                                mIsDisabilityText.setText(isDisability);
                            }
                        }
                    })
                    .create();
            mBuilder.show();
        }
    };

    /**
     * 提交按钮点击事件
     */
    View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           postFNMSApplyInfo();
        }
    };

    private void postFNMSApplyInfo() {
        String name = mNameText.getText().toString();
        if(TextUtils.isEmpty(name)) {
            ToastUtil.showToast(context, "名字不能为空");
            return;
        }
        String sex = mSexText.getText().toString();
        if(TextUtils.isEmpty(sex)) {
            ToastUtil.showToast(context, "性别不能为空");
            return;
        }
        if("男".equals(sex)) {
            gender = "m";
        }else {
            gender = "f";
        }
        String birthday = mBirthDataText.getText().toString();
        if(TextUtils.isEmpty(sex)) {
            ToastUtil.showToast(context, "性别不能为空");
            return;
        }
        String location = mNowLocationText.getText().toString();
        if(TextUtils.isEmpty(sex)) {
            ToastUtil.showToast(context, "性别不能为空");
            return;
        }
        String salary = mNowSalaryText.getText().toString();
        if(TextUtils.isEmpty(salary)) {
            ToastUtil.showToast(context, "工资不能为空");
            return;
        }
        String mDisability = mIsDisabilityText.getText().toString();
        if("是".equals(mDisability)) {
            disability = 1;
        }else {
            disability = 0;
        }
        String mobile = mMobileNumberText.getText().toString();
        if(TextUtils.isEmpty(mobile)) {
            ToastUtil.showToast(context, "手机号码不能为空");
            return;
        }
        String email = mEmailText.getText().toString();
        if(TextUtils.isEmpty(email)) {
            ToastUtil.showToast(context, "邮箱不能为空");
            return;
        }

        String lifeExperience = mLifeSpecialExperienceText.getText().toString();
        if(TextUtils.isEmpty(lifeExperience)) {
            ToastUtil.showToast(context, "人生经历不能为空");
            return;
        }
        String jobExperience = mJobSpecialExperienceText.getText().toString();
        if(TextUtils.isEmpty(jobExperience)) {
            ToastUtil.showToast(context, "工作经历不能为空");
            return;
        }
        if(saveFile == null) {
            ToastUtil.showToast(context, "头像不能为空");
            return;
        }

        MultipartRequest<DefaultReponse> mRequest = mApi.requestMultipartPostData(getFNMSApplyRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        ToastUtil.showToast(context, "资料提交成功");
                        finish();
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, FNMSApplyActivity.this);
        mRequest.addMultipartStringEntity("id", id+"")
                .addMultipartStringEntity("name", name)
                .addMultipartStringEntity("gender", gender)
                .addMultipartStringEntity("birthday", birthday)
                .addMultipartStringEntity("location", location)
                .addMultipartStringEntity("income", salary)
                .addMultipartStringEntity("disability", disability+"")
                .addMultipartStringEntity("mobile", mobile)
                .addMultipartStringEntity("email", email)
                .addMultipartStringEntity("lifeExperience", lifeExperience)
                .addMultipartStringEntity("workExperience", jobExperience)
                .addMultipartFileEntity("avatar", saveFile);
        mApi.addRequest(mRequest);
    }

    private String getFNMSApplyRequest() {
        return DOMAIN + "group/entry_form.json";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }
}
