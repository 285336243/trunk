package com.socialtv.personcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.socialtv.R;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.util.IConstant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/10/28.
 * 重置密码成功的页面
 */
@ContentView(R.layout.forget_password_result)
public class ForgetPasswordResultActivity extends DialogFragmentActivity {

    @InjectView(R.id.forget_password_result)
    private TextView resultMessageText;

    @InjectView(R.id.forget_password_i_know)
    private View iKnowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("重置密码");

        final String message = getStringExtra(IConstant.MESSAGE);
        resultMessageText.setText(message);

        iKnowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
