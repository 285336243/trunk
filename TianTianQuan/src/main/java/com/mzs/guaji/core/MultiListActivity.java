package com.mzs.guaji.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class MultiListActivity<E> extends FragmentListActivity<E> {

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

    @Override
    public void onLoadFinished(Loader<E> listLoader, E es) {
        super.onLoadFinished(listLoader, es);
        if (isRefresh)
            getListAdapter().getWrappedAdapter().clear();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefresh = true;
    }

    @Override
    public void onLastItemVisible() {

    }
}
