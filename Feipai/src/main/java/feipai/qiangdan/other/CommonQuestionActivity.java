package feipai.qiangdan.other;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.MenuItem;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import roboguice.inject.InjectView;

/**
 * 参考手册
 */
//@ContentView(R.layout.layout_common_question)
public class CommonQuestionActivity extends DialogFragmentActivity {
    @InjectView(R.id.return_back)
    private View returnBack;
    @InjectView(R.id.webview)
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_question);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    private void init() {
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView加载web资源
//        webView.loadUrl("http://baidu.com");
        //飞派备用地址
        webView.loadUrl("http://www.ifeipai.com/api/v1/req");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

}
