package com.socialtv.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.socialtv.R;
import com.socialtv.http.HttpUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-9-19.
 */
public abstract class FragmentGridActivity<E> extends PagerActivity implements PullToRefreshBase.OnRefreshListener<GridView>, PullToRefreshBase.OnLastItemVisibleListener, LoaderManager.LoaderCallbacks<E> {

    @InjectView(R.id.refresh_list)
    protected PullToRefreshGridView refreshGridView;

    @InjectView(R.id.pb_loading)
    protected ProgressBar progressBar;

    @InjectView(R.id.list_empty_layout)
    protected View emptyView;

    @InjectView(R.id.list_empty_text)
    protected TextView emptyTextView;

    protected GridView gridView;

    protected AbsListView.OnScrollListener scrollListener;

    /**
     * List items provided to {@link #onLoadFinished(android.support.v4.content.Loader, Object)}}
     */
    protected List<?> items = Collections.emptyList();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getContentView());

        gridView = refreshGridView.getRefreshableView();
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

        refreshGridView.setOnRefreshListener(this);
        refreshGridView.setOnLastItemVisibleListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
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

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {

    }

    @Override
    public void onLastItemVisible() {

    }

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

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshGridView != null) {
            refreshGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
        MobclickAgent.onResume(this);
    }

    protected void refresh() {
        refresh(null);
    }

    private void refresh(Bundle args) {
        setSupportProgressBarIndeterminateVisibility(true);
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    protected abstract int getLoadingMessage();

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    protected abstract int getErrorMessage(Exception exception);

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
        refreshGridView.onRefreshComplete();
        setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(loader);
        if (exception != null) {
            showError(exception, getErrorMessage(exception));
            showList();
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<E> eLoader) {

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
            if (!items.isEmpty())
                hide(progressBar).hide(emptyView).fadeIn(gridView, animate)
                        .show(refreshGridView).show(gridView);
            else
                hide(progressBar).hide(gridView).fadeIn(emptyView, animate)
                        .show(emptyView);
        else
            hide(gridView).hide(emptyView).fadeIn(progressBar, animate)
                    .show(progressBar);

        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param message
     * @return this fragment
     */
    protected FragmentGridActivity<E> setEmptyText(final String message) {
        if (emptyTextView != null)
            emptyTextView.setText(message);
        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param resId
     * @return this fragment
     */
    protected FragmentGridActivity<E> setEmptyText(final int resId) {
        if (emptyTextView != null)
            emptyTextView.setText(resId);
        return this;
    }

    @Override
    protected FragmentProvider getProvider() {
        return null;
    }
}
