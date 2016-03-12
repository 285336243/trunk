package com.example.xiadan.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by 51wanh on 2015/3/6.
 */
public class ToastLocationUtil {
    private static Toast mToast;

    /**
     * 显示Toast消息
     *
     * @param msg 提示消息
     */
    public static void showToast(Context context,String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,0,0);
        mToast.show();
    }
}
