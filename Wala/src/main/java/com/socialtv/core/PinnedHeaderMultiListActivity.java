package com.socialtv.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class PinnedHeaderMultiListActivity<E> extends PinnedHeaderListActivity<E> {

    /**
     * Is Refresh State
     */
    protected boolean isRefresh = false;

    /**
     * Load data hasmore
     */
    protected boolean hasMore = false;

    /**
     * List foot view
     */
    protected View footView;

    /**
     * List foot loading message view
     */
    protected TextView loadingTextView;

    /**
     * List foot loading progressbar
     */
    protected ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureList(this, getListView());
    }

    /**
     * Configure list after view has been created
     *
     * @param activity
     * @param listView
     */
    protected void configureList(Activity activity, ListView listView) {
        footView = createFootView();
        listView.addFooterView(footView);
        listView.addHeaderView(adapterHeaderView());
        listView.setAdapter(createListAdapter());
    }

    /**
     * Create ListView FootView
     * @return
     */
    protected View createFootView() {
        View v = getLayoutInflater().inflate(R.layout.loading_item, null);
        loadingProgressBar = (ProgressBar) v.findViewById(R.id.pb_loading);
        loadingTextView = (TextView) v.findViewById(R.id.tv_loading);
        loadingTextView.setText(getLoadingMessage());
        return v;
    }

    /**
     * Set List FootView visible
     * @param visible
     */
    protected void setFootVisible(boolean visible) {
        if (visible)
            getListView().addFooterView(footView);
        else
            getListView().removeFooterView(footView);
    }

    /**
     * loading all data
     */
    protected void loadingAllFinish() {
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setText("已加载全部内容");
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    protected MultiTypeAdapter createListAdapter() {
//        MultiTypeAdapter wrapped = createAdapter();
//        HeaderFooterListAdapter<MultiTypeAdapter> adapter = new HeaderFooterListAdapter<MultiTypeAdapter>(getListView(), wrapped);
//        if (isAddAdapterHeader()) {
//            adapter.addHeader(adapterHeaderView());
//        }
        return createAdapter();
    }

    /**
     * Create adapter to display items
     *
     * @param
     * @return adapter
     */
    protected abstract MultiTypeAdapter createAdapter();

    /**
     * Is add adapter header
     * @return
     */
    protected abstract boolean isAddAdapterHeader();

    /**
     * Adapter header view
     * @return
     */
    protected abstract View adapterHeaderView();

    /**
     * Get list adapter
     *
     * @return list adapter
     */
    @SuppressWarnings("unchecked")
    protected MultiTypeAdapter getListAdapter() {
        if (headerListView != null) {
            ListAdapter listAdapter = headerListView.getAdapter();
            if (listAdapter instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
                return (MultiTypeAdapter) headerViewListAdapter.getWrappedAdapter();
            }
        }
        return null;
    }

    /**
     * Notify the underlying adapter that the data set has changed
     *
     * @return this fragment
     */
    protected PinnedHeaderMultiListActivity<E> notifyDataSetChanged() {
        MultiTypeAdapter root = getListAdapter();
        if (root != null) {
            root.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * Set list adapter to use on list view
     *
     * @param adapter
     * @return this fragment
     */
    protected PinnedHeaderMultiListActivity<E> setListAdapter(final ListAdapter adapter) {
        if (headerListView != null)
            headerListView.setAdapter(adapter);
        return this;
    }

    protected PinnedHeaderMultiListActivity<E> setListHeaderAdapter(final BaseAdapter listAdapter) {
        HeaderFooterListAdapter<BaseAdapter> adapter = new HeaderFooterListAdapter<BaseAdapter>(getListView(), listAdapter);
        if (isAddAdapterHeader()) {
            adapter.addHeader(adapterHeaderView());
        }
        if (headerListView != null)
            headerListView.setAdapter(adapter);
        return this;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        isRefresh = true;
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        isRefresh = false;
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        super.onLoadFinished(loader, data);
        if (isRefresh)
            getListAdapter().clear();
    }
}
