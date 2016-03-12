package com.shengzhish.xyj.activity.shaking;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.ActivityItem;
import com.shengzhish.xyj.activity.entity.Render;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.IConstant;

import roboguice.inject.InjectView;

/**
 * 摇一摇活动
 */
public class ShakeActivity extends DialogFragmentActivity {

    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.shke_icon)
    private View shakeIcon;
    private ShakeListener mShakeListener;
    private String id;
    private Render render;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_layout);
        AnimationUtil.verticalAnimation2(shakeIcon,true);
        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                startActivity(new Intent(ShakeActivity.this, CountTime.class).putExtra(IConstant.ACTIVITY_ID, id)
                        .putExtra(IConstant.RENDER,render));
            }
        });

        HttpboLis.getInstance().getHttp(this, ActivityDetails.class, String.format("activity/detail.json?id=%s", id), new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                ActivityItem activityItem = response.getActivity();
                if (activityItem != null /*&& dialogIsShowing*/) {
                    if (!TextUtils.isEmpty(activityItem.getRule())) {
                        final Dialog dialog = new Dialog(ShakeActivity.this, R.style.DialogAnimation);
                        dialog.setContentView(R.layout.activity_news_rule_dialog);
                        TextView textView = (TextView) dialog.findViewById(R.id.activity_rule_text);
                        View closeView = dialog.findViewById(R.id.close_activity_rule_dialog);
                        closeView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        textView.setText(activityItem.getRule() + "\n");
                        render= activityItem.getRender();

                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }
                }
            }
        });

    }


    @Override
    protected void onPause() {

        // TODO Auto-generated method stub
        super.onPause();
        mShakeListener.stop();
    }

    @Override
    protected void onResume() {

        // TODO Auto-generated method stub
        super.onResume();
        mShakeListener.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShakeListener.stop();
    }

}
