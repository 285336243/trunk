package com.jiujie8.choice.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.view.GridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import roboguice.inject.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class FragmentGridActivity<E extends Response> extends PagerActivity implements LoaderManager.LoaderCallbacks<E>, OnRefreshListener {

    @InjectView(R.id.ptr_layout)
    protected PullToRefreshLayout refreshLayout;

    @InjectView(R.id.refresh_list)
    protected GridView gridView;

    @InjectView(R.id.pb_loading)
    protected ProgressBar progressBar;

    @InjectView(R.id.list_empty_layout)
    protected View emptyView;

    protected AbsListView.OnScrollListener scrollListener;

    protected boolean showList = false;
    /**
     * Is Last Item Visible
     */
    private boolean mLastItemVisible = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getContentView());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                onGridItemClick((GridView) adapterView, view, position, id);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                return onGridItemLongClick((GridView) adapterView, view, position, id);
            }
        });

        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
                .useViewDelegate(GridView.class, new AbsListViewDelegate())
                .setup(refreshLayout);


        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRefreshStarted(View view) {

    }

    protected int getContentView() {
        return R.layout.grid_list;
    }

    protected GridView getGridView() {
        return gridView;
    }

    public void onGridItemClick(GridView g, View v, int position, long id) {

    }

    public boolean onGridItemLongClick(GridView g, View v, int position, long id) {
        return false;
    }

    /**
     * create Reqeust
     * @return
     */
    protected abstract Request<E> createRequest();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emptyView = null;
        progressBar = null;
        gridView = null;
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        if (gridView != null) {
            gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        if(gridView != null) {
            AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int state) {
                    if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
                        onLastItemVisible();
                    }
                    if (scrollListener != null) {
                        scrollListener.onScrollStateChanged(absListView, state);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
                    if (scrollListener != null) {
                        scrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
                    }
                }
            };
            gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, mScrollListener));
        }
    }

    /**
     * Last Item Visible
     */
    public void onLastItemVisible() {

    }

    protected void refresh() {
        refresh(null);
    }

    private void refresh(Bundle args) {
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<E> onCreateLoader(int i, Bundle bundle) {
        return new ThrowableLoader<E>(this) {
            @Override
            public E loadData() throws Exception {
                return (E) HttpUtils.doRequest(createRequest()).result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        refreshLayout.setRefreshComplete();
        Exception exception = getException(loader);
        if (exception != null) {
            showError(exception, R.string.loading_error);
            showList();
            return;
        }
        if ("0000".equals(data.getCode())) {
            onLoadFinishedCallback(data);
        } else {
            ToastUtils.show(this, data.getMessage());
        }
        showList();
    }

    @Override
    public void onLoaderReset(Loader<E> eLoader) {

    }

    /**
     * onLoadFinished callback
     * @param data
     */
    public abstract void onLoadFinishedCallback(E data);

    /**
     * Show exception in
     *
     * @param e
     * @param defaultMessage
     */
    protected void showError(final Exception e, final int defaultMessage) {
        ToastUtils.show(this, e, defaultMessage);
    }

    /**
     * Get exception from loader if it provides one by being a
     *
     * @param loader
     * @return exception or null if none provided
     */
    protected Exception getException(final Loader<E> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<E>) loader).clearException();
        } else {
            return null;
        }
    }

    private FragmentGridActivity<E> fadeIn(final View view, final boolean animate) {
        if (view != null)
            if (animate)
                view.startAnimation(AnimationUtils.loadAnimation(this,
                        android.R.anim.fade_in));
            else
                view.clearAnimation();
        return this;
    }

    public FragmentGridActivity<E> show(final View view) {
        super.show(view);
        return this;
    }

    public FragmentGridActivity<E> hide(final View view) {
        super.hide(view);
        return this;
    }

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, true);
    }

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @param animate
     * @return this fragment
     */
    public FragmentGridActivity<E> setListShown(final boolean shown,
                                                final boolean animate) {
        if (shown)
            hide(progressBar).hide(emptyView).fadeIn(gridView, animate).show(gridView);
        else
            hide(progressBar).hide(gridView).fadeIn(emptyView, animate)
                    .show(emptyView);

        return this;
    }
}
