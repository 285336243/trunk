package com.shengzhish.xyj.util;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context context, String text) {
        mHandler.removeCallbacks(mRunnable);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
        mHandler.postDelayed(mRunnable, 1000);
        mToast.show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    public static void showCenterToast(Context context, String text) {
        mHandler.removeCallbacks(mRunnable);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mHandler.postDelayed(mRunnable, 1000);
        mToast.show();
    }
}
