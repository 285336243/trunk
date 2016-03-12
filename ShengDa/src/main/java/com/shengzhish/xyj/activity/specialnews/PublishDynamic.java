package com.shengzhish.xyj.activity.specialnews;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.activity.SpecialNewsActivity;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.http.MultipartRequest;

import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 发表动态
 */
@ContentView(R.layout.special_dynamic_layout)
public class PublishDynamic extends DialogFragmentActivity {
    @InjectView(R.id.back_imageview)
    private ImageView back_imageview;

    @InjectView(R.id.publish_button)
    private Button publishButton;
    @InjectView(R.id.publish_context)
    private EditText publishContent;

    @InjectView(R.id.placed_imageView1)
    private ImageView imageview;

    private final static String PUBLISH_URI = "activity/status.json";
    private String id;
    private File upImageFile;
    private Context context;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = PublishDynamic.this;
        Intent intentdta = this.getIntent().getParcelableExtra(IConstant.PHOTOALBUMDATA);
        setPicToView(intentdta);
        id = getIntent().getStringExtra(IConstant.SPECIALNEWS_ID);
        position = getIntent().getIntExtra(IConstant.HOST_POSITION, 0);
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String content = publishContent.getText().toString();
//                if (!TextUtils.isEmpty(content)) {
                    showCancelDialog();
          /*      } else {
                    finish();
                }*/
            }
        });
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtilSh.isLogin(context)) {
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    publishDtnamic();
                }
            }
        });

    }

    private void publishDtnamic() {
        final String content = publishContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show(this, "内容不能为空");
            return;
        }
        new ProgressDialogTask<Response>(this) {

            @Override
            protected Response run(Object data) {
                MultipartRequest<Response> request = new MultipartRequest<Response>(PUBLISH_URI);
                request.setClazz(Response.class);
                request.addMultipartStringEntity("id", id);
                request.addMultipartStringEntity("msg", content);
                if (upImageFile != null) {
                    request.addMultipartFileEntity("img", upImageFile);
                }
                return (Response) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        SpecialNewsActivity.instance.finish();
                        finish();
                        startActivity(new Intent(context, SpecialNewsActivity.class).
                                putExtra(IConstant.SPECIALNEWS_ID, id).putExtra(IConstant.HOST_POSITION, position));
//                        ToastUtils.show(PublishDynamic.this, "发布成功");
                    } else {
                        ToastUtils.show(PublishDynamic.this, response.getResponseMessage());
                    }
                }
            }
        }.start("正在发布");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upImageFile.exists()) {
            upImageFile.delete();

        }
        System.gc();
    }

    // 将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            upImageFile = saveBitmap(photo);
            imageview.setImageBitmap(photo);

            if (photo != null && !photo.isRecycled()) {
                photo.isRecycled();
                photo = null;
            }
        }
    }


    public File saveBitmap(Bitmap bitmap) {
        File jpgFile = null;
        // 判断SD卡是否可读写
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File fileFolder = new File(Environment.getExternalStorageDirectory() + "/ShengDa/");
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = format.format(new Date()) + ".jpg";
            jpgFile = new File(fileFolder, filename); // 创建文件对象

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(jpgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return jpgFile;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jpgFile;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            final String content = publishContent.getText().toString();
//            if (!TextUtils.isEmpty(content)) {
                showCancelDialog();
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.edit_continue_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }

}
