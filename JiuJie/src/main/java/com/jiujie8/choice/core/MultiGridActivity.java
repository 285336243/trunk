package com.jiujie8.choice.core;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.util.NetWorkUtil;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class MultiGridActivity<E extends Response> extends FragmentGridActivity<E> {

    /**
     * Is Refresh State
     */
    protected boolean isRefresh = false;

    /**
     * Load data hasmore
     */
    protected boolean hasMore = false;

    /**
     * Loading View
     */
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingView = getLayoutInflater().inflate(R.layout.loading_item, null);
        getGridView().addFooterView(loadingView);
        if (isAddAdapterHeader()) {
            getGridView().addHeaderView(adapterHeaderView());
        }
        gridView.setAdapter(createAdapter());
    }

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

    protected MultiTypeAdapter getGridAdapter() {
        if (gridView != null) {
            return (MultiTypeAdapter) gridView.getAdapter();
        } else {
            return null;
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        super.onRefreshStarted(view);
        isRefresh = true;
        hasMore = false;
        requestPage = 1;
        if (getGridView().getFooterViewsCount() <= 0) {
            getGridView().addFooterView(loadingView);
        }
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
    protected void refresh() {
        hasMore = false;
        super.refresh();
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        if (isRefresh && NetWorkUtil.isNetworkConnected(this))
            getGridAdapter().clear();
        super.onLoadFinished(loader, data);
    }
}
