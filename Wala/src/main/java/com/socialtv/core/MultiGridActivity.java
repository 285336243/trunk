package com.socialtv.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.GridView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class MultiGridActivity<E> extends FragmentGridActivity<E> {

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

    protected abstract MultiTypeAdapter createAdapter();

    protected MultiTypeAdapter getGridAdapter() {
        if (gridView != null) {
            return (MultiTypeAdapter) gridView.getAdapter();
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
//        this.items.clear();
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

    @Override
    protected void refresh() {
        hasMore = false;
        super.refresh();
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        if (isRefresh)
            getGridAdapter().clear();
        super.onLoadFinished(loader, data);
    }
}
