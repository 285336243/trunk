package com.shengzhish.xyj.activity.vote;


import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.activity.ActivityServices;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.VoteList;
import com.shengzhish.xyj.activity.entity.VoteListResponse;
import com.shengzhish.xyj.core.FragmentProvider;
import com.shengzhish.xyj.core.SingleListActivity;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.JavaUtil;

import java.util.LinkedList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * 投票活动
 */
public class VoteActivity extends SingleListActivity<VoteListResponse> {

    @Inject
    private ActivityServices services;
    @InjectView(R.id.back_imageview)
    private View backImageView;
    @InjectView(R.id.list_empty_layout)
    private View emptyListView;
    @InjectView(R.id.submit_button)
    private Button submitButton;
    private VoteListAdapter voteListAdapter;
    private List<String> idsList = new LinkedList<String>();
    private String id;
    protected boolean hasMore = false;
    private String mode;
    private TextView shoeRule;
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String idString = JavaUtil.getInstance().listToString(idsList);
            String url = String.format("activity/vote.json?id=%s&vid=%s", id, idString);
            HttpboLis.getInstance().getHttpDialog(VoteActivity.this, Response.class, url, "正在提交", new HttpboLis.OnCompleteListener<Response>() {
                @Override
                public void onComplete(Response response) {
                    if (response.getResponseCode() == 0) {
                        refresh();

                    } else {
                        ToastUtils.show(VoteActivity.this, response.getResponseMessage());
                    }
                }
            });
        }
    };


    @Override
    protected int getContentView() {
        return R.layout.vote_list_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);
        submitButton.setOnClickListener(buttonClickListener);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        HttpboLis.getInstance().getHttpDialog(this, ActivityDetails.class, String.format("activity/detail.json?id=%s", id), "正在加载...", new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                if (!TextUtils.isEmpty(response.getActivity().getRule())) {
                    shoeRule.setText(response.getActivity().getRule() + "\n");
                }
            }
        });

        voteListAdapter.setListWatch(new VoteListAdapter.ListWatch() {
            @Override
            public void listChange(List list) {
                idsList = list;
//                submitButton.setText(idsList.toString());
            }
        });

    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        voteListAdapter = new VoteListAdapter(this);
        return voteListAdapter;
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        shoeRule = (TextView) getLayoutInflater().inflate(R.layout.vote_list_header_layout, null);
        return shoeRule;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<VoteListResponse> createRequest() {
        return services.createVoteListRequest(id);
    }

    @Override
    public void onLoadFinished(Loader<VoteListResponse> loader, VoteListResponse data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getList() != null && !data.getList().isEmpty()) {

                    List<VoteList> voteList = data.getList();
                    mode = data.getMode();
                    if ("SHOW_RESULT".equals(mode)) {
                        submitButton.setVisibility(View.GONE);
                    }
                    this.items = voteList;
                    getListAdapter().getWrappedAdapter().setItems(voteList);
                    voteListAdapter.setMode(mode);
                    if (voteList.size() < requestCount) {
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
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.loading_list_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.loading_list;
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
        hasMore = false;
        loadingIndicator.loadingStill();
        refresh();
    }


    @Override
    public void onLastItemVisible() {

        return;
  /*      super.onLastItemVisible();
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        refresh();*/
    }
}
