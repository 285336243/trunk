package com.socialtv.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.socialtv.R;
import com.socialtv.http.HttpUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

/**
 * Created by wlanjie on 14-2-18.
 */
public abstract class FragmentListActivity<E> extends PagerActivity implements LoaderManager.LoaderCallbacks<E>, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {


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
     * List items provided to {@link #onLoadFinished(android.support.v4.content.Loader, Object)}}
     */
    protected List<?> items = Collections.emptyList();

    /**
     * List view
     */
    protected ListView listView;

    /**
     * Empty view
     */
    protected TextView emptyView;

    /**
     * Progress bar
     */
    protected ProgressBar progressBar;

    /**
     * Is the list currently shown?
     */
    protected boolean listShown;

    /**
     * List footer loading
     */
    protected ResourceLoadingIndicator loadingIndicator;

    protected PullToRefreshListView refreshListView;

    protected AbsListView.OnScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        if (!items.isEmpty()) {
            setListShown(true, false);
        }
        refreshListView = (PullToRefreshListView) findViewById(R.id.refresh_list);
        listView = refreshListView.getRefreshableView();
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
        refreshListView.setOnRefreshListener(this);
        refreshListView.setOnLastItemVisibleListener(this);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);

        emptyView = (TextView) findViewById(android.R.id.empty);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onLastItemVisible() {

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
        listShown = false;
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
        if(refreshListView != null) {
            refreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
        MobclickAgent.onResume(this);
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
        setSupportProgressBarIndeterminateVisibility(true);
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    /**
     * Show more events while retaining the current pager state
     */
    private void showMore() {
        refresh();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    protected abstract int getErrorMessage(Exception exception);

    protected int getNetWorkErrorMessage() {
        return R.string.network_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    protected abstract int getLoadingMessage();

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        refreshListView.onRefreshComplete();
        setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(loader);
        if (exception != null) {
//            showError(exception, getErrorMessage(exception));
            showError(exception, getNetWorkErrorMessage());
            showList();
            return;
        }
    }

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
     * Refresh the list with the progress bar showing
     */
    protected void refreshWithProgress() {
        items.clear();
        setListShown(false);
        refresh();
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
//        ViewUtils.setGone(view, false);
        super.show(view);
        return this;
    }

    public FragmentListActivity<E> hide(final View view) {
        super.hide(view);
//        ViewUtils.setGone(view, true);
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
        listShown = shown;

        if (shown)
            if (!items.isEmpty())
                hide(progressBar).hide(emptyView).fadeIn(listView, animate)
                        .show(refreshListView).show(listView);
            else
//                hide(progressBar).hide(listView).fadeIn(emptyView, animate)
//                        .show(emptyView);
                hide(progressBar).show(refreshListView).show(listView);
        else
            hide(listView).hide(emptyView).fadeIn(progressBar, animate)
                    .show(progressBar);

        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param message
     * @return this fragment
     */
    protected FragmentListActivity<E> setEmptyText(final String message) {
        if (emptyView != null)
            emptyView.setText(message);
        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param resId
     * @return this fragment
     */
    protected FragmentListActivity<E> setEmptyText(final int resId) {
        if (emptyView != null)
            emptyView.setText(resId);
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
