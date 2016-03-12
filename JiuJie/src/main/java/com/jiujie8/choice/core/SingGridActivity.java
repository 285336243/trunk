package com.jiujie8.choice.core;

import android.os.Bundle;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class SingGridActivity<E extends Response> extends FragmentGridActivity<E> {

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

    protected abstract SingleTypeAdapter<?> createAdapter();

    protected SingleTypeAdapter<?> getGridAdapter() {
        if (gridView != null) {
            return (SingleTypeAdapter<?>) gridView.getAdapter();
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

    @Override
    protected void refresh() {
        hasMore = false;
        super.refresh();
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
}
