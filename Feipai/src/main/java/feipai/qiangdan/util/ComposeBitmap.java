package feipai.qiangdan.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import feipai.qiangdan.R;


/**
 * Created by 51wanh on 2015/4/29.
 */
public class ComposeBitmap {

    public static Bitmap imageCompose(Bitmap aboveBitmap, String orderNumber, String emploee, String address, String location) {
        FileOutputStream stream = null;
        String imageComposePath = null;

        //创建画布
        Bitmap basebitmap = Bitmap.createBitmap(500, 700, Config.ARGB_8888);
//            Bitmap.createBitmap()

        // Bitmap basebitmap =
        // Bitmap.createBitmap(loadedImage.getWidth(),
        // loadedImage.getHeight(), loadedImage.getConfig());
        // Bitmap b = Bitmap.createScaledBitmap(shareBitmap,
        // (int)(480 / density), (int) (360/density), true);
        // Bitmap b = Bitmap.createScaledBitmap(shareBitmap,
        // 480, 360, true);
        //创建上面的图片
        Bitmap b = Bitmap.createScaledBitmap(aboveBitmap, 500, 550, true);

        Canvas canvas = new Canvas(basebitmap);
        canvas.drawColor(Color.parseColor("#FF69B4"));
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        // canvas.drawBitmap(loadedImage, 0, 0, paint);
        // canvas.drawBitmap(b, (int)5 / density, (int)5 /
        // density, paint);
        //将图片画入画布上半部分
        canvas.drawBitmap(b, 0, 0, paint);

        /**
         * 验证图片
         */
        // loadedImage=Bitmap.createScaledBitmap(loadedImage,
        // 490, 95, true);

//        canvas.drawBitmap(loadedImage, 0, 370, paint);
        paint.setColor(Color.BLUE);
        paint.setTextSize(20);
        //在画布上写字
        //订单编号
        canvas.drawText(orderNumber, 5, 575, paint);
        //飞派员
        canvas.drawText(emploee, 5, 595, paint);
        //应送地址
        canvas.drawText(address, 5, 615, paint);


        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String photoTime = "拍照时间：" + format.format(date);
        //拍照时间
        canvas.drawText(photoTime, 5, 635, paint);

        if (null != location) {
            //我的位置
            canvas.drawText("检测地址："+location, 5, 655, paint);
        }
        // imageFile = new
        // File(Environment.getExternalStorageDirectory(),
        // "/socialTvImages/" + System.currentTimeMillis());

        if (b != null && !b.isRecycled()) {
            b.isRecycled();
            b = null;
        }
        return basebitmap;
    }
}
