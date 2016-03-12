package com.socialtv.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class SingleListActivity<E> extends FragmentListActivity<E> {

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
        loadingIndicator = new ResourceLoadingIndicator(this, getLoadingMessage());
        loadingIndicator.setList(getListAdapter());
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    protected HeaderFooterListAdapter<SingleTypeAdapter<?>> createListAdapter() {
        SingleTypeAdapter<?> wrapped = createAdapter();
        HeaderFooterListAdapter<SingleTypeAdapter<?>> adapter = new HeaderFooterListAdapter<SingleTypeAdapter<?>>(getListView(), wrapped);
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
    protected abstract SingleTypeAdapter<?> createAdapter();

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
    protected HeaderFooterListAdapter<SingleTypeAdapter<E>> getListAdapter() {
        if (listView != null) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter instanceof HeaderFooterListAdapter) {
                return (HeaderFooterListAdapter<SingleTypeAdapter<E>>) listAdapter;
            } else {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
                return  (HeaderFooterListAdapter<SingleTypeAdapter<E>>) headerViewListAdapter.getWrappedAdapter();
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
    protected SingleListActivity<E> notifyDataSetChanged() {
        HeaderFooterListAdapter<SingleTypeAdapter<E>> root = getListAdapter();
        if (root != null) {
            SingleTypeAdapter<E> typeAdapter = root.getWrappedAdapter();
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
    protected SingleListActivity<E> setListAdapter(final ListAdapter adapter) {
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
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        isRefresh = true;
        hasMore = false;
        requestPage = 1;
        refresh();
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        isRefresh = false;
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();
    }
}
