package com.socialtv.personcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.MultiListActivity;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.FollowResponse;
import com.socialtv.publicentity.User;
import com.socialtv.util.IConstant;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-7.
 * 关注用户的列表
 */
public class FollowActivity extends MultiListActivity<FollowResponse> {

    @Inject
    private PersonalUrlService service;

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    @Inject
    private Activity activity;

    @InjectExtra(value = IConstant.TAG, optional = true)
    private String tag;

    @InjectView(R.id.list_empty_layout)
    private View emptyListView;

    @InjectView(R.id.list_empty_text)
    private TextView emptyTextView;

    private boolean isShowRecommend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        if ("fans".equals(tag)) {
            actionBar.setTitle("粉丝");
            emptyTextView.setText("暂无粉丝");
        } else if ("follow".equals(tag)) {
            actionBar.setTitle("关注");
            emptyTextView.setText("暂无关注");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        getListView().setDividerHeight(Utils.dip2px(this, 1));
        hasMore = true;
    }

    @Override
    protected int getContentView() {
        return R.layout.feed_list;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new FollowAdapter(this, service);
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
    protected Request<FollowResponse> createRequest() {
        if ("fans".equals(tag)) {
            return service.createFansRequest(userId);
        } else if ("follow".equals(tag)) {
            return service.createFollowListRequest(userId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<FollowResponse> loader, FollowResponse data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                FollowAdapter adapter = (FollowAdapter) getListAdapter().getWrappedAdapter();
                if (data.getUsers() != null && !data.getUsers().isEmpty()) {
                    if (isShowRecommend) {
                        adapter.clear();
                        isShowRecommend = false;
                    }
                    if (isRefresh) {
                        adapter.clear();
                        isRefresh = false;
                    }
                    this.items = data.getUsers();
                    adapter.setItems(data.getUsers(), "follow");
                    if (data.getUsers().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    loadingIndicator.setVisible(false);
                    //只有关注才加推荐
                    if (adapter.getCount() <= 0 && "follow".equals(tag)) {
                        showFollowRecommend();
                    }
                }
            } else {
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }
        showList();
    }

    private void showFollowRecommend() {
        new AbstractRoboAsyncTask<FollowResponse>(activity){
            @Override
            protected FollowResponse run(Object data) throws Exception {
                return (FollowResponse) HttpUtils.doRequest(service.createRecommendRequest()).result;
            }

            @Override
            protected void onSuccess(FollowResponse followResponse) throws Exception {
                super.onSuccess(followResponse);
                if (followResponse != null) {
                    isShowRecommend = true;
                    if (followResponse.getResponseCode() == 0) {
                        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                        loadingIndicator.setVisible(false);
                        FollowAdapter adapter = (FollowAdapter) getListAdapter().getWrappedAdapter();
                        adapter.setItems(followResponse.getUsers(), "recommend");
                    }
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isRefresh = true;
            hasMore = false;
            requestPage = 1;
            refresh();
        }
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.follow_erro;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.follow_loading;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
