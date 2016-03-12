package com.socialtv.core;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class SingGridActivity<E> extends FragmentGridActivity<E> {

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
        configureList(this, getGridView());
    }

    protected void configureList(Activity activity, GridView gridView) {
        gridView.setAdapter(createAdapter());
    }

    protected abstract SingleTypeAdapter<?> createAdapter();

    protected SingleTypeAdapter<?> getGridAdapter() {
        if (gridView != null) {
            return (SingleTypeAdapter<?>) gridView.getAdapter();
        } else {
            return null;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
        super.onRefresh(refreshView);
        isRefresh = true;
        hasMore = false;
        requestPage = 1;
        refresh();
    }

    @Override
    protected void refresh() {
        hasMore = false;
        super.refresh();
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
