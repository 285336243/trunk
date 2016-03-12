package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.QuestionHallRankAdapter;
import com.mzs.guaji.entity.QuestionHall;
import com.mzs.guaji.util.ToastUtil;

/**
 * Created by wlanjie on 13-12-23.
 * 扫一扫排名的UI
 */
public class ScanRankingActivity extends GuaJiActivity implements RadioGroup.OnCheckedChangeListener{

    private Context context = ScanRankingActivity.this;
    private Button mStartButton;

    private ImageView timekey1;
    private ImageView timekey2;
    private ImageView timekey3;
    private ImageView timekey4;
    private ImageView timekey5;

    private TextView additionalTimekey;

    private TextView hallCoins;

    private String code;

    private ListView rankList;

    private RadioGroup questionHallRadioGroup;

    private QuestionHallRankAdapter adapter;

    private LinearLayout scanRule;

    private LinearLayout scanBack;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.scan_ranking_layout);
        code = getIntent().getStringExtra("code");
        timekey1 = (ImageView)findViewById(R.id.timekey1);
        timekey2 = (ImageView)findViewById(R.id.timekey2);
        timekey3 = (ImageView)findViewById(R.id.timekey3);
        timekey4 = (ImageView)findViewById(R.id.timekey4);
        timekey5 = (ImageView)findViewById(R.id.timekey5);
        additionalTimekey = (TextView) findViewById(R.id.additionalTimekey);
        hallCoins = (TextView)findViewById(R.id.hall_coins);
        scanBack = (LinearLayout)findViewById(R.id.scan_back);
        scanBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanRankingActivity.this.finish();
            }
        });
        mStartButton = (Button) findViewById(R.id.scan_ranking_start);
        mStartButton.setOnClickListener(mStartClickListener);

        questionHallRadioGroup =  (RadioGroup)findViewById(R.id.question_hall_radio_group);
        questionHallRadioGroup.setOnCheckedChangeListener(this);
        rankList = (ListView) findViewById(R.id.rank_list);
        scanRule = (LinearLayout)findViewById(R.id.scan_rule);
        scanRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("title", "规则");
                intent.putExtra("url", DOMAIN+"question/rule.html");
                context.startActivity(intent);
            }
        });
        code = getIntent().getStringExtra("code");
    }

    /**
     * 开始按钮点击事件
     */
    View.OnClickListener mStartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, AnswerActivity.class);
            mIntent.putExtra("code", code);
            startActivity(mIntent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mApi.requestGetData(DOMAIN +"question/hall.json", QuestionHall.class, new Response.Listener<QuestionHall>() {
            @Override
            public void onResponse(QuestionHall response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        timekey5.setVisibility(View.GONE);
                        timekey4.setVisibility(View.GONE);
                        timekey3.setVisibility(View.GONE);
                        timekey2.setVisibility(View.GONE);
                        timekey1.setVisibility(View.GONE);
                        hallCoins.setText(""+response.getCoins());
                        if(response.getRetainAccessTimes() > 0){
                            int accessTimes = response.getRetainAccessTimes();
                            if(accessTimes > 5){
                                additionalTimekey.setText("+"+(accessTimes-5));
                                additionalTimekey.setVisibility(View.VISIBLE);
                            }else{
                            	additionalTimekey.setText("0");
                            }
                            switch (accessTimes>5?5:accessTimes){
                                case 5:
                                    timekey5.setVisibility(View.VISIBLE);
                                case 4:
                                    timekey4.setVisibility(View.VISIBLE);
                                case 3:
                                    timekey3.setVisibility(View.VISIBLE);
                                case 2:
                                    timekey2.setVisibility(View.VISIBLE);
                                case 1:
                                    timekey1.setVisibility(View.VISIBLE);
                                default:

                            }
                        }
                        adapter = new QuestionHallRankAdapter(context, response.getFollowRank(), response.getTotalRank());
                        rankList.setAdapter(adapter);
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                        ScanRankingActivity.this.finish();
                    }
                }
            }
        }, null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(adapter!=null){
            if(checkedId == R.id.scan_ranking_firend_ranking){
                adapter.covert(true);
            }else if(checkedId == R.id.scan_ranking_total_ranking){
                adapter.covert(false);
            }
        }
    }
}
