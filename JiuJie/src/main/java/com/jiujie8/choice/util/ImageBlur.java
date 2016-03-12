package com.jiujie8.choice.util;

import android.graphics.Bitmap;

/**
 * Created by wlanjie on 14/11/26.
 */
public class ImageBlur {

    private static native void blurIntArray(int[] pImg, int w, int h, int r);

    private static native void blurBitmap(Bitmap bitmap, int r);

    static {
        System.loadLibrary("ImageBlur");
    }

    public static Bitmap doBlurJniBitmap(Bitmap bitmap, int radius, boolean canReuseInBitmap) {
        Bitmap mBitmap;
        if (canReuseInBitmap) {
            mBitmap = bitmap;
        } else {
            mBitmap = bitmap.copy(bitmap.getConfig(), true);
        }
        if (radius < 1) {
            return bitmap;
        }
        blurBitmap(mBitmap, radius);
        return mBitmap;
    }

    public static Bitmap doBlurJniArray(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        //Jni 数组计算
        blurIntArray(pix, w, h, radius);

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
