package com.mzs.guaji.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;

/**
 * 提示对话框工具类
 * @author lenovo
 *
 */
public class TipsUtil {

	private static PopupWindow mPopupWindow;
    private static Dialog mDialog;

	/**
	 * 上传提示poopupwindow
	 */
	public static void showPopupWindow(Context context, View mRootLayout, int resId) {
		View v = View.inflate(context, R.layout.image_pop, null);
		mPopupWindow = new PopupWindow(v, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		TextView mDialogMessageText = (TextView) v.findViewById(R.id.dialog_message_text);
		mDialogMessageText.setText(resId);
		mPopupWindow.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
	}

    public static void showTipDialog(Context context, int resId) {
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.image_pop);
        TextView mDialogMessageText = (TextView) mDialog.findViewById(R.id.dialog_message_text);
        mDialogMessageText.setText(resId);
        if(mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public static void showTipDialog(Context context, String text) {
        mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.image_pop);
        TextView mDialogMessageText = (TextView) mDialog.findViewById(R.id.dialog_message_text);
        mDialogMessageText.setText(text);
        if(mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }


    public static void dismissDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

	/**
	 * 关闭上传popupwindow
	 */
	public static void dismissPopupWindow() {
		if(mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
}
