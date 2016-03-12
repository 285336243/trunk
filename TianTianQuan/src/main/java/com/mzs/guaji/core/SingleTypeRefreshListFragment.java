package com.mzs.guaji.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by wlanjie on 14-2-14.
 */
public abstract class SingleTypeRefreshListFragment<E> extends PullToRefreshItemListFragment<E> implements LoaderManager.LoaderCallbacks<E>, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {

    protected int requestPage = 1;

    protected int requestCount = 20;

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
     * @return
     */
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
    protected SingleTypeRefreshListFragment<E> notifyDataSetChanged() {
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
    protected SingleTypeRefreshListFragment<E> setListAdapter(final ListAdapter adapter) {
        if (listView != null)
            listView.setAdapter(adapter);
        return this;
    }

    @Override
    public void onLoadFinished(Loader<E> listLoader, E es) {
        super.onLoadFinished(listLoader, es);
        if (isRefresh)
            getListAdapter().getWrappedAdapter();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefresh = true;
    }

    @Override
    public void onLastItemVisible() {

    }
}
