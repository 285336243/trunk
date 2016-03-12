package com.socialtv.core;

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
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.socialtv.R;
import com.socialtv.http.HttpUtils;
import com.socialtv.program.entity.ProgramNews;
import com.socialtv.view.GridView;
import com.socialtv.view.PullToRefreshGridView;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-25.
 */
public abstract class PullToRefreshItemGridFragment<E> extends DialogFragment implements LoaderManager.LoaderCallbacks<ProgramNews>,PullToRefreshBase.OnLastItemVisibleListener, PullToRefreshBase.OnRefreshListener<GridView> {
    protected int requestPage = 1;

    protected int requestCount = 20;

    protected boolean hasMore = false;

    @InjectView(R.id.refresh_list)
    protected PullToRefreshGridView refreshGridView;

    protected GridView gridView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectView(android.R.id.empty)
    private TextView emptyTextView;

    @Inject
    protected Activity activity;

    @InjectView(R.id.list_empty_layout)
    private View emptyView;

    private boolean listShown;

    protected List<?> items = Collections.emptyList();

    private AbsListView.OnScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), null);
    }

    private int getContentView() {
        return R.layout.grid_item;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshGridView.setOnLastItemVisibleListener(this);
        refreshGridView.setOnRefreshListener(this);
        gridView = refreshGridView.getRefreshableView();
        if (isAddAdapterHeader())
            gridView.addHeaderView(adapterHeaderView());
        gridView.setAdapter(createAdapter());
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
    }

    public void onGridItemClick(GridView g, View v, int position, long id) {

    }

    protected abstract boolean isAddAdapterHeader();

    protected abstract View adapterHeaderView();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!items.isEmpty()) {
            setListShown(true, false);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listShown = false;
        emptyView = null;
        progressBar = null;
        refreshGridView = null;
        gridView = null;
        items.clear();
        items = null;
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshGridView != null) {
            refreshGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
        MobclickAgent.onResume(getActivity());
    }

    protected abstract SingleTypeAdapter<?> createAdapter();

    protected abstract Request<ProgramNews> createRequest();

    protected void refresh() {
        if (!isUsable())
            return;
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        getLoaderManager().restartLoader(0, null, this);
    }

    protected abstract int getErrorMessage(Exception exception);

    protected abstract int getLoadingMessage();

    public PullToRefreshItemGridFragment<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    public PullToRefreshItemGridFragment<E> setEmptyText(final int resId) {
        if (emptyTextView != null) {
            emptyTextView.setText(resId);
        }
        return this;
    }

    public PullToRefreshItemGridFragment<E> setEmptyText(final String text) {
        if (emptyTextView != null) {
            emptyTextView.setText(text);
        }
        return this;
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

        if (shown == listShown) {
            if (shown) {
                if (items.isEmpty()) {
                    hide(gridView).show(emptyView);
                } else {
                    hide(emptyView).show(gridView);
                }
                return this;
            }
        }

        listShown = shown;
        if (shown) {
            if (!items.isEmpty()) {
                hide(progressBar).hide(emptyView).fadeIn(gridView, animate).show(refreshGridView).show(gridView);
            } else {
                hide(gridView).hide(progressBar).fadeIn(emptyView, animate).show(emptyView);
            }
        } else {
            hide(gridView).hide(emptyView).fadeIn(progressBar, animate).show(progressBar);
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

    protected Exception getException(final Loader<ProgramNews> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<ProgramNews>) loader).clearException();
        } else {
            return null;
        }
    }

    @Override
    public Loader<ProgramNews> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<ProgramNews>(getActivity()) {
            @Override
            public ProgramNews loadData() throws Exception {
                return (ProgramNews) HttpUtils.doRequest(createRequest()).result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ProgramNews> loader, ProgramNews data) {
        refreshGridView.onRefreshComplete();
        if (!isUsable())
            return;
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(loader);
        if (exception != null) {
            showError(exception, getErrorMessage(exception));
            showList();
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<ProgramNews> loader) {

    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {

    }
}
