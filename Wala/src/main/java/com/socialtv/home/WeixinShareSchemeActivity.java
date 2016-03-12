package com.socialtv.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.core.ThirdPartyShareActivity;

/**
 * Created by wlanjie on 14-9-25.
 */
public class WeixinShareSchemeActivity extends ThirdPartyShareActivity {

    private String link;
    private String imgUrl;
    private String title;
    private String desc;
    private String host;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    host = uri.getHost();
                    link = uri.getQueryParameter("link");
                    imgUrl = uri.getQueryParameter("imgUrl");
                    title = uri.getQueryParameter("title");
                    desc = uri.getQueryParameter("desc");

                    final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    if ("weixin.shareToTimeline".equals(host)) {
                        if (TextUtils.isEmpty(imgUrl)) {
                            shareWeiXinWebPage(link, title, desc, bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(imgUrl, new SimpleImageLoadingListener(){
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    super.onLoadingFailed(imageUri, view, failReason);
                                    shareWeiXinWebPage(link, title, desc, bitmap);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    shareWeiXinWebPage(link, title, desc, loadedImage);
                                }
                            });
                        }
                    } else {
                        if (TextUtils.isEmpty(imgUrl)) {
                            shareWeiXinFriendWebPage(link, title, desc, bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(imgUrl, new SimpleImageLoadingListener(){
                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    super.onLoadingFailed(imageUri, view, failReason);
                                    shareWeiXinFriendWebPage(link, title, desc, bitmap);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    shareWeiXinFriendWebPage(link, title, desc, loadedImage);
                                }
                            });
                        }
                    }
                    finish();
                }
            }
        }
    }
}
