package com.shengzhish.xyj.activity.specialnews;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.activity.ActivityServices;
import com.shengzhish.xyj.activity.entity.CommentListResponse;
import com.shengzhish.xyj.activity.entity.Post;
import com.shengzhish.xyj.core.FragmentProvider;
import com.shengzhish.xyj.core.SingleListActivity;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * 动态评论列表
 */
public class DynamicCommendListActivity extends SingleListActivity<CommentListResponse> {

    @Inject
    private ActivityServices services;

    @InjectView(R.id.list_empty_layout)
    private View emptyListView;

    @InjectView(R.id.list_empty_text)
    private TextView emptyTextView;

    @InjectView(R.id.comment_editview)
    private EditText commnentEdit;

    @InjectView(R.id.send_button)
    private Button sendButton;


    @InjectView(R.id.back_imageview)
    private View backImageView;

    protected boolean hasMore = false;
    private String id;
    private static final String POSTURL = "activity/status_post.json";
    private Context context;
    private String cusorId = String.valueOf(0);

    List<Post> postList = new LinkedList<Post>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emptyTextView.setText("暂时没有评论 \n来坐第一个沙发");
        context = DynamicCommendListActivity.this;
        id = getIntent().getStringExtra(IConstant.STATUS_ID);
        listView.setDividerHeight(Utils.dip2px(this, 2));
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = commnentEdit.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    showCancelDialog();
                } else {
                    finish();
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtilSh.isLogin(context)) {
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    sendCommmentData();
                }
            }
        });

    }

    private void sendCommmentData() {
        String content = commnentEdit.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show(this, "不能发表空评论");
            return;
        }
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("id", id);
        bodyMap.put("msg", content);

        HttpboLis.getInstance().postHttp(this, Response.class, POSTURL, "正在发送", bodyMap, new HttpboLis.OnCompleteListener<Response>() {
            @Override
            public void onComplete(Response response) {
                if (!postList.isEmpty()) {
                    postList.clear();
                }
                cusorId = String.valueOf(0);
                refresh();
                if(emptyTextView.getVisibility()==View.VISIBLE){
                    hide(emptyTextView);
                }
                commnentEdit.setText(null);
                ToastUtils.show(DynamicCommendListActivity.this, "发送成功");
            }
        });

    }


    @Override
    protected int getContentView() {
        return R.layout.dynamic_comment_list_layout;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new CommentListAdapter(this);
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        return null;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<CommentListResponse> createRequest() {
        return services.createCommentListRequest(id, cusorId, requestCount);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.listerror;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.comment_list_loading;
    }

    @Override
    public void onLoadFinished(Loader<CommentListResponse> loader, CommentListResponse data) {
        super.onLoadFinished(loader, data);


        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getPosts() != null && !data.getPosts().isEmpty()) {
                    cusorId = data.getCusorId();
                    List<Post> posts = data.getPosts();
                    postList.addAll(data.getPosts());
                    this.items = posts;
                    getListAdapter().getWrappedAdapter().setItems(postList);
                    if (posts.size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }

                } else {
                    if (!this.items.isEmpty()) {
                        loadingIndicator.setVisible(true);
                        loadingIndicator.loadingAllFinish();
                        hasMore = true;
                    } else {
                        this.items.clear();
                        show(emptyListView);
                    }

                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }

        }
        showList();


    }

    /**
     * Get provider of the currently selected fragment
     *
     * @return fragment provider
     */
    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        if (!postList.isEmpty()) {
            postList.clear();
        }
        cusorId = String.valueOf(0);
        hasMore = false;
        loadingIndicator.loadingStill();
        refresh();
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        refresh();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final String content = commnentEdit.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                showCancelDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.edit_continue_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }
}
