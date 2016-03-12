package com.jiujie8.choice.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.view.GridView;
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
 * Created by wlanjie on 14-7-25.
 */
public abstract class PullToRefreshItemGridFragment<E extends Response> extends DialogFragment implements LoaderManager.LoaderCallbacks<E>, OnRefreshListener {

    @InjectView(R.id.refresh_list)
    protected GridView gridView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectView(R.id.ptr_layout)
    protected PullToRefreshLayout refreshLayout;

    @Inject
    protected Activity activity;

    @InjectView(R.id.list_empty_layout)
    private View emptyView;

    private boolean showList;

    private AbsListView.OnScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), null);
    }

    private int getContentView() {
        return R.layout.grid_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    onGridItemClick((GridView) parent, view, position, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
                .useViewDelegate(android.widget.GridView.class, new AbsListViewDelegate())
                .setup(refreshLayout);

    }

    public void onGridItemClick(GridView g, View v, int position, long id) {

    }

    @Override
    public void onRefreshStarted(View view) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showList = false;
        emptyView = null;
        progressBar = null;
        gridView = null;
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gridView != null) {
            gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
    }

    protected abstract Request<E> createRequest();

    protected void refresh() {
        if (!isUsable())
            return;
        getLoaderManager().restartLoader(0, null, this);
    }

    public PullToRefreshItemGridFragment<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    /**
     * hide view
     * @param view
     * @return
     */
    protected PullToRefreshItemGridFragment<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * show view
     * @param view
     * @return
     */
    protected PullToRefreshItemGridFragment<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    public PullToRefreshItemGridFragment<E> setListShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return this;
        }

        if (shown) {
            hide(progressBar).hide(emptyView).fadeIn(gridView, animate).show(gridView);
        } else {
            hide(gridView).hide(progressBar).fadeIn(emptyView, animate).show(emptyView);
        }
        return this;
    }

    /**
     * view fadeIn animation
     * @param view
     * @param animate
     * @return
     */
    private PullToRefreshItemGridFragment<E> fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    public GridView getGridView() {
        return  gridView;
    }

    protected void showList() {
        setListShown(true, isResumed());
    }

    protected void showError(final Exception e, final int defaultMessage) {
        ToastUtils.show(getActivity(), e, defaultMessage);
    }

    protected Exception getException(final Loader<E> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<E>) loader).clearException();
        } else {
            return null;
        }
    }

    @Override
    public Loader<E> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<E>(getActivity()) {
            @Override
            public E loadData() throws Exception {
                return (E) HttpUtils.doRequest(createRequest()).result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<E> loader, E data) {
        refreshLayout.setRefreshComplete();
        if (!isUsable())
            return;
        Exception exception = getException(loader);
        if (exception != null) {
            showError(exception, R.string.loading_error);
            showList();
            return;
        }
        if ("0000".equals(data.getCode())) {
            onLoadFinishedCallback(data);
        } else {
            ToastUtils.show(getActivity(), data.getMessage());
        }
        showList();
    }

    /**
     * Load Finished Callback
     * @param data
     */
    protected abstract void onLoadFinishedCallback(E data);

    @Override
    public void onLoaderReset(Loader<E> loader) {

    }
}
