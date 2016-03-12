package com.shengzhish.xyj.activity.specialnews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.ThirdPartyShareActivity;
import com.shengzhish.xyj.gallery.entity.Pic;
import com.shengzhish.xyj.util.BitmapUtil;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.util.ScreenUtil;

import java.io.File;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;


/**
 * 分享activity
 */

public class ShareActivity extends ThirdPartyShareActivity {

    @InjectView(R.id.share_sina)
    private View shareSina;

    @InjectView(R.id.share_layout)
    private View shareLayout;

    @InjectView(R.id.share_tencent)
    private View shareTencent;

    @InjectView(R.id.share_weixin)
    private View shareWeiXin;

    private String imgUrl, nikeName;
    private Context context;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_layout);
        if (Build.VERSION.SDK_INT >= 11) {
            setFinishOnTouchOutside(true);
        }
        int heighted = ScreenUtil.getViewHeight(shareLayout);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //设置窗口的大小及透明度
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = heighted;
        layoutParams.gravity = Gravity.BOTTOM;
//        layoutParams.alpha = 0.5f;
        window.setAttributes(layoutParams);


        context = ShareActivity.this;
        imgUrl = getIntent().getStringExtra(IConstant.SHARE_IMG_URL);
        nikeName = getIntent().getStringExtra(IConstant.SHARE_NIKENAME);
        shareSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog("SINA");

            }
        });
        shareTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog("TENCENT");

            }
        });
        shareWeiXin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().displayImage(imgUrl, new ImageView(context), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Bitmap bitmap = BitmapUtil.yaImage(loadedImage, 100);
                        shareWeiXinWebPge(bitmap, imgUrl, "分享了@" + nikeName + "的 戏游纪，快来看一下>>>", "分享了@" + nikeName + "的 戏游纪，快来看一下>>>");
                    }
                });
                finish();
            }
        });

    }

    private void shareDialog(final String type) {
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.special_share_dialog_layout);
        ImageLoader.getInstance().displayImage(imgUrl, (ImageView) dialog.findViewById(R.id.placed_imageView1),
                ImageUtils.imageLoader(context, 4));

        final EditText shareEditText = (EditText) dialog.findViewById(R.id.share_context);
        shareEditText.setText("分享了@" + nikeName + "的 戏游纪，快来看一下>>>");
        final String content = shareEditText.getText().toString().trim();
        dialog.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("SINA".equals(type)) {
                    ImageLoader.getInstance().displayImage(imgUrl, new ImageView(context), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            sinaSharePic(loadedImage, content);
//                            if (dialog != null && dialog.isShowing())
//                                dialog.dismiss();
//                            finish();
                        }
                    });

                } else {
                    RoboAsyncTask<Void> task = new RoboAsyncTask<Void>(context) {
                        @Override
                        public Void call() throws Exception {
                            File file = ImageLoader.getInstance().loadImageFileSync(imgUrl);
                            qqSharePic(file, content);
                    /*        try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
//                            if (dialog != null && dialog.isShowing())
//                                dialog.dismiss();
//                            finish();
                            return null;
                        }
                    };
                    task.execute();

                }
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.back_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
