package com.example.xiadan.order;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.xiadan.R;
import com.example.xiadan.bean.OrderSucces;
import com.example.xiadan.menucontent.ProgressWebView;
import com.example.xiadan.util.DialogUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.whole.AppManager;
import com.google.gson.Gson;

/**
 * Created by 51wanh on 2015/3/2.
 */
public class SuccesstActivity extends SherlockFragmentActivity {
    private static final String WEB_URL = IConstant.DOMAIN + "Recharge/alipayapinet?id=%s";
    //显示结果
    private View mShowView;
    //显示支付页面
    private ProgressWebView payWeb;
    private int isredirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sucess);
        ActionBar actionBar = getSupportActionBar();

        //设置可点击
        actionBar.setDisplayHomeAsUpEnabled(true);
        AppManager.getAppManager().addActivity(this);
        String orderResult = getIntent().getStringExtra(IConstant.ORDER_SUCCESS);
        Gson gson = new Gson();
        final OrderSucces data = gson.fromJson(orderResult, OrderSucces.class);
        mShowView = findViewById(R.id.success_text);
        payWeb = (ProgressWebView) findViewById(R.id.pay_webview);
        isredirect = data.getIsredirect();
        if (isredirect == 0) {
            actionBar.setTitle("完成");
            mShowView.setVisibility(View.VISIBLE);
            payWeb.setVisibility(View.GONE);
            findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            actionBar.setTitle("支付");
            mShowView.setVisibility(View.GONE);
            payWeb.setVisibility(View.VISIBLE);
            //设置WebView属性，能够执行Javascript脚本
            payWeb.getSettings().setJavaScriptEnabled(true);
            payWeb.loadUrl(String.format(WEB_URL, data.getId()));
//            payWeb.loadUrl("http://www.baidu.com/");
            //设置Web视图
            payWeb.setWebViewClient(new HelloWebViewClient());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isredirect == 0) {
                    finish();
                } else {
                    showCancelDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isredirect == 0) {
                finish();
            } else {
                showCancelDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void showCancelDialog() {
        DialogUtil.getInstance().showCancelDialog(this, "确定关闭支付页面？").setLisener(new DialogUtil.CallBack() {
            @Override
            public void setConfirm() {
                finish();
            }
        });
    }
}
