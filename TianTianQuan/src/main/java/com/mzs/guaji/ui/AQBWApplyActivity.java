package com.mzs.guaji.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.AQBWApplyCacheName;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.ToastUtil;

import java.io.File;

/**
 * Created by wlanjie on 14-1-6.
 * 爱情保卫战报名UI
 */
public class AQBWApplyActivity extends SetAvatarActivity {

    private Context context = AQBWApplyActivity.this;
    private LinearLayout mClauseLayout;
    private Button mConsentButton;
    private TextView mClauseContentText;
    private RelativeLayout mManInformationLayout;
    private RelativeLayout mWomanInformationLayout;
    private EditText mPrimaryContradictionEdit;
    private RelativeLayout mProgramAspiration;
    private TextView mProgramAspirationText;
    private Button mSubmitConsent;
    private String programAspiration = "";
    private long id;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aqbw_apply_layout);
        String clause = getIntent().getStringExtra("clause");
        id = getIntent().getLongExtra("id", -1);
        TextView mBackText = (TextView) findViewById(R.id.aqbw_back);
        mBackText.setOnClickListener(mBackClickListener);
        mClauseContentText = (TextView) findViewById(R.id.aqbw_clause_content);
        mClauseContentText.setText(clause);
        mClauseLayout = (LinearLayout) findViewById(R.id.aqbw_clause_layout);
        mConsentButton = (Button) findViewById(R.id.aqbw_consent_button);
        mConsentButton.setOnClickListener(mConsentClickListener);
        mManInformationLayout = (RelativeLayout) findViewById(R.id.aqbw_man_information);
        mManInformationLayout.setOnClickListener(mManInformationClickListener);
        mWomanInformationLayout = (RelativeLayout) findViewById(R.id.aqbw_woman_information);
        mWomanInformationLayout.setOnClickListener(mWomanInformationClickListener);
        mPrimaryContradictionEdit = (EditText) findViewById(R.id.aqbw_primary_contradiction);
        mProgramAspiration = (RelativeLayout) findViewById(R.id.aqbw_program_aspiration);
        mProgramAspiration.setOnClickListener(mProgramAspirationClickListener);
        mProgramAspirationText = (TextView) findViewById(R.id.aqbw_program_aspiration_text);
        mSubmitConsent = (Button) findViewById(R.id.aqbw_submit_consent_button);
        mSubmitConsent.setOnClickListener(mSubmitConsentClickListener);

    }

    /**
     * 条款同意按钮点击事件
     */
    View.OnClickListener mConsentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClauseLayout.setVisibility(View.GONE);
        }
    };

    /**
     * 男方资料点击事件
     */
    View.OnClickListener mManInformationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, AQBWPersonInfomationActivity.class);
            mIntent.putExtra("title", "男方资料");
            startActivity(mIntent);
        }
    };

    /**
     * 女主资料点击事件
     */
    View.OnClickListener mWomanInformationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, AQBWPersonInfomationActivity.class);
            mIntent.putExtra("title", "女方资料");
            startActivity(mIntent);
        }
    };

    /**
     * 上节目意愿
     */
    View.OnClickListener mProgramAspirationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String[] types = new String[]{"男女双方均愿意", "仅男方愿意", "仅女方愿意"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            programAspiration = types[whichButton];
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
                            if("".equals(programAspiration)) {
                                mProgramAspirationText.setText(types[0]);
                            }else {
                                mProgramAspirationText.setText(programAspiration);
                            }
                        }
                    }).create();
            mBuilder.show();
        }
    };

    /**
     * 提交按钮点击事件
     */
    View.OnClickListener mSubmitConsentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postData();
        }
    };

    private void postData() {
        String primaryContradiction = mPrimaryContradictionEdit.getText().toString();
        String programAspiration = mProgramAspirationText.getText().toString();
        if(TextUtils.isEmpty(primaryContradiction)) {
            ToastUtil.showToast(context, "主要矛盾不能为空");
            return;
        }
        if(TextUtils.isEmpty(programAspiration)) {
            ToastUtil.showToast(context, "上节目意愿不能为空");
            return;
        }
        String manName = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NAME);
        String manBirthData = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_BIRTHDATA);
        String manNativePlace = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NATIVEPLACE);
        String manLocation = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_LOCATION);
        String manHeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_HEIGHT);
        String manWeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_WEIGHT);
        String manEducationBackground = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_EDUCATION_BACKGROUND);
        String manProfession = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_PROFESSION);
        String manIncome = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_INCOME);
        String manMobileNumber = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_MOBILE_NUMBER);
        String manQQ = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_QQ);
        String manImagePath = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_IMAGE_PATH);

        String womanName = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NAME);
        String womanBirthData = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_BIRTHDATA);
        String womanNativePlace = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NATIVEPLACE);
        String womanLocation = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_LOCATION);
        String womanHeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_HEIGHT);
        String womanWeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_WEIGHT);
        String womanEducationBackground = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_EDUCATION_BACKGROUND);
        String womanProfession = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_PROFESSION);
        String womanIncome = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_INCOME);
        String womanMobileNumber = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_MOBILE_NUMBER);
        String womanQQ = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_QQ);
        String womanImagePath = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_IMAGE_PATH);
        if(TextUtils.isEmpty(manName)) {
            ToastUtil.showToast(context, "男方姓名不能为空");
            return;
        }
        if(TextUtils.isEmpty(manBirthData)) {
            ToastUtil.showToast(context, "男方出生年月不能为空");
            return;
        }

        if(TextUtils.isEmpty(manNativePlace)) {
            ToastUtil.showToast(context, "男方籍贯不能为空");
            return;
        }

        if(TextUtils.isEmpty(manLocation)) {
            ToastUtil.showToast(context, "男方所在地不能为空");
            return;
        }

        if(TextUtils.isEmpty(manHeight)) {
            ToastUtil.showToast(context, "男方身高不能为空");
            return;
        }

        if(TextUtils.isEmpty(manWeight)) {
            ToastUtil.showToast(context, "男方体重不能为空");
            return;
        }

        if(TextUtils.isEmpty(manProfession)) {
            ToastUtil.showToast(context, "男方职业不能为空");
            return;
        }

        if(TextUtils.isEmpty(manEducationBackground)) {
            ToastUtil.showToast(context, "男方学历不能为空");
            return;
        }

        if(TextUtils.isEmpty(manIncome)) {
            ToastUtil.showToast(context, "男方年收入不能为空");
            return;
        }

        if(TextUtils.isEmpty(manMobileNumber)) {
            ToastUtil.showToast(context, "男方手机号不能为空");
            return;
        }
        if(TextUtils.isEmpty(manQQ)) {
            ToastUtil.showToast(context, "男方QQ不能为空");
            return;
        }

        if(TextUtils.isEmpty(manImagePath)) {
            ToastUtil.showToast(context, "男方图片不能为空");
            return;
        }

        if(TextUtils.isEmpty(womanName)) {
            ToastUtil.showToast(context, "女方姓名不能为空");
            return;
        }

        if(TextUtils.isEmpty(womanBirthData)) {
            ToastUtil.showToast(context, "女方出生年月不能为空");
            return;
        }

        if(TextUtils.isEmpty(womanNativePlace)) {
            ToastUtil.showToast(context, "女方籍贯不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanLocation)) {
            ToastUtil.showToast(context, "女方所在地不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanHeight)) {
            ToastUtil.showToast(context, "女方身高不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanWeight)) {
            ToastUtil.showToast(context, "女方体重不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanEducationBackground)) {
            ToastUtil.showToast(context, "女方学历不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanProfession)) {
            ToastUtil.showToast(context, "女方职业不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanIncome)) {
            ToastUtil.showToast(context, "女方年收入不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanMobileNumber)) {
            ToastUtil.showToast(context, "女方手机号码不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanQQ)) {
            ToastUtil.showToast(context, "女方QQ不能为空");
            return;
        }
        if(TextUtils.isEmpty(womanImagePath)) {
            ToastUtil.showToast(context, "女方图片不能为空");
            return;
        }

        MultipartRequest<DefaultReponse> mRequest = mApi.requestMultipartPostData(getAQBWApplyRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
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
        }, this);
        mRequest.addMultipartStringEntity("id", id+"")
                .addMultipartStringEntity("maleName", manName)
                .addMultipartStringEntity("maleBirthday", manBirthData)
                .addMultipartStringEntity("maleNativePlace", manNativePlace)
                .addMultipartStringEntity("maleLocation", manLocation)
                .addMultipartStringEntity("maleHeight", manHeight)
                .addMultipartStringEntity("maleWeight", manWeight)
                .addMultipartStringEntity("maleEducation", manEducationBackground)
                .addMultipartStringEntity("maleCareer", manProfession)
                .addMultipartStringEntity("maleAnnualIncome", manIncome)
                .addMultipartStringEntity("maleMobile", manMobileNumber)
                .addMultipartStringEntity("maleQQ", manQQ)
                .addMultipartStringEntity("femaleName", womanName)
                .addMultipartStringEntity("femaleBirthday", womanBirthData)
                .addMultipartStringEntity("femaleNativePlace", womanNativePlace)
                .addMultipartStringEntity("femaleLocation", womanLocation)
                .addMultipartStringEntity("femaleHeight", womanHeight)
                .addMultipartStringEntity("femaleWeight", womanWeight)
                .addMultipartStringEntity("femaleEducation", womanEducationBackground)
                .addMultipartStringEntity("femaleCareer", womanProfession)
                .addMultipartStringEntity("femaleAnnualIncome", womanIncome)
                .addMultipartStringEntity("femaleMobile", womanMobileNumber)
                .addMultipartStringEntity("femaleQQ", womanQQ)
                .addMultipartStringEntity("conflict", primaryContradiction)
                .addMultipartStringEntity("show", programAspiration)
                .addMultipartFileEntity("maleAvatar", new File(manImagePath))
                .addMultipartFileEntity("femaleAvatar", new File(womanImagePath));
        mApi.addRequest(mRequest);
    }

    private String getAQBWApplyRequest() {
        return DOMAIN + "group/entry_form.json";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GuaJi";
        String[] fileName = new String[]{"photo"};
        StorageUtil.deleteAllFiles(path, fileName);
        mRepository.clear(AQBWApplyCacheName.CACHE_NAME);
    }
}
