package com.jiujie8.choice.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.util.NetWorkUtil;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class MultiListActivity<E extends Response> extends FragmentListActivity<E> {

    /**
     * Is Refresh State
     */
    protected boolean isRefresh = false;

    /**
     * Load data hasmore
     */
    protected boolean hasMore = false;

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
        listView.setAdapter(createListAdapter());
        loadingIndicator = new ResourceLoadingIndicator(this);
        loadingIndicator.setList(getListAdapter());
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    protected HeaderFooterListAdapter<MultiTypeAdapter> createListAdapter() {
        MultiTypeAdapter wrapped = createAdapter();
        HeaderFooterListAdapter<MultiTypeAdapter> adapter = new HeaderFooterListAdapter<MultiTypeAdapter>(getListView(), wrapped);
        if (isAddAdapterHeader()) {
            adapter.addHeader(adapterHeaderView());
        }
        return adapter;
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
    protected HeaderFooterListAdapter<MultiTypeAdapter> getListAdapter() {
        if (listView != null) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter instanceof HeaderFooterListAdapter) {
                return (HeaderFooterListAdapter<MultiTypeAdapter>) listAdapter;
            } else {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
                return  (HeaderFooterListAdapter<MultiTypeAdapter>) headerViewListAdapter.getWrappedAdapter();
            }
        } else {
            return null;
        }
    }

    /**
     * Notify the underlying adapter that the data set has changed
     *
     * @return this fragment
     */
    protected MultiListActivity<E> notifyDataSetChanged() {
        HeaderFooterListAdapter<MultiTypeAdapter> root = getListAdapter();
        if (root != null) {
            MultiTypeAdapter typeAdapter = root.getWrappedAdapter();
            if (typeAdapter != null)
                typeAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * Set list adapter to use on list view
     *
     * @param adapter
     * @return this fragment
     */
    protected MultiListActivity<E> setListAdapter(final ListAdapter adapter) {
        if (listView != null)
            listView.setAdapter(adapter);
        return this;
    }

    protected MultiListActivity<E> setListHeaderAdapter(final BaseAdapter listAdapter) {
        HeaderFooterListAdapter<BaseAdapter> adapter = new HeaderFooterListAdapter<BaseAdapter>(getListView(), listAdapter);
        if (isAddAdapterHeader()) {
            adapter.addHeader(adapterHeaderView());
        }
        if (listView != null)
            listView.setAdapter(adapter);
        return this;
    }

    @Override
    protected void showError(Exception e, int defaultMessage) {
        super.showError(e, defaultMessage);
        loadingIndicator.setVisible(false);
    }

    @Override
    public void onRefreshStarted(View view) {
        super.onRefreshStarted(view);
        isRefresh = true;
        hasMore = false;
        requestPage = 1;
        if (!loadingIndicator.getVisible())
            loadingIndicator.setVisible(true);
        refresh();
    }

    public void onLastItemVisible() {
        isRefresh = false;
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        if (isRefresh && NetWorkUtil.isNetworkConnected(this))
            getListAdapter().getWrappedAdapter().clear();
        super.onLoadFinished(loader, data);
    }
}
