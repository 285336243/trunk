package feipai.qiangdan.order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import feipai.qiangdan.util.ComposeBitmap;
import feipai.qiangdan.util.ImageUtils;
import feipai.qiangdan.util.RotateBitmap;
import feipai.qiangdan.util.StorageUtil;

/**
 * 处理图片的activity
 */
public class PictrueActivity extends SetUserHeadPhoto {

    private Bitmap bitmap;
    private Bitmap blankBitmap;
    protected String orderNumber;
    protected String address;
    protected String emploee;
    protected String mylocation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            cancelDialog();
            switchMethod(requestCode, data);
        }
    }

    private void switchMethod(int requestCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                setPicture(Uri.fromFile(saveFile), true);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    setPicture(data.getData(), false);
                break;
            default:
                break;
        }
    }

    /**
     * 设置图片
     *
     * @param uri        图片数据
     * @param isRotation 是否旋转 true为需要旋转
     */
    private void setPicture(Uri uri, boolean isRotation) {
        // TODO Auto-generated method stub
        try {
            // 从ContentResolver中获取到Uri的输入流
            InputStream is = getContentResolver().openInputStream(uri);
            WindowManager wm = getWindowManager();
            // 得到屏幕的宽和高
            int windowWidth = wm.getDefaultDisplay().getWidth();
            int windowHeight = wm.getDefaultDisplay().getHeight();

            // 实例化一个Options对象
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 指定它只读取图片的信息而不加载整个图片
            opts.inJustDecodeBounds = true;
            // 通过这个Options对象，从输入流中读取图片的信息
            BitmapFactory.decodeStream(is, null, opts);

            // 得到Uri地址的图片的宽和高
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;
            // 分析图片的宽高比，用于进行优化
            if (bitmapHeight > windowHeight || bitmapWidth > windowWidth) {
                int scaleX = bitmapWidth / windowWidth;
                int scaleY = bitmapHeight / windowHeight;
                if (scaleX > scaleY) {
                    opts.inSampleSize = scaleX;
                } else {
                    opts.inSampleSize = scaleY;
                }
            } else {
                opts.inSampleSize = 1;
            }

            // 设定读取完整的图片信息
            opts.inJustDecodeBounds = false;
            is = getContentResolver().openInputStream(uri);

            // 如果没有被系统回收，就强制回收它

            if (blankBitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();// 回收上次加载的图片
            }

            bitmap = BitmapFactory.decodeStream(is, null, opts);

            // 如果没有被系统回收，就强制回收它
            if (blankBitmap != null && !blankBitmap.isRecycled()) {
                blankBitmap.recycle();// 回收上次加载的图片
            }
            Bitmap correctBitmap = null;
            if (isRotation) {
                //拍照后图片会反转90度，需要把纠正图片
                correctBitmap = RotateBitmap.rotaingImageView(90, bitmap);
            } else {
                //相册选得照片正常，不需要纠正角度
                correctBitmap = bitmap;
            }
            Bitmap composeBitmap = ComposeBitmap.imageCompose(correctBitmap, orderNumber, emploee, address, mylocation);
            mAvatarImage.setImageBitmap(composeBitmap);
            saveAndshow(composeBitmap);

        } catch (Exception e) {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveAndshow(Bitmap bitmap) {
        try {
            saveFile = File.createTempFile("photoview", ".jpg",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            FileOutputStream out = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(this, "Error occured while extracting bitmap", Toast.LENGTH_SHORT).show();
        }
//        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mAvatarImage, ImageUtils.imageLoader(this, 0));
    }

    private void savephoto(Bitmap bitmap) {
        FileOutputStream outputStream = null;
        try {
            saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis() + ".jpg");
            outputStream = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mAvatarImage, ImageUtils.imageLoader(this, 0));
    }
}
