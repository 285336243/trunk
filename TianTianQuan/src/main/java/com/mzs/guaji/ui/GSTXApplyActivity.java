package com.mzs.guaji.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
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
import com.mzs.guaji.util.RecordUtil;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.GifView;

import java.io.File;
import java.util.Calendar;

/**
 * Created by wlanjie on 13-12-16.
 *
 * 国色天香报名UI
 */
public class GSTXApplyActivity extends  GuaJiActivity{

    private Context context = GSTXApplyActivity.this;
    private LinearLayout mBackLayout;
    private EditText mNameText;
    private TextView mSexText;
    private TextView mBirthDataText;
    private EditText mNativePlaceText;
    private EditText mLocationText;
    private EditText mMobileNumberText;
    private TextView mPerformanceTypeText;
    private LinearLayout mSexLayout;
    private LinearLayout mBirthDataLayout;
    private LinearLayout mPerformanceTypeLayout;
    private RelativeLayout mPressedRecode;
    private RelativeLayout mSubmitLayout;
    private long id;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String selectSex = "";
    private String type = "";
    private RecordUtil mRecord;
    private GifView mGifView;
    private RelativeLayout mRecordLayout;
    private TextView mRecordText;
    private ImageView mPlayIconImage;
    private RelativeLayout mDeleteRecordLayout;
    private TextView mRecordTimeText;
    private long recordTime = 0;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle bundle) {
       super.onCreate(bundle);
        setContentView(R.layout.gstx_apply_layout);

        id = getIntent().getExtras().getLong("id", -1);
        mBackLayout = (LinearLayout) findViewById(R.id.apply_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mNameText = (EditText) findViewById(R.id.apply_edit_name);
        mSexText = (TextView) findViewById(R.id.apply_edit_sex);
        mBirthDataText = (TextView) findViewById(R.id.apply_edit_birth_data);
        mNativePlaceText = (EditText) findViewById(R.id.apply_edit_native_place);
        mLocationText = (EditText) findViewById(R.id.apply_edit_location);
        mMobileNumberText = (EditText) findViewById(R.id.apply_edit_mobile_number);
        mPerformanceTypeLayout = (LinearLayout) findViewById(R.id.performance_type_layout);
        mPerformanceTypeLayout.setOnClickListener(mPerformanceTypeClickListener);
        mPerformanceTypeText = (TextView) findViewById(R.id.apply_edit_performance_type);
        mSexLayout = (LinearLayout) findViewById(R.id.apply_sex_layout);
        mSexLayout.setOnClickListener(mSexClickListener);
        mBirthDataLayout = (LinearLayout) findViewById(R.id.apply_birth_data_layout);
        mBirthDataLayout.setOnClickListener(mBirthDataClickListener);
        mPressedRecode = (RelativeLayout) findViewById(R.id.apply_pressed_recode);
        mPressedRecode.setOnTouchListener(mPressedRecodeTouchListener);
        mSubmitLayout = (RelativeLayout) findViewById(R.id.apply_submit);
        mSubmitLayout.setOnClickListener(mSubmitClickListener);
        mGifView = (GifView) findViewById(R.id.apply_record_gif);
        mRecordLayout = (RelativeLayout) findViewById(R.id.apply_record_layout);
        mRecordText = (TextView) findViewById(R.id.apply_recode_text);
        mPlayIconImage = (ImageView) findViewById(R.id.apply_play_icon);
        mDeleteRecordLayout = (RelativeLayout) findViewById(R.id.apply_delete_record);
        mDeleteRecordLayout.setOnClickListener(mDeleteRecordClickListener);
        mRecordTimeText = (TextView) findViewById(R.id.apply_record_time);
    }

    /**
     * 返回,点击事件
     */
    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    /**
     * 性别点击事件,点击之后弹出选择性别对话框
     */
    View.OnClickListener mSexClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(new String[]{"男", "女"}, 1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (whichButton == 0) {
                                selectSex = "男";
                            }else {
                                selectSex = "女";
                            }
                        }
                    })
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if("".equals(selectSex)) {
                                mSexText.setText("女");
                            }else {
                                mSexText.setText(selectSex);
                            }
                        }
                    })
                    .create();
            mBuilder.show();
        }
    };

    View.OnClickListener mPerformanceTypeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String[] types = new String[]{"京剧","评剧","豫剧","越剧","黄梅戏","二人转","评弹","大鼓","花鼓戏","昆曲"};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setCancelable(false)
                    .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whichButton) {
                           type = types[whichButton];
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
                    if("".equals(type)) {
                        mPerformanceTypeText.setText("京剧");
                    }else {
                        mPerformanceTypeText.setText(type);
                    }
                }
            }).create();
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
     * 按下录音按钮,点击事件,当按下之后,显示录音中的gif动画,当松开之后,隐藏gif动画
     */
    View.OnTouchListener mPressedRecodeTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case  MotionEvent.ACTION_DOWN:
                    startRecord();
                    break;

                case  MotionEvent.ACTION_UP:
                    recordCompletion();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    recordCompletion();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GuaJi";
        String[] fileName = new String[]{"audio"};
        StorageUtil.deleteAllFiles(path, fileName);
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        if(mPlayIconImage.getVisibility() == View.VISIBLE) {
            mMediaPlayer = new MediaPlayer();
            try {
                if(mRecord.getRecordFile() != null) {
                    mMediaPlayer.setDataSource(mRecord.getRecordFile().getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mMediaPlayer.start();
                        }
                    });
                }
            }catch (Exception e) {
                e.printStackTrace();
                if(mMediaPlayer != null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }
        }else {
            recordTime = System.currentTimeMillis();
            mGifView.setStart();
            mRecordLayout.setVisibility(View.VISIBLE);
            mGifView.setVisibility(View.VISIBLE);
            mRecord = new RecordUtil(context);
            mRecord.startRecord();
        }
    }


    /**
     * 录音完成执行的动作
     */
    private void recordCompletion() {
        if(mPlayIconImage.getVisibility() != View.VISIBLE) {
            long time = (System.currentTimeMillis() - recordTime) / 1000;
            mGifView.setStop();
            mRecordLayout.setVisibility(View.GONE);
            mRecord.release();
            mRecordText.setVisibility(View.GONE);
            mPressedRecode.setBackgroundResource(R.drawable.bdg_pop_record);
            mPlayIconImage.setVisibility(View.VISIBLE);
            mDeleteRecordLayout.setVisibility(View.VISIBLE);
            mRecordTimeText.setVisibility(View.VISIBLE);
            mRecordTimeText.setText((int)time + "\"");
        }else {
            recordTime = 0;
        }
    }

    /**
     * 删除按钮点击事件
     */
    View.OnClickListener mDeleteRecordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           File mRecordFile = mRecord.getRecordFile();
           if(mRecordFile != null && mRecordFile.exists()) {
               mRecordFile.delete();
               mPressedRecode.setBackgroundResource(R.drawable.bdg_applybtn_tj);
               mPlayIconImage.setVisibility(View.GONE);
               mRecordTimeText.setVisibility(View.GONE);
               mDeleteRecordLayout.setVisibility(View.GONE);
               mRecordText.setVisibility(View.VISIBLE);
           }
         }
    };

    /**
     * 提交按钮点击事件
     */
    View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            execSubmitApply();
        }
    };

    /**
     * 获取提交报名的URL
     * @return
     */
    private String getApplySubmitRequest() {
        return  DOMAIN + "group/entry_form.json";
    }

    /**
     * post 提交报名信息, 防止重复点击,多次请求服务器,当点击按钮过后设置按钮的enabled为false,
     * 不管数据请求是否成功都设置按钮enabled为true
     */
    private void execSubmitApply() {
        mSubmitLayout.setEnabled(false);
        String name = mNameText.getText().toString();
        String sex = mSexText.getText().toString();
        String birthData = mBirthDataText.getText().toString();
        String nativePlace = mNativePlaceText.getText().toString();
        String location = mLocationText.getText().toString();
        String mobileNumber = mMobileNumberText.getText().toString();
        String performanceType = mPerformanceTypeText.getText().toString();
        if(TextUtils.isEmpty(name)) {
            ToastUtil.showToast(context, "姓名不能为空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(sex)) {
            ToastUtil.showToast(context, "性别不能为空");
            mSubmitLayout.setEnabled(true);
            return;
        }

        if(TextUtils.isEmpty(birthData)) {
            ToastUtil.showToast(context, "出生年月不能为空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(nativePlace)) {
            ToastUtil.showToast(context, "籍贯不能为空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(location)) {
            ToastUtil.showToast(context, "所在地不为空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(mobileNumber)) {
            ToastUtil.showToast(context, "手机号码不能为空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(performanceType)) {
            ToastUtil.showToast(context, "曲艺类型不能空");
            mSubmitLayout.setEnabled(true);
            return;
        }
        MultipartRequest<DefaultReponse> mMultipartRequest = mApi.requestMultipartPostData(getApplySubmitRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                mSubmitLayout.setEnabled(true);
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        ToastUtil.showToast(context, "提交成功了");
                        finish();
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, this);
        mMultipartRequest.addMultipartStringEntity("id", id+"");
        mMultipartRequest.addMultipartStringEntity("name", name);
        mMultipartRequest.addMultipartStringEntity("gender", sex);
        mMultipartRequest.addMultipartStringEntity("birthday", birthData);
        mMultipartRequest.addMultipartStringEntity("nativePlace", nativePlace);
        mMultipartRequest.addMultipartStringEntity("location", location);
        mMultipartRequest.addMultipartStringEntity("mobile",mobileNumber);
        mMultipartRequest.addMultipartStringEntity("type", performanceType);
        mMultipartRequest.addMultipartFileEntity("audio", mRecord.getRecordFile());
        mApi.addRequest(mMultipartRequest);
    }

}
