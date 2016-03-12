package com.example.xiadan.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.example.xiadan.MainActivity;
import com.example.xiadan.R;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.order.SuccesstActivity;
import com.example.xiadan.util.SaveSettingUtil;


public class WelcomeActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_layout);
        context = WelcomeActivity.this;
        //启动动画
        View bdgLayout=findViewById(R.id.root_layout);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1000);
        bdgLayout.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
//                if (null != SaveSettingUtil.getUserSid(context)) {
                    intent = new Intent(context, MainActivity.class);
//                    intent = new Intent(context, SuccesstActivity.class);
               /* } else {
                    intent = new Intent(context, LoginActivity.class);
                }*/
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        }, 1500);
    }


}
