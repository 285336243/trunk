package com.shengzhish.xyj.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 位图工具
 */
public class BitmapUtil {
    public static long getBitmapsize(Bitmap bitmap) {
        //在Android API（12）之前的版本和后来的版本是不一样：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    /**
     * @param bgimage
     * @param size
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, int size) {
// 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
// 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
// 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) size) / width;
        float scaleHeight = ((float) size) / height;
        float scaleSize = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
// 缩放图片动作
        matrix.postScale(scaleSize, scaleSize);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
                matrix, true);
        return bitmap;
    }

    /**
     * 按输入尺寸压缩
     *
     * @param bgimage 原图片
     * @param size    指定图片尺寸
     * @return 缩放后的位图
     */
    public static Bitmap yaImage(Bitmap bgimage, int size) {
        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) size) / width;
        float scaleHeight = ((float) size) / height;
        //取缩放率小的
        float radio = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
        //生成缩略图
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bgimage, (int) (width * radio), (int) (height * radio));
        return bitmap;
    }
}
