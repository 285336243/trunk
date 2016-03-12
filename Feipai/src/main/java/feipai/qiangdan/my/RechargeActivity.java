package feipai.qiangdan.my;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressWebView;
import feipai.qiangdan.util.SaveSettingUtil;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/3/13.
 */
public class RechargeActivity extends DialogFragmentActivity {
    private static final String WEB_URL = IConstant.DOMAIN + "Recharge/writeMoney?sid=%s";
    @InjectView(R.id.pay_webview)
    private ProgressWebView payWeb;
    /**
     * 返回按钮
     */
    @InjectView(R.id.about_back)
    private View backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_recharge);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelDialog();
            }
        });
        //设置WebView属性，能够执行Javascript脚本
        payWeb.getSettings().setJavaScriptEnabled(true);

        payWeb.loadUrl(String.format(WEB_URL, SaveSettingUtil.getUserSid(this)));
//            payWeb.loadUrl("http://www.baidu.com/");
        //设置Web视图
        payWeb.setWebViewClient(new HelloWebViewClient());
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("确定关闭支付页面？");
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        TextView contiText = (TextView) dialog.findViewById(R.id.edit_continue_view);
        contiText.setText("继续支付");
        contiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }
}
