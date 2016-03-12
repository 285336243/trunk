package feipai.qiangdan.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.Utils;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends DialogFragmentActivity {


    @InjectView(R.id.bgd_layout)
    private View bdgLayout;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome);
        Utils.isPushStart = false;
//        bdgLayout.setBackgroundResource(R.drawable.ic_launcher);
        context = WelcomeActivity.this;
//        getPicture();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (null != SaveSettingUtil.getUserSid(context)) {
                    intent = new Intent(context, HomeActivity.class);
                } else {
                    intent = new Intent(context, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);

        //启动百度推送，要加载启动类中
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
        //启动动画
//        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
//        animation.setDuration(1000);
//        bdgLayout.startAnimation(animation);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.isPushStart = true;
    }
}
