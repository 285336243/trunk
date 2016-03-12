package com.jiujie8.choice.persioncenter;

import android.os.Bundle;
import android.view.View;

import com.jiujie8.choice.R;
import com.jiujie8.choice.core.ThirdPartyShareActivity;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/12/4.
 * 登录
 */
@ContentView(R.layout.login)
public class LoginActivity extends ThirdPartyShareActivity {

    @InjectView(R.id.login_colse)
    private View mColseView;

    @InjectView(R.id.login_weixin)
    private View mWeixinView;

    @InjectView(R.id.login_sina)
    private View mSinaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mColseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mWeixinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weiXinLogin();
                finish();
            }
        });

        mSinaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSinaAuthorize(null);
            }
        });
    }
}
