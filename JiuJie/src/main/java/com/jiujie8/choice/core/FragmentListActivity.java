package com.jiujie8.choice.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.http.HttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.Collections;
import java.util.List;

import roboguice.inject.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class FragmentListActivity<E extends Response> extends PagerActivity implements LoaderManager.LoaderCallbacks<E>, OnRefreshListener {


    private static final String FORCE_REFRESH = "forceRefresh";

    /**
     * @param args
     *            bundle passed to the loader by the LoaderManager
     * @return true if the bundle indicates a requested forced refresh of the
     *         items
     */
    protected static boolean isForceRefresh(Bundle args) {
        return args != null && args.getBoolean(FORCE_REFRESH, false);
    }

    /**
     * Refresh Layout
     */
    @InjectView(R.id.ptr_layout)
    protected PullToRefreshLayout refreshLayout;

    /**
     * List view
     */
    @InjectView(R.id.refresh_list)
    protected ListView listView;

    /**
     * Progress bar
     */
    @InjectView(R.id.pb_loading)
    protected ProgressBar progressBar;

    @InjectView(R.id.list_empty_layout)
    protected View emptyView;

    /**
     * Is the list currently shown?
     */
    protected boolean showList = false;

    /**
     * Is Last Item Visible
     */
    private boolean mLastItemVisible = false;

    /**
     * List footer loading
     */
    protected ResourceLoadingIndicator loadingIndicator;

    protected AbsListView.OnScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onListItemClick((ListView) parent, view, position, id);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                return onListItemLongClick((ListView) parent, view, position,
                        id);
            }
        });

        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
                .useViewDelegate(ListView.class, new AbsListViewDelegate())
                .setup(refreshLayout);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    /**
     * Last Item Visible
     */
    public void onLastItemVisible() {

    }

    /**
     * Refresh
     * @param view
     */
    @Override
    public void onRefreshStarted(View view) {

    }

    /**
     * Create Request
     * @return
     */
    protected abstract Request<E> createRequest();

    /**
     * This activity content view
     * @return
     */
    protected int getContentView() {
        return R.layout.list_item;
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroy() {
        showList = false;
        emptyView = null;
        progressBar = null;
        listView = null;

        super.onDestroy();
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listView != null) {
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
            listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, mScrollListener));
        }
    }

    /**
     * Force a refresh of the items displayed ignoring any cached items
     */
    protected void forceRefresh() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(FORCE_REFRESH, true);
        refresh(bundle);
    }

    /**
     * Refresh the fragment's list
     */
    public void refresh() {
        refresh(null);
    }

    private void refresh(final Bundle args) {
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        refreshLayout.setRefreshComplete();
        setSupportProgressBarIndeterminateVisibility(false);
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

    /**
     * Load Finished Callback
     * @param data
     */
    protected abstract void onLoadFinishedCallback(E data);

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, true);
    }

    @Override
    public Loader<E> onCreateLoader(int id, Bundle bundle) {
        return new ThrowableLoader<E>(this) {
            @Override
            public E loadData() throws Exception {
                return (E) HttpUtils.doRequest(createRequest()).result;
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<E> loader) {

    }

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

    /**
     * Get {@link android.widget.ListView}
     *
     * @return listView
     */
    public ListView getListView() {
        return listView;
    }

    private FragmentListActivity<E> fadeIn(final View view, final boolean animate) {
        if (view != null)
            if (animate)
                view.startAnimation(AnimationUtils.loadAnimation(this,
                        android.R.anim.fade_in));
            else
                view.clearAnimation();
        return this;
    }

    public FragmentListActivity<E> show(final View view) {
        super.show(view);
        return this;
    }

    public FragmentListActivity<E> hide(final View view) {
        super.hide(view);
        return this;
    }

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @return this fragment
     */
    public FragmentListActivity<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @param animate
     * @return this fragment
     */
    public FragmentListActivity<E> setListShown(final boolean shown,
                                            final boolean animate) {
        if (showList)
            hide(progressBar).hide(emptyView).fadeIn(listView, animate).show(listView);
        else
            hide(progressBar).hide(listView).fadeIn(emptyView, animate)
                    .show(emptyView);

        return this;
    }

    /**
     * Callback when a list view item is clicked
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    /**
     * Callback when a list view item is clicked and held
     *
     * @param l
     * @param v
     * @param position
     * @param id
     * @return true if the callback consumed the long click, false otherwise
     */
    public boolean onListItemLongClick(ListView l, View v, int position, long id) {
        return false;
    }
}
