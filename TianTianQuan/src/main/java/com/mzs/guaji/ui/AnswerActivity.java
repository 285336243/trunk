package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.AnswerAdapter;
import com.mzs.guaji.entity.AnswerResponse;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.QuestionResult;
import com.mzs.guaji.util.CommonDialog;
import com.mzs.guaji.util.Log;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.ScaleImageView;

/**
 * Created by wlanjie on 13-12-23.
 * 答题UI
 */
public class AnswerActivity extends GuaJiActivity implements CommonDialog.OnOKPressListener{

    private Context context = AnswerActivity.this;
    private LinearLayout mBackLayout;
    private String code;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private TextView mTimeText;
    private TextView mProgressText;
    private TextView mTitleText;
    private ScaleImageView mImageView;
    private Handler mHandler;
    private AnswerRunnable mRunnable;
    private int position = 0;
    private String sessionId;

    private RelativeLayout answerTitleLayout;
    private LinearLayout answerBottomLayout;


    private Handler nextAnswerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListView.setEnabled(true);
            AnswerResponse response = (AnswerResponse) msg.obj;
            updateAnswerContent(response, position);
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.answer_layout);
        mHandler = new Handler();
        code = getIntent().getStringExtra("code");
        mBackLayout = (LinearLayout) findViewById(R.id.answer_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mProgressBar = (ProgressBar) findViewById(R.id.answer_progress);
        mTimeText = (TextView) findViewById(R.id.answer_time_text);
        mListView = (ListView) findViewById(R.id.answer_list);
        mProgressText = (TextView) findViewById(R.id.answer_progress_text);
        mTitleText = (TextView) findViewById(R.id.answer_title);
        mImageView = (ScaleImageView) findViewById(R.id.answer_image);
        answerTitleLayout = (RelativeLayout) findViewById(R.id.answer_title_layout);
        answerBottomLayout = (LinearLayout)findViewById(R.id.answer_bottom_layout);
        requestData();

    }

    private void requestData() {
        mApi.requestGetData(getQuestionRequest(code), AnswerResponse.class, new Response.Listener<AnswerResponse>() {
            @Override
            public void onResponse(AnswerResponse response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getQuestions() != null) {
                            sessionId = response.getSessionId();
                            updateAnswerContent(response, position);
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                        AnswerActivity.this.finish();
                    }
                }
            }
        }, null);
    }

    /**
     * 返回按钮点击事件
     */
    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CommonDialog dialog = new CommonDialog(context, "继续", "放弃", "提示", "放弃答题不能获得任何积分，确定放弃答题么?");
            dialog.setOnOKPressListener(AnswerActivity.this);
            dialog.show();
        }
    };


    /**
     * 获取题目内容URL
     * @param code
     * @return
     */
    private String getQuestionRequest(String code) {
        return DOMAIN + "question/start.json" + "?code=" + code;
    }

    /**
     * 提交答案URL
     * @param sessionId
     * @param questionId
     * @param answerId
     * @param costTime
     * @return
     */
    private String getQuestionResultRequest(String sessionId, long questionId, long answerId, int costTime) {
        return DOMAIN + "question/result.json" + "?sessionId=" + sessionId + "&questionId=" + questionId + "&answerId=" + answerId + "&costTime=" + costTime;
    }

    /**
     * 答案list点击事件
     */
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mRunnable.stop();
            AnswerAdapter.AnswerViewHolder mHolder = (AnswerAdapter.AnswerViewHolder) view.getTag();
            if(mHolder.mQuestion.getResult() == mHolder.mAnswerId) {
                mHolder.mRelativeLayout.setBackgroundResource(R.drawable.bdg_answerright_tj);
                mHolder.mImageView.setImageResource(R.drawable.icon_sure_tj);
                mHolder.mTextView.setTextColor(Color.WHITE);
                execSubmitQuestion(mHolder, mHolder.response.getSessionId(), mHolder.mQuestion.getId(), mHolder.mAnswerId,(mHolder.mQuestion.getTime() - mRunnable.getTime()/1000));
            }else {
                mHolder.mRelativeLayout.setBackgroundResource(R.drawable.bdg_answerwrong_tj);
                mHolder.mImageView.setImageResource(R.drawable.icon_cancel_tj);
                mHolder.mTextView.setTextColor(Color.WHITE);
                execSubmitQuestion(mHolder, mHolder.response.getSessionId(), mHolder.mQuestion.getId(), mHolder.mAnswerId, (mHolder.mQuestion.getTime() - mRunnable.getTime()/1000));
                startEndAnswerUI(mHolder.response.getSessionId());
            }
            mListView.setEnabled(false);
        }
    };

    private void startEndAnswerUI(String sessionId) {
        Intent mIntent = new Intent(context, EndAnswerActivity.class);
        mIntent.putExtra("sessionId", sessionId);
        startActivity(mIntent);
        this.finish();
    }

    /**
     * 执行提交答案
     * @param mHolder
     * @param sessionId
     * @param questionId
     * @param answerId
     * @param costTime
     */
    private void execSubmitQuestion(final AnswerAdapter.AnswerViewHolder mHolder, String sessionId, long questionId, long answerId, int costTime) {
        mApi.requestGetData(getQuestionResultRequest(sessionId, questionId, answerId, costTime), QuestionResult.class, new Response.Listener<QuestionResult>() {
            @Override
            public void onResponse(QuestionResult response) {
                if(response != null) {
                    if(response.getResponseCod() == 0) {
                        position++;
                        if(position < mHolder.response.getQuestions().size()) {
                            Message msg = Message.obtain();
                            msg.obj = mHolder.response;
                            nextAnswerHandler.sendMessageDelayed(msg, 0);
                        }else {
                            startEndAnswerUI(mHolder.response.getSessionId());
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, null);
    }

    /**
     * 更新下一题内容
     * @param response
     * @param position
     */
    private void updateAnswerContent(AnswerResponse response, int position) {

        mTitleText.setText(response.getQuestions().get(position).getTitle());
        mProgressBar.setMax(response.getQuestions().get(position).getTime() * 1000);
        mProgressBar.setProgress(response.getQuestions().get(position).getTime());
        mProgressText.setText(position + 1 +  "/" + response.getQuestions().size());

        if(response.getQuestions().get(position).getAnswers() != null) {
            mListView.setAdapter(new AnswerAdapter(context, response, response.getQuestions().get(position), response.getQuestions().get(position).getAnswers()));
            mListView.startLayoutAnimation();
        }
        if(response.getQuestions().get(position).getImg() != null && !"".equals(response.getQuestions().get(position).getImg())) {
            mImageLoader.displayImage(response.getQuestions().get(position).getImg(), mImageView, options);
        }
        answerTitleLayout.startLayoutAnimation();
        answerBottomLayout.startLayoutAnimation();
        mListView.setOnItemClickListener(mItemClickListener);

        if(mRunnable == null){
            mRunnable = new AnswerRunnable();
            mHandler.postDelayed(mRunnable, 50);
        }
        mRunnable.reset();
    }

    /**
     * 计时runnable
     */
    private class AnswerRunnable implements Runnable {

        private int mSendTime ;
        private boolean isRun = false;
        private boolean isFinished = false;

        @Override
        public void run() {
            if(isFinished){
                return;
            }
            if(isRun){
                mSendTime = mSendTime-50;
                if(mSendTime % 1000 == 0 ){
                    int showTime = mSendTime/1000;
                    if(showTime < 10) {
                        mTimeText.setText("0"+showTime);
                    }else {
                        mTimeText.setText(showTime+"");
                    }
                }
                mProgressBar.setProgress(mSendTime);
            }
            if(mSendTime <= 0) {
                startEndAnswerUI(sessionId);
            }else {
                mHandler.postDelayed(this, 50);
            }
        }
          
        public void reset(){
            mSendTime = mProgressBar.getMax();
            int showTime = mSendTime/1000;
            if(showTime < 10) {
                mTimeText.setText("0"+showTime);
            }else {
                mTimeText.setText(showTime+"");
            }
            mProgressBar.setProgress(mSendTime);
            isRun = true;
        }

        public void stop(){
            isRun = false;
        }

        public void finish(){
            isFinished = true;
        }

        public int getTime() {
            return mSendTime;
        }
    }


    @Override
    public void onBackPressed() {
        CommonDialog dialog = new CommonDialog(context, "继续", "放弃", "提示", "放弃答题不能获得任何积分，确定放弃答题么?");
        dialog.setOnOKPressListener(this);
        dialog.show();
    }

    @Override
    public void onOkClick() {

    }

    @Override
    public void onCancelClick() {
        mApi.requestGetData(giveup(sessionId), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                Log.i(AnswerActivity.class.getName(), "give up");

            }
        }, null);
        this.finish();
    }

    private String giveup(String sessionId) {
        return DOMAIN + "question/giveup.json?sessionId=" + sessionId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRunnable!=null){
            mRunnable.finish();
        }
    }
}
