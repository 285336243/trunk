package com.shengzhish.xyj.activity.shaking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.Render;
import com.shengzhish.xyj.activity.entity.ShakeResponse;
import com.shengzhish.xyj.activity.entity.Showseq;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.core.Log;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ScreenUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import roboguice.inject.InjectView;

/**
 * 摇一摇计时
 */
public class CountTime extends DialogFragmentActivity {

    @InjectView(R.id.show_time_bkg)
    private ImageView showTimeBkg;
    @InjectView(R.id.count_time_root_layout)
    private RelativeLayout rootLayout;
    @InjectView(R.id.count_time_textview)
    private TextView conutTime;

    SubCountDownTime sub;
    private String id;


    int[] bkgColors = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6};
    private boolean run;
    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            if (run) {
                handler.postDelayed(this, 1 * 1000);
            }
       /*     int i = (int) (Math.random() * bkgColors.length);
            rootLayout.setBackgroundColor(getResources().getColor(bkgColors[i]));*/
            Date date = new Date();
            String sysTime = String.format("%tH:%tM:%tS", date, date, date);
            String currenSecond = sysTime.substring(6);
//            Log.v("person", "systim == " + sysTime);
//            Log.v("person", "currenSecond == " + currenSecond);
            setBackGround(Integer.valueOf(currenSecond));
        }
    };
    private List<Showseq> ShowseqList;
    private int currentColorIndex = Integer.MAX_VALUE;

    public void setBackGround(int second) {
        for (int i = 0; i < ShowseqList.size(); i++) {
            if (ShowseqList.get(i).compare(second)) {
                if (currentColorIndex == i) {
                    return;
                }
//                Log.v("person", "i == " + i);
                com.shengzhish.xyj.activity.entity.Color color = ShowseqList.get(i).getColor();
                rootLayout.setBackgroundColor(Color.rgb(color.getR(), color.getG(), color.getB()));
                currentColorIndex = i;
                break;
            }
        }
    }

    private Bundle bundle;
    private Render render;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shake_count_time_layout);
        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);
        render = (Render) getIntent().getSerializableExtra(IConstant.RENDER);
        bundle = new Bundle();
        String url = String.format("activity/shake.json?id=%s", id);
        HttpboLis.getInstance().getHttp(this, ShakeResponse.class, url, new HttpboLis.OnCompleteListener<ShakeResponse>() {
            @Override
            public void onComplete(ShakeResponse response) {
//                int isHit = response.getIsHit();
//                String congratulation = response.getCongratulation();
                bundle.putInt(IConstant.ISHIT, response.getIsHit());
                bundle.putString(IConstant.CONGRATULATION, response.getCongratulation());

            }
        });
        ShowseqList = render.getShowseq();
        sub = new SubCountDownTime(render.getDuration() * 1000, 1000);
        sub.start();

    }

    @Override
    protected void onPause() {

        // TODO Auto-generated method stub
        super.onPause();
        run = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sub.cancel();
    }

    @Override
    protected void onResume() {

        // TODO Auto-generated method stub
        super.onResume();
        run = true;
        handler.post(myRunnable);
    }

    private class SubCountDownTime extends CountDownTimer {
        public SubCountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        @Override
        public void onTick(long millisUntilFinished) {
    /*        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");// 初始化Formatter的转换格式。
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            String hms = formatter.format(millisUntilFinished);
            conutTime.setText(hms);*/
            conutTime.setText(String.valueOf(millisUntilFinished / 1000));
        }

        /**
         * Callback fired when the time is up.
         */
        @Override
        public void onFinish() {
            startActivity(new Intent(CountTime.this, ShakeRezult.class).putExtras(bundle));
            finish();
        }
    }
}
