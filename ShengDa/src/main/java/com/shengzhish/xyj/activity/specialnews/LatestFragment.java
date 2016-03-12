package com.shengzhish.xyj.activity.specialnews;


import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.RecommendResponse;
import com.shengzhish.xyj.activity.entity.Status;
import com.shengzhish.xyj.core.MultiTypeRefreshListFragment;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.util.IConstant;

/**
 * 最新动态
 */
public class LatestFragment extends MultiTypeRefreshListFragment<RecommendResponse> {

    private static final String STATUS_HOT = "activity/status_hot.json?id=%1$s&p=%2$s&&cnt=%3$s";
    private static final String STATUS_LASTEST = "activity/status_lastest.json?id=%1$s&p=%2$s&&cnt=%3$s";
    private boolean hasMore = false;
    private String id;
    private boolean isHot;

    public static LatestFragment newInstance(boolean isHot) {
        LatestFragment fragment = new LatestFragment();
        Bundle args = new Bundle();
        args.putBoolean("isHot", isHot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            isHot = args.getBoolean("isHot");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleView.setVisibility(View.GONE);
        setEmptyText("暂无活动");
        listView.setDivider(null);
//        getListView().setDividerHeight(Utils.dip2px(getActivity(), 2));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = Utils.dip2px(getActivity(), 6);
        params.rightMargin = Utils.dip2px(getActivity(), 6);
        getListView().setLayoutParams(params);
//        getListView().setVerticalScrollBarEnabled(false);
        id = getActivity().getIntent().getStringExtra(IConstant.SPECIALNEWS_ID);
    }


    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {


        return new LatestDynamicAdapter(getActivity());
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
    protected Request<RecommendResponse> createRequest() {
        String url;
        if (isHot) {
            url = String.format(STATUS_HOT, id, requestPage, requestCount);
        } else {
            url = String.format(STATUS_LASTEST, id, requestPage, requestCount);
        }
//       Log.v("person","requestPage == "+requestPage);
        GsonRequest<RecommendResponse> request = new GsonRequest<RecommendResponse>(Request.Method.GET, url);
        request.setClazz(RecommendResponse.class);
        return request;
    }

    @Override
    public void onLoadFinished(Loader<RecommendResponse> listLoader, RecommendResponse data) {
        super.onLoadFinished(listLoader, data);
        if (data != null) {
//            Log.v("person", "data.getStatuses().size() == " + data.getStatuses().size());
            if (data.getResponseCode() == 0) {
                if (data.getStatuses() != null && data.getStatuses().size() > 0) {
                    this.items = data.getStatuses();

                    LatestDynamicAdapter adapter = (LatestDynamicAdapter) getListAdapter().getWrappedAdapter();
                    for (Status item : data.getStatuses()) {
                        adapter.addItemObject(item);
                    }
                    if (data.getStatuses().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }

                } else {

                    if (!this.items.isEmpty()){
                        loadingIndicator.setVisible(true);
                        loadingIndicator.loadingAllFinish();
                        hasMore = true;
                    }else
                        loadingIndicator.setVisible(false);
                }
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
        return R.string.activity_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.activity_loading;
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        forceRefresh();
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        requestPage = 1;
        hasMore=false;
        loadingIndicator.loadingStill();
        forceRefresh();
    }
}
