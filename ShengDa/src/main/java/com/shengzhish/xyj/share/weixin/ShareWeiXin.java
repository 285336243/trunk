package com.shengzhish.xyj.share.weixin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.tencent.mm.sdk.openapi.IWXAPI;
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

    private static final String WEIXID_APP_ID = "wx92efbb3680cdbcd1";
    private static final int THUMB_SIZE = 150;
    private IWXAPI mWXAPI;

    public ShareWeiXin(Context context) {
        mWXAPI = WXAPIFactory.createWXAPI(context, WEIXID_APP_ID);
        mWXAPI.registerApp(WEIXID_APP_ID);
    }

    public void shareWeiXinText(String text) {
        if (text == null || text.length() == 0) {
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

    public void shareWeiXinPic(Bitmap bitmap) {
        WXImageObject mImageObj = new WXImageObject(bitmap);
        WXMediaMessage mMediaMessage = new WXMediaMessage();
        mMediaMessage.mediaObject = mImageObj;
        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//        bitmap.recycle();
        mMediaMessage.thumbData = bmpToByteArray(mBitmap, true);
//        mBitmap.recycle();
        SendMessageToWX.Req mReq = new SendMessageToWX.Req();
        mReq.transaction = String.valueOf(System.currentTimeMillis());
        mReq.message = mMediaMessage;
        mReq.scene = SendMessageToWX.Req.WXSceneTimeline;
        mWXAPI.sendReq(mReq);
    }

    public void shareWeiXinWebPge(Bitmap bitmap,String url,String title,String description){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = bmpToByteArray(bitmap, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction =String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        mWXAPI.sendReq(req);
    }


    public void shareWeiXinPic(ImageView imageView, String text) {
        if (imageView != null) {
            Drawable mDrawable = imageView.getDrawable();
            if (mDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
                if (bitmapDrawable != null) {
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

    public void unRegisterApp() {
        mWXAPI.unregisterApp();
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
