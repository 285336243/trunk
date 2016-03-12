package com.jiujie8.choice.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by wlanjie on 14-1-22.
 */
public class ShareWeiXin {

    private static final String WEIXID_APP_ID = "wx26eaee4a1208e2ab";

    private static final int THUMB_SIZE = 96;

    private IWXAPI mWXAPI;

    @Inject
    public ShareWeiXin(Context context) {
        mWXAPI = WXAPIFactory.createWXAPI(context, WEIXID_APP_ID);
        mWXAPI.registerApp(WEIXID_APP_ID);
    }

    public void weiXinLogin() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_choice";
        mWXAPI.sendReq(req);
    }

    public void shareWeiXinCircleText(String text) {
            if(text == null || text.length() == 0) {
                return;
            }
            WXTextObject mTextObj = new WXTextObject();
            mTextObj.text = text;
            WXMediaMessage mMediaMessage = new WXMediaMessage();
            mMediaMessage.mediaObject = mTextObj;
            mMediaMessage.description = text;
            SendMessageToWX.Req mReq = new SendMessageToWX.Req();
            mReq.transaction = String.valueOf(System.currentTimeMillis());
            mReq.message = mMediaMessage;
            mReq.scene = SendMessageToWX.Req.WXSceneTimeline;
            mWXAPI.sendReq(mReq);
    }

    public void shareWeiXinText(String text) {
        if(text == null || text.length() == 0) {
            return;
        }
        WXTextObject mTextObj = new WXTextObject();
        mTextObj.text = text;
        WXMediaMessage mMediaMessage = new WXMediaMessage();
        mMediaMessage.mediaObject = mTextObj;
        mMediaMessage.description = text;
        SendMessageToWX.Req mReq = new SendMessageToWX.Req();
        mReq.transaction = String.valueOf(System.currentTimeMillis());
        mReq.message = mMediaMessage;
        mReq.scene = SendMessageToWX.Req.WXSceneSession;
        mWXAPI.sendReq(mReq);
    }

    public void shareWeiXinPic(Bitmap bitmap) {
            WXImageObject mImageObj = new WXImageObject(bitmap);
            WXMediaMessage mMediaMessage = new WXMediaMessage();
            mMediaMessage.mediaObject = mImageObj;
            Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            mMediaMessage.thumbData = bmpToByteArray(mBitmap, true);
            SendMessageToWX.Req mReq = new SendMessageToWX.Req();
            mReq.transaction = String.valueOf(System.currentTimeMillis());
            mReq.message = mMediaMessage;
            mReq.scene = SendMessageToWX.Req.WXSceneTimeline;
            mWXAPI.sendReq(mReq);
    }

    public void shareWeiXinPic(ImageView imageView, String text) {
            if(imageView != null) {
                Drawable mDrawable = imageView.getDrawable();
                if(mDrawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
                    if(bitmapDrawable != null) {
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        WXImageObject mImageObj = new WXImageObject(bitmap);
                        WXMediaMessage mMediaMessage = new WXMediaMessage();
                        mMediaMessage.mediaObject = mImageObj;

                        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                        mMediaMessage.thumbData = bmpToByteArray(mBitmap, true);
                        SendMessageToWX.Req mReq = new SendMessageToWX.Req();
                        mReq.transaction = String.valueOf(System.currentTimeMillis());
                        mReq.message = mMediaMessage;
                        mReq.scene = SendMessageToWX.Req.WXSceneTimeline;
                        mWXAPI.sendReq(mReq);
        //                bitmap.recycle();
                    }
                }
            }
    }

    public void shareWeiXinWebPage(final String pageUrl, final String title, final String description, final Bitmap bitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = pageUrl;
        WXMediaMessage message = new WXMediaMessage(webpage);
        message.title = title;
        message.description = description;
        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        message.thumbData = bmpToByteArray(mBitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        mWXAPI.sendReq(req);
    }

    public void shareWeiXinFriendWebPage(final String pageUrl, final String title, final String description, final Bitmap bitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = pageUrl;
        WXMediaMessage message = new WXMediaMessage(webpage);
        message.title = title;
        message.description = description;
        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        message.thumbData = bmpToByteArray(mBitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        mWXAPI.sendReq(req);
    }

    public void unRegisterApp() {
        mWXAPI.unregisterApp();
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 70, output);
//        if (needRecycle) {
//            bmp.recycle();
//        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
