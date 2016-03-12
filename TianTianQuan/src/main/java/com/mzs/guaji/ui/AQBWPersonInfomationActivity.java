package com.mzs.guaji.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import com.mzs.guaji.R;
import com.mzs.guaji.util.AQBWApplyCacheName;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.ToastUtil;

import java.io.File;
import java.util.Calendar;

/**
 * Created by wlanjie on 14-1-6.
 * 爱情保卫战报名填写详细资料UI
 */
public class AQBWPersonInfomationActivity extends SetAvatarActivity {

    private Context context = AQBWPersonInfomationActivity.this;
    private EditText mNameText;
    private LinearLayout mBirthDataLayout;
    private TextView mBirthDataText;
    private EditText mNativePlaceText;
    private EditText mLocationText;
    private EditText mMyHeightText;
    private EditText mMyWeightText;
    private LinearLayout mEducationBackgroundLayout;
    private TextView mEducationBackgroundText;
    private EditText mProfessionText;
    private LinearLayout mIncomeLayout;
    private TextView mIncomeText;
    private EditText mMobileNumberText;
    private EditText mQQText;
    private Button mConfirmButton;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String income = "";
    private String educationBackground = "";
    private String title;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aqbw_information_layout);
        title = getIntent().getStringExtra("title");
        mAvatarLayout = (RelativeLayout) findViewById(R.id.aqbw_avatar_layout);
        mAvatarLayout.setOnClickListener(mAvatarClickListener);
        mAvatarImage = (ImageView) findViewById(R.id.aqbw_avatar);
        TextView mBackText = (TextView) findViewById(R.id.aqbw_info_back);
        mBackText.setOnClickListener(mBackClickListener);
        TextView mTitleText = (TextView) findViewById(R.id.aqbw_info_titile);
        mTitleText.setText(title);
        mNameText = (EditText) findViewById(R.id.aqbw_info_edit_name);
        mBirthDataLayout = (LinearLayout) findViewById(R.id.aqbw_info_birth_data_layout);
        mBirthDataLayout.setOnClickListener(mBirthDataClickListener);
        mBirthDataText = (TextView) findViewById(R.id.aqbw_info_edit_birth_data);
        mNativePlaceText = (EditText) findViewById(R.id.aqbw_info_edit_native_place);
        mLocationText = (EditText) findViewById(R.id.aqbw_info_edit_location);
        mMyHeightText = (EditText) findViewById(R.id.aqbw_info_edit_my_height);
        mMyWeightText = (EditText) findViewById(R.id.aqbw_info_edit_my_weight);
        mEducationBackgroundLayout = (LinearLayout) findViewById(R.id.aqbw_info_education_background);
        mEducationBackgroundLayout.setOnClickListener(mEducationBackgroundClickListener);
        mEducationBackgroundText = (TextView) findViewById(R.id.aqbw_info_edit_education_background);
        mProfessionText = (EditText) findViewById(R.id.aqbw_info_edit_profession);
        mIncomeLayout = (LinearLayout) findViewById(R.id.aqbw_info_income_layout);
        mIncomeLayout.setOnClickListener(mIncomeClickListener);
        mIncomeText = (TextView) findViewById(R.id.aqbw_info_edit_income);
        mMobileNumberText = (EditText) findViewById(R.id.aqbw_info_edit_mobile_number);
        mQQText = (EditText) findViewById(R.id.aqbw_info_edit_QQ);
        mConfirmButton = (Button) findViewById(R.id.aqbw_submit_consent_button);
        mConfirmButton.setOnClickListener(mConfirmClickListener);
        modificationInfo();
    }

    private void modificationInfo() {
        if("男方资料".equals(title)) {
            String manName = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NAME);
            mNameText.setText(manName);
            String manBirthData = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_BIRTHDATA);
            mBirthDataText.setText(manBirthData);
            String manNativePlace = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NATIVEPLACE);
            mNativePlaceText.setText(manNativePlace);
            String manLocation = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_LOCATION);
            mLocationText.setText(manLocation);
            String manHeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_HEIGHT);
            mMyHeightText.setText(manHeight);
            String manWeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_WEIGHT);
            mMyWeightText.setText(manWeight);
            String manEducationBackground = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_EDUCATION_BACKGROUND);
            mEducationBackgroundText.setText(manEducationBackground);
            String manProfession = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_PROFESSION);
            mProfessionText.setText(manProfession);
            String manIncome = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_INCOME);
            mIncomeText.setText(manIncome);
            String manMobileNumber = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_MOBILE_NUMBER);
            mMobileNumberText.setText(manMobileNumber);
            String manQQ = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_QQ);
            mQQText.setText(manQQ);
            String manImagePath = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_IMAGE_PATH);
            if(!TextUtils.isEmpty(manImagePath)) {
                mImageLoader.displayImage(Uri.fromFile(new File(manImagePath)).toString(), mAvatarImage, ImageUtils.imageLoader(context, 4));
            }
        }else {
            String womanName = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NAME);
            mNameText.setText(womanName);
            String womanBirthData = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_BIRTHDATA);
            mBirthDataText.setText(womanBirthData);
            String womanNativePlace = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NATIVEPLACE);
            mNativePlaceText.setText(womanNativePlace);
            String womanLocation = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_LOCATION);
            mLocationText.setText(womanLocation);
            String womanHeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_HEIGHT);
            mMyHeightText.setText(womanHeight);
            String womanWeight = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_WEIGHT);
            mMyWeightText.setText(womanWeight);
            String womanEducationBackground = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_EDUCATION_BACKGROUND);
            mEducationBackgroundText.setText(womanEducationBackground);
            String womanProfession = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_PROFESSION);
            mProfessionText.setText(womanProfession);
            String womanIncome = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_INCOME);
            mIncomeText.setText(womanIncome);
            String womanMobileNumber = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_MOBILE_NUMBER);
            mMobileNumberText.setText(womanMobileNumber);
            String womanQQ = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_QQ);
            mQQText.setText(womanQQ);
            String womanImagePath = mRepository.getString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_IMAGE_PATH);
            if(!TextUtils.isEmpty(womanImagePath)) {
                mImageLoader.displayImage(Uri.fromFile(new File(womanImagePath)).toString(), mAvatarImage, ImageUtils.imageLoader(context, 4));
            }
        }
    }

    /**
     * 上传图片点击事件
     */
    View.OnClickListener mAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSetAvatarDialog();
            if(saveFile != null && "男方资料".equals(title)) {
                mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_IMAGE_PATH, saveFile.getAbsolutePath());
            }
            if(saveFile != null && "女方资料".equals(title)) {
                mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_IMAGE_PATH, saveFile.getAbsolutePath());
            }
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
     * 年收入点击事件
     */
    View.OnClickListener mIncomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String[] types = new String[]{"1万以下", "1万~3万", "3万~5万", "5万~8万", "8万~10万", "10万以上"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            income = types[whichButton];
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
                            if("".equals(income)) {
                                mIncomeText.setText(types[0]);
                            }else {
                                mIncomeText.setText(income);
                            }
                        }
                    }).create();
            mBuilder.show();
        }
    };

    /**
     * 学历点击事件
     */
    View.OnClickListener mEducationBackgroundClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String[] types = new String[]{"初中及以下", "高中", "中专/技校", "大专", "本科", "硕士及以上"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                            educationBackground = types[whichButton];
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
                            if("".equals(educationBackground)) {
                                mEducationBackgroundText.setText(types[0]);
                            }else {
                                mEducationBackgroundText.setText(educationBackground);
                            }
                        }
                    }).create();
            mBuilder.show();
        }
    };

    /**
     * 确定按钮点击事件
     */
    View.OnClickListener mConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDetailedInfo();
        }
    };

    /**
     * 获取填写的资料
     */
    private void getDetailedInfo() {
        String name = mNameText.getText().toString();
        String birthData = mBirthDataText.getText().toString();
        String nativePlace = mNativePlaceText.getText().toString();
        String location = mLocationText.getText().toString();
        String height = mMyHeightText.getText().toString();
        String weight = mMyWeightText.getText().toString();
        String educationBackground = mEducationBackgroundText.getText().toString();
        String profession = mProfessionText.getText().toString();
        String income = mIncomeText.getText().toString();
        String mobileNumber = mMobileNumberText.getText().toString();
        String qq = mQQText.getText().toString();
        if(saveFile == null) {
            ToastUtil.showToast(context, "图片不能为空");
            return;
        }
        if(TextUtils.isEmpty(name)) {
            ToastUtil.showToast(context, "姓名不能为空");
            return;
        }
        if(TextUtils.isEmpty(birthData)) {
            ToastUtil.showToast(context, "出生年月不能为空");
            return;
        }
        if(TextUtils.isEmpty(nativePlace)) {
            ToastUtil.showToast(context, "籍贯不能为空");
            return;
        }
        if(TextUtils.isEmpty(location)) {
            ToastUtil.showToast(context, "所在地不能为空");
            return;
        }
        if(TextUtils.isEmpty(height)) {
            ToastUtil.showToast(context, "身高不能为空");
            return;
        }
        if(TextUtils.isEmpty(weight)) {
            ToastUtil.showToast(context, "体重不能为空");
            return;
        }
        if(TextUtils.isEmpty(educationBackground)) {
            ToastUtil.showToast(context, "学历不能为空");
            return;
        }
        if(TextUtils.isEmpty(profession)) {
            ToastUtil.showToast(context, "职业不能为空");
            return;
        }
        if(TextUtils.isEmpty(income)) {
            ToastUtil.showToast(context, "年收不能为空");
            return;
        }
        if(TextUtils.isEmpty(mobileNumber)) {
            ToastUtil.showToast(context, "手机号码不能为空");
            return;
        }
        if(TextUtils.isEmpty(qq)) {
            ToastUtil.showToast(context, "QQ不能为空");
            return;
        }
        if("男方资料".equals(title)) {
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NAME, name);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_BIRTHDATA, birthData);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_NATIVEPLACE, nativePlace);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_LOCATION, location);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_HEIGHT, height);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_WEIGHT, weight);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_EDUCATION_BACKGROUND, educationBackground);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_PROFESSION, profession);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_INCOME, income);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_MOBILE_NUMBER, mobileNumber);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_QQ, qq);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.MAN_IMAGE_PATH, saveFile.getAbsolutePath());
        }else {
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NAME, name);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_BIRTHDATA, birthData);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_NATIVEPLACE, nativePlace);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_LOCATION, location);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_HEIGHT, height);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_WEIGHT, weight);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_EDUCATION_BACKGROUND, educationBackground);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_PROFESSION, profession);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_INCOME, income);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_MOBILE_NUMBER, mobileNumber);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_QQ, qq);
            mRepository.putString(AQBWApplyCacheName.CACHE_NAME, AQBWApplyCacheName.WOMAN_IMAGE_PATH, saveFile.getAbsolutePath());
        }
        finish();
    }
}