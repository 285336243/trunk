package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 13-12-25.
 */
public class VoteCommentActivity extends GuaJiActivity {

    private Context context = VoteCommentActivity.this;
    private View mReleaseLayout;
    private EditText mContentEdit;
    private String voteUrl;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.topic_comment_layput);
        voteUrl = getIntent().getStringExtra("vote_url");
        View mBackLayout = findViewById(R.id.topic_comment_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mReleaseLayout = findViewById(R.id.topic_comment_release);
        mReleaseLayout.setOnClickListener(mCommentClickListener);
        mContentEdit = (EditText) findViewById(R.id.topic_comment_content);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(VoteCommentActivity.this, mContentEdit.getText().toString());
        }
    };

    /**
     * 发布按钮点击事件
     */
    View.OnClickListener mCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(TextUtils.isEmpty(mContentEdit.getText().toString())) {
                ToastUtil.showToast(context, R.string.opinion_feedback_cannot_empty);
                return;
            }
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("message", mContentEdit.getText().toString());
            v.setEnabled(false);
            TipsUtil.showTipDialog(context, "正在发布评论");
            if (!TextUtils.isEmpty(voteUrl)) {
                mApi.requestPostData(voteUrl, DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
                    @Override
                    public void onResponse(DefaultReponse response) {
                        TipsUtil.dismissDialog();
                        v.setEnabled(true);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                Intent mIntent = new Intent();
                                mIntent.putExtra("success", "success");
                                setResult(RESULT_OK, mIntent);
                                finish();
                            } else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }
                }, VoteCommentActivity.this);
            }
        }
    };

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(VoteCommentActivity.this, mContentEdit.getText().toString());
    }
}
