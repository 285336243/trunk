package com.shengzhish.xyj.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.util.IConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.List;

/**
 * Created by wlanjie on 14-2-14.
 */
public abstract class PullToRefreshItemListFragment<E> extends DialogFragment implements LoaderManager.LoaderCallbacks<E>, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {

    protected int requestPage = 1;

    protected int requestCount = 20;

    private static final String FORCE_REFRESH = "forceRefresh";

    /**
     * @param args bundle passed to the loader by the LoaderManager
     * @return true if the bundle indicates a requested forced refresh of the
     * items
     */
    protected static boolean isForceRefresh(Bundle args) {
        return args != null && args.getBoolean(FORCE_REFRESH, false);
    }

    /**
     * List items provided to {@link #onLoadFinished(android.support.v4.content.Loader, Object)} (Loader, List)}
     */
    protected List<?> items = Collections.emptyList();

    protected PullToRefreshListView refreshListView;

    /**
     * List view
     */
    protected ListView listView;

    /**
     * Empty view
     */
    protected TextView emptyView;

    /**
     * title view
     */
    protected TextView titleView;

    /**
     * List footer loading
     */
    protected ResourceLoadingIndicator loadingIndicator;

    /**
     * Progress bar
     */
    protected ProgressBar progressBar;

    /**
     * Is the list currently shown?
     */
    protected boolean listShown;
    protected boolean isRefresh = false;

    protected E e;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!items.isEmpty()) {
            setListShown(true, false);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), null);
    }

    /**
     * This activity content view
     *
     * @return
     */
    protected int getContentView() {
        return R.layout.list_item;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listShown = false;
        emptyView = null;
        progressBar = null;
        listView = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshListView != null) {
            refreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        }
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshListView = (PullToRefreshListView) view.findViewById(R.id.refresh_list);
        refreshListView.setOnRefreshListener(this);
        refreshListView.setOnLastItemVisibleListener(this);
        listView = refreshListView.getRefreshableView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                onListItemClick((ListView) adapterView, view, position, id);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                return onListItemLongClick((ListView) adapterView, view, position, id);
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        titleView = (TextView) view.findViewById(R.id.tv_title);
    }

    /**
     * Create Request
     *
     * @return
     */
    protected abstract Request<E> createRequest();

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
        if (!isUsable())
            return;

        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        getLoaderManager().restartLoader(0, args, this);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    protected abstract int getErrorMessage(Exception exception);

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @return this fragment
     */
    public PullToRefreshItemListFragment<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    /**
     * Set empty text on list fragment
     *
     * @param message
     * @return this fragment
     */
    protected PullToRefreshItemListFragment<E> setEmptyText(final String message) {
        if (emptyView != null) {
            emptyView.setText(message);
        }
        return this;
    }

    /**
     * Set empty text on list fragment
     *
     * @param resId
     * @return
     */
    protected PullToRefreshItemListFragment<E> setEmptyText(final int resId) {
        if (emptyView != null) {
            emptyView.setText(resId);
        }
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

    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @param animate
     * @return
     */
    public PullToRefreshItemListFragment<E> setListShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return this;
        }

        if (shown == listShown) {
            if (shown) {
                if (items.isEmpty()) {
                    hide(listView).show(emptyView);
                } else {
                    hide(emptyView).show(listView);
                }
                return this;
            }
        }

        listShown = shown;
        if (shown) {
            if (!items.isEmpty()) {
                hide(progressBar).hide(emptyView).fadeIn(listView, animate).show(refreshListView).show(listView);
            } else {
                hide(listView).hide(progressBar).fadeIn(emptyView, animate).show(emptyView);
            }
        } else {
            hide(listView).hide(emptyView).fadeIn(progressBar, animate).show(progressBar);
        }
        return this;
    }

    /**
     * hide view
     *
     * @param view
     * @return
     */
    private PullToRefreshItemListFragment<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * show view
     *
     * @param view
     * @return
     */
    private PullToRefreshItemListFragment<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    /**
     * view fadeIn animation
     *
     * @param view
     * @param animate
     * @return
     */
    private PullToRefreshItemListFragment<E> fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    /**
     * Get {@link android.widget.ListView}
     *
     * @return
     */
    public ListView getListView() {
        return listView;
    }

    @Override
    public void onLoadFinished(Loader<E> listLoader, E data) {
        refreshListView.onRefreshComplete();
        if (!isUsable()) {
            return;
        }
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(listLoader);
        if (exception != null) {
            showError(exception, getErrorMessage(exception));
            showList();
            return;
        }
    }

    @Override
    public Loader<E> onCreateLoader(int id, Bundle bundle) {
        return new ThrowableLoader<E>(getActivity(), e) {
            @Override
            public E loadData() throws Exception {
                return (E) HttpUtils.doRequest(createRequest()).result;
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<E> listLoader) {

    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    protected abstract int getLoadingMessage();

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, isResumed());
    }

    /**
     * Show exception in a {@link android.widget.Toast}
     *
     * @param e
     * @param defaultMessage
     */
    protected void showError(final Exception e, final int defaultMessage) {
        if(defaultMessage== IConstant.CACHE){
            return;
        }
        ToastUtils.show(getActivity(), e, defaultMessage);
    }

    /**
     * Get exception from loader if it provides one by being a
     *
     * @param loader
     * @return
     */
    protected Exception getException(final Loader<E> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<List<E>>) loader).clearException();
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
}
