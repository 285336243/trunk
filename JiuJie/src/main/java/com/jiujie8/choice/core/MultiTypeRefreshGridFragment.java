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
public abstract class MultiTypeRefreshGridFragment<E extends Response> extends PullToRefreshItemGridFragment<E> {

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAddAdapterHeader())
            gridView.addHeaderView(adapterHeaderView());
        loadingView = getLayoutInflater(null).inflate(R.layout.loading_item, null);
        gridView.addFooterView(loadingView);
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
        if (getLoaderManager().hasRunningLoaders())
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
        if (isRefresh && NetWorkUtil.isNetworkConnected(getActivity()))
            getGridAdapter().clear();
        super.onLoadFinished(loader, data);
    }
}
