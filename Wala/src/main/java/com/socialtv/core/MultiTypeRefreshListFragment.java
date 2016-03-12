package com.socialtv.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.util.NetWorkUtil;

/**
 * Created by wlanjie on 14-2-14.
 */
public abstract class MultiTypeRefreshListFragment<E> extends PullToRefreshItemListFragment<E> implements LoaderManager.LoaderCallbacks<E>, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {

    /**
     * Is Refresh State
     */
    protected boolean isRefresh = false;

    /**
     * Load data hasmore
     */
    protected boolean hasMore = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureList(getActivity(), getListView());
    }

    /**
     * Configure list after view has been created
     * @param activity
     * @param listView
     */
    protected void configureList(Activity activity, ListView listView) {
        listView.setAdapter(createListAdapter());
        loadingIndicator = new ResourceLoadingIndicator(getActivity(), getLoadingMessage());
        loadingIndicator.setList(getListAdapter());
    }

    /**
     * create adapter to display items
     * @return
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
     * Create adapter to display items
     * @return adapter
     */
    protected abstract MultiTypeAdapter createAdapter();

    /**
     * Get list adapter
     * @return
     */
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
     * @return this fragment
     */
    protected MultiTypeRefreshListFragment<E> notifyDataSetChanged() {
        HeaderFooterListAdapter<MultiTypeAdapter> root = getListAdapter();
        if (root != null) {
            MultiTypeAdapter typeAdapter = root.getWrappedAdapter();
            if (typeAdapter != null) {
                typeAdapter.notifyDataSetChanged();
            }
        }
        return this;
    }

    /**
     * Set list adapter to use on list view
     * @param adapter
     * @return this fragment
     */
    protected MultiTypeRefreshListFragment<E> setListAdapter(final ListAdapter adapter) {
        if (listView != null) {
            listView.setAdapter(adapter);
        }
        return this;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        isRefresh = true;
        hasMore = false;
        refresh();
    }

    @Override
    protected void showError(Exception e, int defaultMessage) {
        super.showError(e, defaultMessage);
        loadingIndicator.setVisible(false);
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        isRefresh = false;
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();

    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        if (isRefresh && NetWorkUtil.isNetworkConnected(getActivity()))
            getListAdapter().getWrappedAdapter().clear();
        super.onLoadFinished(loader, data);
    }
}
