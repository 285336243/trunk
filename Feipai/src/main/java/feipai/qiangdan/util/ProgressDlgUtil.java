package feipai.qiangdan.util;

import android.app.ProgressDialog;
import android.content.Context;

import feipai.qiangdan.R;

/**
 * Created by 51wanh on 2015/1/19.
 */
public class ProgressDlgUtil {
    static ProgressDialog progressDlg = null;

    /**
     * 启动进度条
     *
     * @param strMessage 进度条显示的信息
     * @param ctx   当前的activity
     */
    public static void showProgressDlg(Context ctx, String strMessage) {

        if (null == progressDlg) {
            progressDlg = new ProgressDialog(ctx);
            //设置进度条样式
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置进度条标题
            progressDlg.setTitle(ctx.getApplicationContext().getString(
                    R.string.app_name));
            //提示的消息
            progressDlg.setMessage(strMessage);
            progressDlg.setIndeterminate(false);
            progressDlg.setCancelable(false);
            progressDlg.setIcon(R.drawable.icon);
            progressDlg.show();
        }
    }

    /**
     * 结束进度条
     */
    public static void stopProgressDlg() {
        if (null != progressDlg) {
            progressDlg.dismiss();
            progressDlg = null;
        }
    }
}
