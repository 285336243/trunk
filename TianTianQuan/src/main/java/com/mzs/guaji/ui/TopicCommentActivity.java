package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.DialogFragmentActivity;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.TopicDetailsService;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 13-12-25.
 */
public class TopicCommentActivity extends DialogFragmentActivity {

    private Context context = TopicCommentActivity.this;
    private View mReleaseLayout;
    private EditText mContentEdit;
    private long id;
    private boolean isFrom;
    private String type;

    @Inject
    TopicDetailsService service;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.topic_comment_layput);
        id = getLongExtra(IConstant.TOPIC_ID);
        type = getStringExtra(IConstant.TOPIC_TYPE);
        isFrom = getIntent().getBooleanExtra("from", false);
        if (type == null || "".equals(type)) {
            type = "group";
        }
        View mBackLayout = findViewById(R.id.topic_comment_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mReleaseLayout = findViewById(R.id.topic_comment_release);
        mReleaseLayout.setOnClickListener(mCommentClickListener);
        mContentEdit = (EditText) findViewById(R.id.topic_comment_content);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(TopicCommentActivity.this, mContentEdit.getText().toString());
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
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put("id", id+"");
            headers.put("message", mContentEdit.getText().toString());
            headers.put("type", type);
            new ProgressDialogTask<DefaultReponse>(context){

                @Override
                protected DefaultReponse run(Object data) throws Exception {
                    return new ResourcePager<DefaultReponse>(){
                        @Override
                        protected Object getId(DefaultReponse resource) {
                            return null;
                        }
                        @Override
                        public PageIterator<DefaultReponse> createIterator(int page, int count) {
                            return service.pageCommitComment(headers);
                        }
                    }.start();
                }

                @Override
                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                    super.onSuccess(defaultReponse);
                    if (defaultReponse != null) {
                        if (defaultReponse.getResponseCode() == 0) {
                            if(isFrom) {
                                Intent mIntent = new Intent(context, TopicDetailsActivity.class);
                                mIntent.putExtra(IConstant.TOPIC_ID, id);
                                mIntent.putExtra(IConstant.TOPIC_TYPE, type);
                                startActivity(mIntent);
                            }else {
                                Intent mIntent = new Intent();
                                mIntent.putExtra("success", "success");
                                setResult(RESULT_OK, mIntent);
                            }
                            finish();
                        } else {
                            ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                        }
                    }
                }
            }.start("正在发布评论");
        }
    };

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(TopicCommentActivity.this, mContentEdit.getText().toString());
    }
}
