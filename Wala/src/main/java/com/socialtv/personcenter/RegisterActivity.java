package com.socialtv.personcenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.AuthCode;
import com.socialtv.personcenter.entity.RegisterResponse;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StorageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * user Registation pager
 *
 * 注册页面
 */
@ContentView(R.layout.register_layout)
public class RegisterActivity extends DialogFragmentActivity {

    private static final int SET_PHOTO_REQUEST_CODE = 0x101;

    private static final int CUT_PHOTO_REQUEST_CODE = 0x102;

    @InjectView(R.id.register_flipper)
    private ViewFlipper viewFlipper;

    @InjectView(R.id.register_mobile_number_edit)
    private EditText mobileNumberEdit;

    @InjectView(R.id.register_mobile_number_hint)
    private TextView mobileNumberHint;

    @InjectView(R.id.register_mobile_number_next)
    private View mobileNumberNext;

    @InjectView(R.id.auth_code_edit)
    private EditText authCodeEdit;

    @InjectView(R.id.auth_code_hint)
    private TextView authCodeHint;

    @InjectView(R.id.auth_code_afresh_send)
    private Button authCodeAfreshSend;

    @InjectView(R.id.auth_code_next)
    private Button authCodeNext;

    @InjectView(R.id.register_nickname)
    private EditText nicknameEdit;

    @InjectView(R.id.register_password)
    private EditText passwordEdit;

    @InjectView(R.id.register_next)
    private View nextView;

    @InjectView(R.id.register_avatar)
    private View avatarView;

    @InjectView(R.id.register_avatar_image)
    private ImageView avatarImage;

    @InjectView(R.id.register_takephoto)
    private View takephotoView;

    @Inject
    private Activity activity;

    @Inject
    private UserService service;

    @InjectResource(R.color.white_grey)
    private int whiteGreyColor;

    @InjectResource(R.color.light_grey)
    private int lightGreyColor;

    private int mSendTime = 60;

    private final Handler mHandler = new Handler();

    private final SecurityCodeRunnable runnable = new SecurityCodeRunnable();

    private Dialog dialog;

    private File saveFile;

    private Bitmap bitmap;

    private long toVerify = 1;

    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar  actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("注册");
        setSupportProgressBarIndeterminateVisibility(false);

        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Wala");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }

        final SharedPreferences preferences = getSharedPreferences(IConstant.MESSAGE, MODE_PRIVATE);
        mobileNumberHint.setText(preferences.getString(IConstant.REGISTER_MOBILE, ""));
        authCodeHint.setText(preferences.getString(IConstant.VERIFY_CODE_MESSAGE, ""));

        /**
         * 手机号码的点击事件和请求
         */
        mobileNumberNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeEdit.setText("");
                final String mobileNumber = mobileNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mobileNumber)) {
                    ToastUtils.show(activity, "手机号码不能为空");
                    return;
                }
                if (mobileNumber.length() < 11) {
                    ToastUtils.show(activity, "格式不正确");
                    return;
                }
                if (!mobileNumber.equals(mobile)) {
                    new ProgressDialogTask<AuthCode>(activity) {
                        /**
                         * Execute task with an authenticated account
                         *
                         * @param data
                         * @return result
                         * @throws Exception
                         */
                        @Override
                        protected AuthCode run(Object data) throws Exception {
                            return (AuthCode) HttpUtils.doRequest(service.createGetAuthCode(mobileNumber, 1)).result;
                        }

                        @Override
                        protected void onSuccess(AuthCode authCode) throws Exception {
                            super.onSuccess(authCode);
                            if (authCode != null) {
                                if (authCode.getResponseCode() == 0) {
                                    toVerify = authCode.getToVerify();
                                    if (authCode.getToVerify() == 1) {
                                        mHandler.postDelayed(runnable, 1000);
                                        viewFlipper.showNext();
                                        mobile = mobileNumber;
                                    } else {
                                        viewFlipper.setDisplayedChild(2);
                                    }
                                } else {
                                    ToastUtils.show(activity, authCode.getResponseMessage());
                                }
                            }
                        }
                    }.start("正在提交");
                } else {
                    if (toVerify == 1) {
                        viewFlipper.showNext();
                        mHandler.postDelayed(runnable, 1000);
                    } else {
                        viewFlipper.setDisplayedChild(3);
                    }
                }
            }
        });

        /**
         * 验证码的点击事件和请求
         */
        authCodeNext.setText(getString(R.string.next));
        authCodeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = mobileNumberEdit.getText().toString().trim();
                final String authCode = authCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(authCode)) {
                    ToastUtils.show(activity, "验证码不能为空");
                    return;
                }
                new ProgressDialogTask<Response>(activity){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Response run(Object data) throws Exception {
                        final Map<String, String> bodys = new HashMap<String, String>();
                        bodys.put("mobile", mobile);
                        bodys.put("code", authCode);
                        return (Response) HttpUtils.doRequest(service.createVerifyAuthCode(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                authCodeEdit.setText("");
                                viewFlipper.showNext();
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }

                        }
                    }
                }.start("正在提交");
            }
        });

        /**
         * 重新发送验证码的点击事件和请求
         */
        authCodeAfreshSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobileNumber = mobileNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mobileNumber)) {
                    ToastUtils.show(activity, "手机号码不能为空");
                    return;
                }
                if (mobileNumber.length() < 11) {
                    ToastUtils.show(activity, "格式不正确");
                    return;
                }
                mSendTime = 60;
                mHandler.postDelayed(runnable, 1000);
                new ProgressDialogTask<AuthCode>(activity){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected AuthCode run(Object data) throws Exception {
                        return (AuthCode) HttpUtils.doRequest(service.createGetAuthCode(mobileNumber, 1)).result;
                    }

                    @Override
                    protected void onSuccess(AuthCode authCode) throws Exception {
                        super.onSuccess(authCode);
                        if (authCode != null) {
                            if (authCode.getResponseCode() == 0) {

                            } else {
                                ToastUtils.show(activity, authCode.getResponseMessage());
                            }
                        }
                    }
                }.start("正在提交");
            }
        });


        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.user_choice_photo);
        View textAibum = dialog.findViewById(R.id.choice_aibum);
        View textCamera = dialog.findViewById(R.id.choice_camera);
        textAibum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
            }
        });
        textCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                saveFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
                startActivityForResult(intentCamera, SET_PHOTO_REQUEST_CODE);
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();
            }
        });

        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = mobileNumberEdit.getText().toString();
                final String nickname = nicknameEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                if (TextUtils.isEmpty(nickname)) {
                    ToastUtils.show(activity, "昵称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.show(activity, "密码不能为空");
                    return;
                }
                new ProgressDialogTask<RegisterResponse>(activity){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected RegisterResponse run(Object data) throws Exception {
                        return (RegisterResponse) HttpUtils.doRequest(service.createRegister(saveFile, account, nickname, password)).result;
                    }

                    /**
                     * Sub-classes may override but should always call super to ensure the
                     * progress dialog is dismissed
                     *
                     * @param registerResponse
                     */
                    @Override
                    protected void onSuccess(RegisterResponse registerResponse) throws Exception {
                        super.onSuccess(registerResponse);
                        if (registerResponse != null) {
                            if(registerResponse.getResponseCode()==0){
                                LoginUtil.saveUserId(getApplicationContext(), String.valueOf(registerResponse.getUser().getUserId()));
                                LoginUtil.saveLoginState(getApplicationContext(), registerResponse.getResponseCode());
                                sendBroadcast(new Intent(IConstant.USER_LOGIN));
                                startActivity(new Intents(activity, InviteCodeActivity.class).toIntent());
                                finish();
                            } else {
                                ToastUtils.show(RegisterActivity.this, registerResponse.getResponseMessage());
                                LoginUtil.saveLoginState(getApplicationContext(), registerResponse.getResponseCode());
                            }
                        }
                    }
                }.start("正在提交");
            }
        });
    }

    /**
     * 取消对话框
     */
    private void cancelDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileOutputStream outputStream = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CUT_PHOTO_REQUEST_CODE) {
                if (data != null) {
                    cancelDialog();
                    try {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis() + ".jpg");
                        outputStream = new FileOutputStream(saveFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        hide(takephotoView);
                        ImageLoader.getInstance().displayImage(Uri.fromFile(saveFile).toString(), avatarImage, ImageUtils.imageLoader(activity, 1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                if (data != null) {
                    cropImageUri(data.getData());
                } else {
                    cropImageUri(Uri.fromFile(saveFile));
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            show(takephotoView);
        }

    }

    /**
     * 调用系统裁剪头像图片
     */
    private void cropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 180);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
    }

    /**
     * 重新发送验证码计时的Runnable
     */
    private class SecurityCodeRunnable implements Runnable {
        @Override
        public void run() {
            mSendTime--;
            authCodeAfreshSend.setClickable(false);
            authCodeAfreshSend.setTextColor(lightGreyColor);
            authCodeAfreshSend.setText("重新发送(" + mSendTime + ")");

            if(mSendTime == 0) {
                authCodeAfreshSend.setTextColor(whiteGreyColor);
                authCodeAfreshSend.setText("重新发送");
                authCodeAfreshSend.setClickable(true);
            }else {
                mHandler.postDelayed(this, 1000);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                int displayedChild = viewFlipper.getDisplayedChild();
                if (displayedChild != 0) {
                    if (displayedChild == 1) {
                        mHandler.removeCallbacks(runnable);
                    }
                    viewFlipper.showPrevious();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }
}
