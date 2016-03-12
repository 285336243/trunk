package com.socialtv.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by sunjian on 13-12-26.
 */
public interface OAuthThirdParty {

    public static final String SINA = "sina";
    public static final String TOKEN = "token";
    public static final String EXPIRED_TIME = "expiredTime";
    public static final String UID = "uid";

    public void authorize(AuthorizeListener authorizeListener);

    public void onCreate(Bundle bundle);

    public void onNewIntent(Intent intent);

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void shareText(String text);

    public void sharePic(ImageView imageView, String text);

    public void sharePic(Bitmap bitmap, String text);

    public void sharePage(String text, ImageView imageView, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText);

    public void sharePage(String text, Bitmap bitmap, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText);

}
