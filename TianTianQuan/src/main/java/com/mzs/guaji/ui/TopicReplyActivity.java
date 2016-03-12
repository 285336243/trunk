package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.DialogFragmentActivity;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.TopicDetailsService;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 13-12-26.
 * 话题回复评论的UI
 */
public class TopicReplyActivity extends DialogFragmentActivity {

    private Context context = TopicReplyActivity.this;
    private EditText mContentText;
    private View mReleaseLayout;
    private TextView mNicknameText;
    private long commentId;
    private long topicId;
    private boolean isFrom;
    private String type;

    @Inject
    TopicDetailsService service;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.reply_topic_layout);
        String mNickName = getIntent().getStringExtra("nickname");
        commentId = getIntent().getLongExtra("commentId", -1);
        topicId = getIntent().getLongExtra("topicId", -1);
        //如果from为false,就是从话题里进来的,如果为true则是从个人中心进来的,从个人中心进来的回复成功跳到话题详情
        isFrom = getIntent().getBooleanExtra("from", false);
        type = getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(type)) {
            type = "group";
        }
        View mBackLayout = findViewById(R.id.reply_topic_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mReleaseLayout = findViewById(R.id.reply_topic_release);
        mReleaseLayout.setOnClickListener(mReleaseClickListener);
        mContentText = (EditText) findViewById(R.id.reply_topic_content);
        mNicknameText = (TextView) findViewById(R.id.reply_topic_nickname);
        mNicknameText.setText("回复 "+mNickName);

    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(TopicReplyActivity.this, mContentText.getText().toString());
        }
    };

    /**
     * 发布按钮点击事件
     */
    View.OnClickListener mReleaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            execReply(commentId, v);
        }
    };

    /**
     * 执行回复
     */
    private void execReply(long id, final View v) {
        if(TextUtils.isEmpty(mContentText.getText().toString())) {
            ToastUtil.showToast(context, R.string.opinion_feedback_cannot_empty);
            return;
        }
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("id", id+"");
        headers.put("message", mContentText.getText().toString());
        headers.put("type", type);
        new ProgressDialogTask<DefaultReponse>(context){

            @Override
            protected DefaultReponse run(Object data) throws Exception {
                return new ResourcePager<DefaultReponse>(){
                    @Override
                    public PageIterator<DefaultReponse> createIterator(int page, int count) {
                        return service.pageReply(headers);
                    }
                }.start();
            }

            @Override
            protected void onSuccess(DefaultReponse response) throws Exception {
                super.onSuccess(response);
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        ToastUtil.showToast(context, "回复成功");
                        if(isFrom) {
                            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
                            mIntent.putExtra("topicId", topicId);
                            startActivity(mIntent);
                        }
                        Intent mIntent = new Intent(BroadcastActionUtil.REPLY_OK);
                        sendBroadcast(mIntent);
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }.start("正在发布回复");
    }

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(TopicReplyActivity.this, mContentText.getText().toString());
    }
}
