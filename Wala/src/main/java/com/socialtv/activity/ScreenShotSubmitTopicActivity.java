package com.socialtv.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.topic.CancelDialog;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.io.File;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-21.
 *
 * 发布截图的话题
 */
@ContentView(R.layout.screen_shot_submit_topic)
public class ScreenShotSubmitTopicActivity extends ThirdPartyShareActivity {

    @InjectView(R.id.screen_shot_submit_topic_image)
    private ImageView imageView;

    @InjectView(R.id.screen_shot_submit_topic_edit)
    private EditText editText;

    @InjectView(R.id.screen_shot_submit_topic_tencent)
    private CheckBox tencentCheckBox;

    @InjectView(R.id.screen_shot_submit_topic_sina)
    private CheckBox sinaCheckBox;

    @InjectExtra(value = IConstant.IMAGE_PATH, optional = true)
    private String imagePath;

    @InjectExtra(value = IConstant.ACTIVITY_ID, optional = true)
    private String activityId;

    @InjectExtra(value = IConstant.SHARE_TEXT, optional = true)
    private String shareText;

    @Inject
    private ActivityServices services;

    private boolean isPublish = true;

    private File file;

    private Bitmap bitmap;

    private CancelDialog dialog;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("发布");

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        file = new File(imagePath);
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), imageView, ImageUtils.imageLoader(this, 0), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                bitmap = loadedImage;
            }
        });

        dialog = new CancelDialog(this, new CancelDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismissListener() {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "发布话题")
                .setIcon(R.drawable.btn_topsend_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showCancelDialog();
        } else if (item.getItemId() == 0) {
            if (isPublish) {
                submitTopic();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCancelDialog() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        final String text = editText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            finish();
        }
    }

    private void submitTopic() {
        final String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            isPublish = true;
            ToastUtils.show(this, "内容不能为空");
            return;
        }
        if (TextUtils.isEmpty(shareText)) {
            shareText = message;
        } else {
            shareText = shareText.replace("<用户留言>", message);
        }
        if (shareText.length() >= 140) {
            shareText = shareText.substring(0, 137);
        }
        if (tencentCheckBox.isChecked()) {
            if (bitmap != null) {
                tencentSharePic(bitmap, shareText);
            } else {
                tencentShareText(shareText);
            }
        }
        if (sinaCheckBox.isChecked()) {
            if (bitmap != null) {
                sinaSharePic(bitmap, shareText);
            } else {
                sinaShareText(shareText);
            }
        }
        new ProgressDialogTask<Response>(this){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createSubmitActivityTopicRequest(activityId, message, file)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                isPublish = true;
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(ScreenShotSubmitTopicActivity.this, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                isPublish = true;
            }
        }.start("正在发布话题");
        isPublish = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (file != null && file.exists()) {
            file.delete();
        }
        isPublish = true;
    }
}
