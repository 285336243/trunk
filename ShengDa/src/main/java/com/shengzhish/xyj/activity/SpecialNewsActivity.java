package com.shengzhish.xyj.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.specialnews.PublishDynamic;
import com.shengzhish.xyj.activity.specialnews.SeleterPictureDialog;
import com.shengzhish.xyj.activity.specialnews.SpecialFragment;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.IConstant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.special_news_layout)
public class SpecialNewsActivity extends DialogFragmentActivity implements SpecialFragment.MyListener {

    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.taken_photo_imagebutton)
    private ImageButton taken_photo;

//    private boolean dialogIsShowing = true;

    public static SpecialNewsActivity instance = null;

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    SeleterPictureDialog dialog;
    private String id;
    private final static String ACTIVITY_RULE = "activity/detail.json?id=%s";
    private int index;

    @Override
    public void showPosition(int index) {
//        Log.v("person", "index ===" + index);
        this.index = index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        id = getIntent().getStringExtra(IConstant.SPECIALNEWS_ID);
        index = getIntent().getIntExtra(IConstant.HOST_POSITION, 0);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SpecialFragment fragment = SpecialFragment.newInstance(index);
        ft.replace(R.id.container, fragment);
        ft.commit();

        dialog = new SeleterPictureDialog(SpecialNewsActivity.this, new SeleterPictureDialog.DialogActListener() {
            @Override
            public void takeCamera() {
                // 调用系统的拍照功能
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            }

            @Override
            public void takeAlbum() {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        taken_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing())
                    dialog.show();

            }
        });

        HttpboLis.getInstance().getHttp(this, ActivityDetails.class, String.format(ACTIVITY_RULE, id), new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                if (response.getActivity() != null /*&& dialogIsShowing*/) {
                    if (!TextUtils.isEmpty(response.getActivity().getRule())) {
                        final Dialog dialog = new Dialog(SpecialNewsActivity.this, R.style.DialogAnimation);
                        dialog.setContentView(R.layout.activity_news_rule_dialog);
                        final TextView textView = (TextView) dialog.findViewById(R.id.activity_rule_text);
                        View closeView = dialog.findViewById(R.id.close_activity_rule_dialog);
                        closeView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        textView.setText(response.getActivity().getRule() + "\n");
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                startPhotoZoom(Uri.fromFile(tempFile), 150);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    // Uri uri = data.getData();
                    Intent pictureintent = new Intent(this, PublishDynamic.class);
                    pictureintent.putExtra(IConstant.PHOTOALBUMDATA, data);
                    pictureintent.putExtra(IConstant.SPECIALNEWS_ID, id);
                    pictureintent.putExtra(IConstant.HOST_POSITION, index);
                    startActivity(pictureintent);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 19);
        intent.putExtra("aspectY", 20);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (tempFile.exists()) {
            tempFile.delete();

        }

    }
}
