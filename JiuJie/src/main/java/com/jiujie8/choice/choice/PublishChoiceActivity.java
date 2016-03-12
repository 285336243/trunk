package com.jiujie8.choice.choice;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.core.ProgressDialogTask;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.http.MultipartRequest;
import com.jiujie8.choice.http.entity.UploadMultipartEntity;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/12/11.
 *
 * 发布话题的页面
 */
@ContentView(R.layout.publish_choice)
public class PublishChoiceActivity extends DialogFragmentActivity {

    private final static int CAMERA = 0;

    private final static int PHOTO = 1;

    private final static int CROP_CODE = 2;

    private static String orientation = "Orientation";

    private final static String LEFT = "left";

    private final static String RIGHT = "right";

    @InjectView(R.id.publish_choice_group)
    private LinearLayout mGroup;

    @InjectView(R.id.publish_choice_close)
    private ImageButton mCloseButton;

    @InjectView(R.id.publish_choice_submit)
    private ImageButton mSubmitButton;

    @InjectView(R.id.publish_choice_is_vote)
    private CheckBox mIsVoteBox;

    @InjectView(R.id.publish_choice_or_button)
    private CheckBox mOrCheckBox;

    @InjectView(R.id.publish_choice_text_button)
    private CheckBox mTextCheckBox;

    @InjectView(R.id.publish_choice_yn_button)
    private CheckBox mYnCheckBox;

    @InjectView(R.id.publish_choice_or_layout)
    private FrameLayout mOrLayout;

    @InjectView(R.id.publish_choice_or_left_layout)
    private View mLeftViewLayout;

    @InjectView(R.id.publish_choice_or_left_bkg_img)
    private ImageView mLeftBkgImage;

    @InjectView(R.id.publish_choice_or_left_image)
    private ImageView mLeftImage;

    @InjectView(R.id.publish_choice_or_left_delete_button)
    private ImageButton mLeftDeleteButton;

    @InjectView(R.id.publish_choice_or_left_button)
    private ImageButton mLeftButton;

    @InjectView(R.id.publish_choice_left_photo)
    private ImageButton mLeftPhotoButton;

    @InjectView(R.id.publish_choice_left_camera)
    private ImageButton mLeftCameraButton;

    @InjectView(R.id.publish_choice_right_photo)
    private ImageButton mRightPhotoButton;

    @InjectView(R.id.publish_choice_right_camera)
    private ImageButton mRightCameraButton;

    @InjectView(R.id.publish_choice_or_right_bkg_img)
    private ImageView mRightBkgImage;

    @InjectView(R.id.publish_choice_or_right_layout)
    private View mRightViewLayout;

    @InjectView(R.id.publish_choice_or_right_image)
    private ImageView mRightImage;

    @InjectView(R.id.publish_choice_or_right_button)
    private ImageButton mRightButton;

    @InjectView(R.id.publish_choice_or_right_delete_button)
    private ImageButton mRightDeleteButton;

    @InjectView(R.id.publish_choice_or_edit)
    private EditText mOrEditText;

    @InjectView(R.id.publish_choice_text_layout)
    private FrameLayout mTextLayout;

    @InjectView(R.id.publish_choice_text_tip)
    private TextView mTextTipText;

    @InjectView(R.id.publish_choice_text_bkg_text)
    private EditText mTextBkgEditText;

    @InjectView(R.id.publish_choice_text_edit)
    private EditText mTextEditText;

    @InjectView(R.id.publish_choice_text_yes_button)
    private ImageButton mTextYesButton;

    @InjectView(R.id.publish_choice_text_no_button)
    private ImageButton mTextNoButton;

    @InjectView(R.id.publish_choice_yn_layout)
    private FrameLayout mYnLayout;

    @InjectView(R.id.publish_choice_yn_image)
    private ImageView mYnImageView;

    @InjectView(R.id.publish_choice_yn_delete_button)
    private ImageButton mYnDeleteButton;

    @InjectView(R.id.publish_choice_yn_edit)
    private EditText mYnEditText;

    @InjectView(R.id.publish_choice_yn_yes_button)
    private ImageButton mYnYesButton;

    @InjectView(R.id.publish_choice_yn_no_button)
    private ImageButton mYnNoButton;

    @InjectView(R.id.publish_choice_yn_photo)
    private ImageButton mYnPhotoButton;

    @InjectView(R.id.publish_choice_yn_camera)
    private ImageButton mYnCameraButton;

    @InjectView(R.id.publish_choice_or_include)
    private View mOrIncludeView;

    @InjectView(R.id.publish_choice_text_include)
    private View mTextIncludeView;

    @InjectView(R.id.publish_choice_yn_include)
    private View mYnIncludeView;

    private File saveFile;

    private File mOrLeftFile;

    private File mOrRightFile;

    private File mYnFile;

    private final static Map<Integer, Color> COLORS = new HashMap<Integer, Color>();

    private Color mColor;

    private float downX = 0, upX = 0;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        COLORS.put(0, new Color(244, 164, 76));
        COLORS.put(1, new Color(43, 196, 90));
        COLORS.put(2, new Color(117, 198, 255));
        COLORS.put(3, new Color(208, 92, 92));
        COLORS.put(4, new Color(108, 124, 192));
        COLORS.put(5, new Color(255, 134, 134));
        COLORS.put(6, new Color(211, 140, 248));

        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = (int) (width * 0.9);
        final int editHeight = (int) (width * 0.4);
        final LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        final LinearLayout.LayoutParams mEditParms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, editHeight);
        mOrLayout.setLayoutParams(mLayoutParams);
        mOrEditText.setLayoutParams(mEditParms);
        mTextLayout.setLayoutParams(mLayoutParams);
        mTextEditText.setLayoutParams(mEditParms);
        mYnLayout.setLayoutParams(mLayoutParams);
        mYnEditText.setLayoutParams(mEditParms);

        mOrCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOrShowCancelDialog();
            }
        });

        mTextCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTextShowCancelDialog();
            }
        });

        mYnCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYnShowCancelDialog();
            }
        });

        //or类型左边整体的点击事件,点击切换背景,显示和隐藏相应的控件
        mLeftViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                mLeftPhotoButton.setVisibility(View.VISIBLE);
                mLeftCameraButton.setVisibility(View.VISIBLE);
                mRightPhotoButton.setVisibility(View.GONE);
                mRightCameraButton.setVisibility(View.GONE);
            }
        });

        //or类型左边的图库事件
        mLeftPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGalleryIntent(LEFT);
            }
        });

        //or类型左边的图片删除事件
        mLeftDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftViewLayout.setEnabled(true);
                mLeftImage.setImageBitmap(null);
                mLeftPhotoButton.setVisibility(View.VISIBLE);
                mLeftCameraButton.setVisibility(View.VISIBLE);
                mLeftDeleteButton.setVisibility(View.GONE);
                mRightPhotoButton.setVisibility(View.GONE);
                mRightCameraButton.setVisibility(View.GONE);
                mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                if (mOrLeftFile != null && mOrLeftFile.exists()) {
                    mOrLeftFile.delete();
                    mOrLeftFile = null;
                }
            }
        });

        //or类型左边的相机点击事件
        mLeftCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectCameraIntent(LEFT);
            }
        });

        //or类型右边的整体点击事件,点击切换背景,显示和隐藏相应的控件
        mRightViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                mLeftPhotoButton.setVisibility(View.GONE);
                mLeftCameraButton.setVisibility(View.GONE);
                mRightPhotoButton.setVisibility(View.VISIBLE);
                mRightCameraButton.setVisibility(View.VISIBLE);
            }
        });

        //or类型右边的图库点击事件
        mRightPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGalleryIntent(RIGHT);
            }
        });

        //or 类型右边的相机点击事件
        mRightCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCameraIntent(RIGHT);
            }
        });

        //or类型右边图片的删除按钮
        mRightDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRightViewLayout.setEnabled(true);
                mRightImage.setImageBitmap(null);
                mRightPhotoButton.setVisibility(View.VISIBLE);
                mRightCameraButton.setVisibility(View.VISIBLE);
                mRightDeleteButton.setVisibility(View.GONE);
                mLeftPhotoButton.setVisibility(View.GONE);
                mLeftCameraButton.setVisibility(View.GONE);
                mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                if (mOrRightFile != null && mOrRightFile.exists()) {
                    mOrRightFile.delete();
                    mOrRightFile = null;
                }
            }
        });

        //yn类型图库点击事件
        mYnPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGalleryIntent(null);
            }
        });

        //yn类型相机点击事件
        mYnCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCameraIntent(null);
            }
        });

        //yn类型的删除图片事件
        mYnDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYnPhotoButton.setVisibility(View.VISIBLE);
                mYnCameraButton.setVisibility(View.VISIBLE);
                mYnDeleteButton.setVisibility(View.GONE);
                mYnImageView.setImageBitmap(null);
                if (mYnFile != null && mYnFile.exists()) {
                    mYnFile.delete();
                    mYnFile = null;
                }
            }
        });

        //关闭点击事件,根据group的checkId来判断是否要弹出取消编辑的提示框
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkShowCancelDialog();
            }
        });

        //是否支持投票
        mIsVoteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLeftButton.setVisibility(View.VISIBLE);
                    mRightButton.setVisibility(View.VISIBLE);
                    mTextYesButton.setVisibility(View.VISIBLE);
                    mTextNoButton.setVisibility(View.VISIBLE);
                    mYnYesButton.setVisibility(View.VISIBLE);
                    mYnNoButton.setVisibility(View.VISIBLE);
                } else {
                    mLeftButton.setVisibility(View.GONE);
                    mRightButton.setVisibility(View.GONE);
                    mTextYesButton.setVisibility(View.GONE);
                    mTextNoButton.setVisibility(View.GONE);
                    mYnYesButton.setVisibility(View.GONE);
                    mYnNoButton.setVisibility(View.GONE);
                }
            }
        });

        //发布点击 ,根据group的checkId来判断发布的类型
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrCheckBox.isChecked()) {
                    submitOrChoice();
                }
                if (mTextCheckBox.isChecked()) {
                    submitTextChoice();
                }

                if (mYnCheckBox.isChecked()) {
                    submitYnChoice();
                }
            }
        });

        mTextBkgEditText.setLongClickable(false);

        //text类型的输入滑动事件,滑动更改背景
        mTextBkgEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        upX = event.getRawX();
                        if (upX - downX >= 20) {
                            //下一个
                            position++;
                            if (position > COLORS.size() - 1) {
                                position = 0;
                            }
                        } else if (upX - downX <= -20) {
                            //上一个
                            position--;
                            if (position < 0) {
                                position = COLORS.size() - 1;
                            }
                        }
                        mColor = COLORS.get(position);
                        mTextBkgEditText.setBackgroundColor(android.graphics.Color.rgb(mColor.getR(), mColor.getG(), mColor.getB()));
                        if (mTextTipText.getVisibility() == View.VISIBLE) {
                            mTextTipText.setVisibility(View.GONE);
                        }
                        if (downX != upX) {
                            return true;
                        }
                        if (upX - downX < 5 || upX - downX > -5){
                            InputMethodManager manager = (InputMethodManager) mTextBkgEditText.getContext()
                                    .getSystemService(INPUT_METHOD_SERVICE);
                            if (manager != null)
                                manager.showSoftInputFromInputMethod(mTextBkgEditText.getWindowToken(), 0);
                            return false;
                        }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkShowCancelDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 点击多图的时候判断是否有取消框出现
     */
    private void checkOrShowCancelDialog() {
        final String mBkgContent = mTextBkgEditText.getText().toString();
        final String mCount = mTextEditText.getText().toString();
        final String mYnCount = mYnEditText.getText().toString();
        if (!TextUtils.isEmpty(mBkgContent) || !TextUtils.isEmpty(mCount) || mYnFile != null || !TextUtils.isEmpty(mYnCount)) {
            CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
            if (mDialog == null) {
                mDialog = new CancelEditDialog();
                mDialog.show(getSupportFragmentManager(), "cancel");
            }
            mDialog.setContinueListener(new CancelEditDialog.CancelEditDialogContinueListener() {
                @Override
                public void onGiveUpListener() {
                    mYnCheckBox.setChecked(false);
                    mOrCheckBox.setChecked(true);
                    mTextCheckBox.setChecked(false);
                    mOrIncludeView.setVisibility(View.VISIBLE);
                    mTextIncludeView.setVisibility(View.GONE);
                    mYnIncludeView.setVisibility(View.GONE);
                    mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_oractive_choice), null, null);
                    mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_text_choice), null, null);
                    mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_pic_choice), null, null);

                    mYnPhotoButton.setVisibility(View.VISIBLE);
                    mYnCameraButton.setVisibility(View.VISIBLE);
                    mYnDeleteButton.setVisibility(View.GONE);
                    mYnImageView.setImageBitmap(null);
                    if (mYnFile != null && mYnFile.exists()) {
                        mYnFile.delete();
                        mYnFile = null;
                    }
                    mTextBkgEditText.setText("");
                    mTextEditText.setText("");
                    mYnEditText.setText("");
                }

                @Override
                public void onContinueListener() {
                }

            });
        } else {
            mYnCheckBox.setChecked(false);
            mOrCheckBox.setChecked(true);
            mTextCheckBox.setChecked(false);
            mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_oractive_choice), null, null);
            mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_text_choice), null, null);
            mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_pic_choice), null, null);
            mOrIncludeView.setVisibility(View.VISIBLE);
            mTextIncludeView.setVisibility(View.GONE);
            mYnIncludeView.setVisibility(View.GONE);
        }
    }

    /**
     * 点击文本的时候判断是否有取消框出现
     */
    private void checkTextShowCancelDialog() {
        final String mYnCount = mYnEditText.getText().toString();
        final String mOrContent = mOrEditText.getText().toString();

        if (!TextUtils.isEmpty(mYnCount) || !TextUtils.isEmpty(mOrContent) || mYnFile != null || mOrRightFile != null || mOrLeftFile != null) {
            CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
            if (mDialog == null) {
                mDialog = new CancelEditDialog();
                mDialog.show(getSupportFragmentManager(), "cancel");
            }
            mDialog.setContinueListener(new CancelEditDialog.CancelEditDialogContinueListener() {
                @Override
                public void onGiveUpListener() {
                    mYnCheckBox.setChecked(false);
                    mOrCheckBox.setChecked(false);
                    mTextCheckBox.setChecked(true);
                    mOrIncludeView.setVisibility(View.GONE);
                    mTextIncludeView.setVisibility(View.VISIBLE);
                    mYnIncludeView.setVisibility(View.GONE);
                    mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_or_choice), null, null);
                    mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_textactive_choice), null, null);
                    mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_pic_choice), null, null);

                    mYnEditText.setText("");
                    mOrEditText.setText("");
                    mYnPhotoButton.setVisibility(View.VISIBLE);
                    mYnCameraButton.setVisibility(View.VISIBLE);
                    mYnDeleteButton.setVisibility(View.GONE);
                    mYnImageView.setImageBitmap(null);
                    if (mYnFile != null && mYnFile.exists()) {
                        mYnFile.delete();
                        mYnFile = null;
                    }
                    mYnImageView.setImageBitmap(null);
                    mRightImage.setImageBitmap(null);
                    mRightPhotoButton.setVisibility(View.VISIBLE);
                    mRightCameraButton.setVisibility(View.VISIBLE);
                    mRightDeleteButton.setVisibility(View.GONE);
                    mLeftPhotoButton.setVisibility(View.GONE);
                    mLeftCameraButton.setVisibility(View.GONE);
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mRightViewLayout.setEnabled(true);
                    if (mOrRightFile != null && mOrRightFile.exists()) {
                        mOrRightFile.delete();
                        mOrRightFile = null;
                    }
                    mLeftImage.setImageBitmap(null);
                    mLeftPhotoButton.setVisibility(View.VISIBLE);
                    mLeftCameraButton.setVisibility(View.VISIBLE);
                    mLeftDeleteButton.setVisibility(View.GONE);
                    mRightPhotoButton.setVisibility(View.GONE);
                    mRightCameraButton.setVisibility(View.GONE);
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mLeftViewLayout.setEnabled(true);
                    if (mOrLeftFile != null && mOrLeftFile.exists()) {
                        mOrLeftFile.delete();
                        mOrLeftFile = null;
                    }
                }

                @Override
                public void onContinueListener() {
                }
            });
        } else {
            mYnCheckBox.setChecked(false);
            mOrCheckBox.setChecked(false);
            mTextCheckBox.setChecked(true);
            mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_or_choice), null, null);
            mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_textactive_choice), null, null);
            mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_pic_choice), null, null);
            mOrIncludeView.setVisibility(View.GONE);
            mTextIncludeView.setVisibility(View.VISIBLE);
            mYnIncludeView.setVisibility(View.GONE);
        }
    }

    /**
     * 点击单图的时候判断是否有取消框出现
     */
    private void checkYnShowCancelDialog() {
        final String mBkgContent = mTextBkgEditText.getText().toString();
        final String mCount = mTextEditText.getText().toString();
        final String mOrContent = mOrEditText.getText().toString();

        if (mOrRightFile != null || mOrLeftFile != null || !TextUtils.isEmpty(mBkgContent) || !TextUtils.isEmpty(mCount) || !TextUtils.isEmpty(mOrContent)) {
            CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
            if (mDialog == null) {
                mDialog = new CancelEditDialog();
                mDialog.show(getSupportFragmentManager(), "cancel");
            }
            mDialog.setContinueListener(new CancelEditDialog.CancelEditDialogContinueListener() {
                @Override
                public void onGiveUpListener() {
                    mOrIncludeView.setVisibility(View.GONE);
                    mTextIncludeView.setVisibility(View.GONE);
                    mYnIncludeView.setVisibility(View.VISIBLE);
                    mYnCheckBox.setChecked(true);
                    mOrCheckBox.setChecked(false);
                    mTextCheckBox.setChecked(false);
                    mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_or_choice), null, null);
                    mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_text_choice), null, null);
                    mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_picactive_choice), null, null);

                    mYnPhotoButton.setVisibility(View.VISIBLE);
                    mYnCameraButton.setVisibility(View.VISIBLE);
                    mYnDeleteButton.setVisibility(View.GONE);
                    mYnImageView.setImageBitmap(null);
                    if (mYnFile != null && mYnFile.exists()) {
                        mYnFile.delete();
                        mYnFile = null;
                    }
                    mRightImage.setImageBitmap(null);
                    mRightPhotoButton.setVisibility(View.VISIBLE);
                    mRightCameraButton.setVisibility(View.VISIBLE);
                    mRightDeleteButton.setVisibility(View.GONE);
                    mLeftPhotoButton.setVisibility(View.GONE);
                    mLeftCameraButton.setVisibility(View.GONE);
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mRightViewLayout.setEnabled(true);
                    if (mOrRightFile != null && mOrRightFile.exists()) {
                        mOrRightFile.delete();
                        mOrRightFile = null;
                    }
                    mLeftImage.setImageBitmap(null);
                    mLeftPhotoButton.setVisibility(View.VISIBLE);
                    mLeftCameraButton.setVisibility(View.VISIBLE);
                    mLeftDeleteButton.setVisibility(View.GONE);
                    mRightPhotoButton.setVisibility(View.GONE);
                    mRightCameraButton.setVisibility(View.GONE);
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mLeftViewLayout.setEnabled(true);
                    if (mOrLeftFile != null && mOrLeftFile.exists()) {
                        mOrLeftFile.delete();
                        mOrLeftFile = null;
                    }
                    mOrEditText.setText("");
                    mTextBkgEditText.setText("");
                    mTextEditText.setText("");
                }

                @Override
                public void onContinueListener() {
                }
            });
        } else {
            mYnCheckBox.setChecked(true);
            mOrCheckBox.setChecked(false);
            mTextCheckBox.setChecked(false);
            mOrCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_or_choice), null, null);
            mTextCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_text_choice), null, null);
            mYnCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.btn_picactive_choice), null, null);
            mOrIncludeView.setVisibility(View.GONE);
            mTextIncludeView.setVisibility(View.GONE);
            mYnIncludeView.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 判断是否弹出取消框
     */
    private void checkShowCancelDialog() {
        if (mOrCheckBox.isChecked()) {
            final String mContent = mOrEditText.getText().toString();
            if (mOrLeftFile != null || mOrRightFile != null || !TextUtils.isEmpty(mContent)) {
                CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
                if (mDialog == null) {
                    mDialog = new CancelEditDialog();
                }
                mDialog.show(getSupportFragmentManager(), "cancel");
            } else {
                finish();
            }
        }

        if (mTextCheckBox.isChecked()) {
            final String mBkgContent = mTextBkgEditText.getText().toString();
            final String mCount = mTextEditText.getText().toString();
            if (!TextUtils.isEmpty(mBkgContent) || !TextUtils.isEmpty(mCount)) {
                CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
                if (mDialog == null) {
                    mDialog = new CancelEditDialog();
                }
                mDialog.show(getSupportFragmentManager(), "cancel");
            } else {
                finish();
            }
        }

        if (mYnCheckBox.isChecked()) {
            final String mYnCount = mYnEditText.getText().toString();
            if (mYnFile != null || !TextUtils.isEmpty(mYnCount)) {
                CancelEditDialog mDialog = (CancelEditDialog) getSupportFragmentManager().findFragmentByTag("cancel");
                if (mDialog == null) {
                    mDialog = new CancelEditDialog();
                }
                mDialog.show(getSupportFragmentManager(), "cancel");
            } else {
                finish();
            }
        }
    }

    /**
     * 发布or类型的话题
     */
    private void submitOrChoice() {
        final String mCount = mOrEditText.getText().toString();
        if (TextUtils.isEmpty(mCount)) {
            ToastUtils.show(this, "还没有输入纠结的问题");
            return;
        }

        if (mOrLeftFile == null || mOrRightFile == null) {
            ToastUtils.show(this, "添加描述才能更好的解开纠结");
            return;
        }
        final Map<String, Object> mBodys = new HashMap<String, Object>();
        mBodys.put("title", mCount);
        mBodys.put("leftImg", mOrLeftFile);
        mBodys.put("rightImg", mOrRightFile);
        if (mIsVoteBox.isChecked()) {
            mBodys.put("voteSupport", "1");
        } else {
            mBodys.put("voteSupport", "0");
        }

        final List<File> mFiles = new ArrayList<File>();
        mFiles.add(mOrLeftFile);
        mFiles.add(mOrRightFile);

        new AbstractRoboAsyncTask<Response>(this){

            @Override
            protected Response run(Object data) throws Exception {
//                ProgressDialog mProgressDialog = (ProgressDialog) getSupportFragmentManager().findFragmentByTag("submit_choice");
//                if (mProgressDialog == null) {
//                    mProgressDialog = new ProgressDialog();
//                }
                final ProgressDialog mProgressDialog = new ProgressDialog();
                mProgressDialog.show(getSupportFragmentManager(), "submit_choice");
                MultipartRequest<Response> mRequest = ChoiceService.createChoiceOrRequest(mBodys);
//                mRequest.setOnProgressListener(new UploadMultipartEntity.OnProgressListener() {
//                    @Override
//                    public void transferred(long current, long total) {
//                        System.out.println("progress = " + (((float) current / (float) total) * 100));
//                        mProgressDialog.setProgressTotalValue(100, (int)  (((float) current / (float) total) * 100));
////                        mBar.setMax(100);
////                        mBar.setProgress((int)  (((float) current / (float) total) * 100));
//                    }
//                });
//                setDialogProgress(mRequest, mProgressDialog);
                return (Response) HttpUtils.doRequest(mRequest).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                System.out.println("upload onSuccessCallback");
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    /**
     * 发布文本类型的话题
     */
    private void submitTextChoice() {
        final String mCount = mTextBkgEditText.getText().toString();
        if (TextUtils.isEmpty(mCount)) {
            ToastUtils.show(this, "添加描述才能更好的解开纠结");
            return;
        }
        final String mHeaderCount = mTextEditText.getText().toString();
        if (TextUtils.isEmpty(mHeaderCount)) {
            ToastUtils.show(this, "还没有输入纠结的问题");
            return;
        }

        if (mColor == null) {
            mColor = COLORS.get(0);
        }

        final Map<String, Object> mBodys = new HashMap<String, Object>();
        mBodys.put("title", mHeaderCount);
        mBodys.put("desc", mCount);
        mBodys.put("r", String.valueOf(mColor.getR()));
        mBodys.put("g", String.valueOf(mColor.getG()));
        mBodys.put("b", String.valueOf(mColor.getB()));
        if (mIsVoteBox.isChecked()) {
            mBodys.put("voteSupport", "1");
        } else {
            mBodys.put("voteSupport", "0");
        }

        new ProgressDialogTask<Response>(this){

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(ChoiceService.createChoiceTextRequest(mBodys)).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    /**
     * 发布Yes, no类型的话题
     */
    private void submitYnChoice() {
        final String mCount = mYnEditText.getText().toString();
        if (TextUtils.isEmpty(mCount)) {
            ToastUtils.show(this, "还没有输入纠结的问题");
            return;
        }

        if (mYnFile == null) {
            ToastUtils.show(this, "添加描述才能更好的解开纠结");
            return;
        }

        final Map<String, Object> mBodys = new HashMap<String, Object>();
        mBodys.put("title", mCount);
        mBodys.put("img", mYnFile);
        if (mIsVoteBox.isChecked()) {
            mBodys.put("voteSupport", "1");
        } else {
            mBodys.put("voteSupport", "0");
        }

        new AbstractRoboAsyncTask<Response>(this){

            @Override
            protected Response run(Object data) throws Exception {
                MultipartRequest<Response> mRequest = ChoiceService.createChoiceYnRequest(mBodys);
                mRequest.setOnProgressListener(new UploadMultipartEntity.OnProgressListener() {
                    @Override
                    public void transferred(long current, long total) {
                        System.out.println("progress = " + (((float) current / (float) total) * 100));
                        System.out.println("total="+total+" current="+current);
                    }
                });
                return (Response) HttpUtils.doRequest(mRequest).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                System.out.println("onSuccessCallback");
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    /**
     * 选择图库的Intent
     * @param value LEFT Or RIGHT
     */
    private void selectGalleryIntent(final String value) {
        Intent mPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mPhotoIntent.addCategory(Intent.CATEGORY_OPENABLE);
        mPhotoIntent.setType("image/*");
        orientation = value;
        startActivityForResult(mPhotoIntent, PHOTO);
    }

    private void selectCameraIntent(final String value) {
        saveFile = StorageUtil.createImageFile(activity);
        if (saveFile == null) {
            ToastUtils.show(activity, "请检查sd卡");
            return;
        }
        orientation = value;
        Uri uri = Uri.fromFile(saveFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA:
                    if (saveFile != null) {
                        startActivityForResult(new Intents(activity, CropImageActivity.class).toIntent().putExtra(IConstant.IMAGE_PATH, saveFile.getAbsolutePath()), CROP_CODE);
                    }
                    break;
                case PHOTO:
                    if (data != null) {
                        setPhotoResult(data);
                    }
                    break;
                case CROP_CODE:
                    if (data != null && IConstant.CROP_IMAGE_ACTION.equals(data.getAction())) {
                        final String imagePath = data.getStringExtra(IConstant.IMAGE_PATH);
                        setLoadedImage(Uri.fromFile(new File(imagePath)).toString());
                    }
                    break;
            }
        }
    }

    /**
     * 根据Uri查询数据库中的图片地址,并加载图片
     * @param data
     */
    private void setPhotoResult(final Intent data) {
        final Uri selectedImage = data.getData();
        final String[] filePathColumn = {MediaStore.Images.Media.DATA};
        final Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String filePath = cursor.getString(columnIndex);
        cursor.close();
        startActivityForResult(new Intents(activity, CropImageActivity.class).toIntent().putExtra(IConstant.IMAGE_PATH, filePath), CROP_CODE);
//        setLoadedImage(Uri.fromFile(new File(filePath)).toString());
    }

    private void setCameraResultImage(final File file) {
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), mLeftImage, ImageUtils.defaultImageLoader(), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (LEFT.equals(orientation)) {
                    mLeftImage.setImageBitmap(loadedImage);
                    mLeftViewLayout.setEnabled(false);
                    mLeftPhotoButton.setVisibility(View.GONE);
                    mLeftCameraButton.setVisibility(View.GONE);
                    mLeftDeleteButton.setVisibility(View.VISIBLE);
                    final BitmapDrawable mBitmapDrawable = (BitmapDrawable) mRightImage.getDrawable();
                    if (mBitmapDrawable != null) {
                        final Bitmap mBitmap = mBitmapDrawable.getBitmap();
                        if (mBitmap == null) {
                            mRightPhotoButton.setVisibility(View.VISIBLE);
                            mRightCameraButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mRightPhotoButton.setVisibility(View.VISIBLE);
                        mRightCameraButton.setVisibility(View.VISIBLE);
                    }
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mOrLeftFile = saveFile;
                } else if (RIGHT.equals(orientation)) {
                    mRightImage.setImageBitmap(loadedImage);
                    mRightViewLayout.setEnabled(false);
                    mRightPhotoButton.setVisibility(View.GONE);
                    mRightCameraButton.setVisibility(View.GONE);
                    mRightDeleteButton.setVisibility(View.VISIBLE);
                    final BitmapDrawable mBitmapDrawable = (BitmapDrawable) mLeftImage.getDrawable();
                    if (mBitmapDrawable != null) {
                        final Bitmap mBitmap = mBitmapDrawable.getBitmap();
                        if (mBitmap == null) {
                            mLeftPhotoButton.setVisibility(View.VISIBLE);
                            mLeftCameraButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mLeftPhotoButton.setVisibility(View.VISIBLE);
                        mLeftCameraButton.setVisibility(View.VISIBLE);
                    }
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mOrRightFile = saveFile;
                }
            }
        });
    }

    /**
     * 加载图片,和设置相应控件的显示和隐藏
     * @param uri
     */
    private void setLoadedImage(final String uri) {
        ImageLoader.getInstance().loadImage(uri, ImageUtils.defaultImageLoader(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                final File mFile = StorageUtil.createImageFile(activity);
                if (mFile == null) {
                    ToastUtils.show(activity, "请检查sd卡");
                    return;
                }
                if (LEFT.equals(orientation)) {
                    mLeftImage.setImageBitmap(loadedImage);
                    mLeftViewLayout.setEnabled(false);
                    mLeftPhotoButton.setVisibility(View.GONE);
                    mLeftCameraButton.setVisibility(View.GONE);
                    mLeftDeleteButton.setVisibility(View.VISIBLE);
                    final BitmapDrawable mBitmapDrawable = (BitmapDrawable) mRightImage.getDrawable();
                    if (mBitmapDrawable != null) {
                        final Bitmap mBitmap = mBitmapDrawable.getBitmap();
                        if (mBitmap == null) {
                            mRightPhotoButton.setVisibility(View.VISIBLE);
                            mRightCameraButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mRightPhotoButton.setVisibility(View.VISIBLE);
                        mRightCameraButton.setVisibility(View.VISIBLE);
                    }
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mOrLeftFile = mFile;
                    FileOutputStream mOutputStream = null;
                    try {
                        mOutputStream = new FileOutputStream(mOrLeftFile);
                        final boolean compress = loadedImage.compress(Bitmap.CompressFormat.JPEG, 80, mOutputStream);
                        if (compress) {
                            mOutputStream.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (mOutputStream != null)
                                mOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (RIGHT.equals(orientation)){
                    mRightImage.setImageBitmap(loadedImage);
                    mRightViewLayout.setEnabled(false);
                    mRightPhotoButton.setVisibility(View.GONE);
                    mRightCameraButton.setVisibility(View.GONE);
                    mRightDeleteButton.setVisibility(View.VISIBLE);
                    final BitmapDrawable mBitmapDrawable = (BitmapDrawable) mLeftImage.getDrawable();
                    if (mBitmapDrawable != null) {
                        final Bitmap mBitmap = mBitmapDrawable.getBitmap();
                        if (mBitmap == null) {
                            mLeftPhotoButton.setVisibility(View.VISIBLE);
                            mLeftCameraButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mLeftPhotoButton.setVisibility(View.VISIBLE);
                        mLeftCameraButton.setVisibility(View.VISIBLE);
                    }
                    mLeftBkgImage.setBackgroundColor(getResources().getColor(R.color.ligth_grey_8d));
                    mRightBkgImage.setBackgroundColor(getResources().getColor(R.color.white_6E));
                    mOrRightFile = mFile;
                    FileOutputStream mOutputStream = null;
                    try {
                        mOutputStream = new FileOutputStream(mOrRightFile);
                        final boolean compress = loadedImage.compress(Bitmap.CompressFormat.JPEG, 80, mOutputStream);
                        if (compress) {
                            mOutputStream.flush();
                        }
                        mOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (mOutputStream != null) {
                                mOutputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //yn类型
                    mYnImageView.setImageBitmap(loadedImage);
                    mYnPhotoButton.setVisibility(View.GONE);
                    mYnCameraButton.setVisibility(View.GONE);
                    mYnDeleteButton.setVisibility(View.VISIBLE);
                    FileOutputStream mOutputStream = null;
                    mYnFile = mFile;
                    try {
                        mOutputStream = new FileOutputStream(mYnFile);
                        final boolean compress = loadedImage.compress(Bitmap.CompressFormat.JPEG, 80, mOutputStream);
                        if (compress) {
                            mOutputStream.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (mOutputStream != null) {
                            try {
                                mOutputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrLeftFile != null && mOrLeftFile.exists()) {
            mOrLeftFile.delete();
        }
        if (mOrRightFile != null && mOrRightFile.exists()) {
            mOrRightFile.delete();
        }
    }
}
