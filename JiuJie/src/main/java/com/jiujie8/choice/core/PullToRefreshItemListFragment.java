package com.jiujie8.choice.core;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.BasicNetwork;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.http.BaseRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.view.RoundProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import roboguice.inject.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

/**
 * Created by wlanjie on 14-2-14.
 */
public abstract class PullToRefreshItemListFragment<E extends Response> extends DialogFragment implements LoaderManager.LoaderCallbacks<E>, OnRefreshListener{

    protected int loaderId;

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

    @InjectView(R.id.ptr_layout)
    protected PullToRefreshLayout refreshLayout;

    /**
     * List view
     */
    @InjectView(R.id.refresh_list)
    protected ListView listView;

    /**
     * List footer loading
     */
    protected ResourceLoadingIndicator loadingIndicator;

    /**
     * Progress bar
     */
//    @InjectView(R.id.pb_loading)
//    protected ProgressBar progressBar;

    @InjectView(R.id.refresh_list_progredss_bar)
    private RoundProgressBar progressBar;

    /**
     * Empty View
     */
    @InjectView(R.id.refresh_list_empty_layout)
    private View emptyView;

    /**
     * Is the list currently shown?
     */
    protected boolean showList = false;

    private AbsListView.OnScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), null);
    }

    /**
     * This activity content view
     * @return
     */
    protected int getContentView() {
        return R.layout.list_item;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showList = false;
        emptyView = null;
        progressBar = null;
        listView = null;
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    /**
     * Last Item Visible
     */
    public void onLastItemVisible() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(listView != null) {
            listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, scrollListener));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderId = (int) System.currentTimeMillis();
        getLoaderManager().initLoader(loaderId, null, this);
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

        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
//                .useViewDelegate(ListView.class, new AbsListViewDelegate())
                .setup(refreshLayout);


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

        getLoaderManager().restartLoader(loaderId, args, this);
    }

    /**
     * Set list shown or progress bar show
     * @param shown
     * @return this fragment
     */
    public PullToRefreshItemListFragment<E> setListShown(final boolean shown) {
        return setListShown(shown, true);
    }

    /**
     * Callback when a list view item is clicked
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
     * @param shown
     * @param animate
     * @return
     */
    public PullToRefreshItemListFragment<E> setListShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return this;
        }

        if (shown) {
            hide(progressBar).hide(emptyView).fadeIn(listView, animate).show(listView);
        } else {
            hide(listView).hide(progressBar).fadeIn(emptyView, animate).show(emptyView);
        }
        return this;
    }

    /**
     * hide view
     * @param view
     * @return
     */
    protected PullToRefreshItemListFragment<E> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * show view
     * @param view
     * @return
     */
    protected PullToRefreshItemListFragment<E> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    /**
     * view fadeIn animation
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
     * @return
     */
    public ListView getListView() {
        return listView;
    }

    @Override
    public void onLoadFinished(Loader<E> listLoader, E data) {
        refreshLayout.setRefreshComplete();
        if (!isUsable()) {
            return;
        }
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(listLoader);
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
    public Loader<E> onCreateLoader(int id, Bundle bundle) {
        return new ThrowableLoader<E>(getActivity()) {
            @Override
            public E loadData() throws Exception {
                final Request<E> mRequest = createRequest();
                if (mRequest != null && mRequest instanceof BaseRequest) {
                    ((BaseRequest) mRequest).setOnProgressListener(new BasicNetwork.OnProgressListener() {
                        @Override
                        public void onProgress(long current, long total) {
                            progressBar.setMax((int)total);
                            progressBar.setProgress((int)current);
                        }
                    });
                }
                return (E) HttpUtils.doRequest(mRequest).result;
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<E> listLoader) {

    }

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, isResumed());
    }

    /**
     * Show exception in a {@link android.widget.Toast}
     * @param e
     * @param defaultMessage
     */
    protected void showError(final Exception e, final int defaultMessage) {
        ToastUtils.show(getActivity(), e, defaultMessage);
    }

    /**
     * Get exception from loader if it provides one by being a
     * @param loader
     * @return
     */
    protected Exception getException(final Loader<E> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<E>) loader).clearException();
        } else {
            return null;
        }
    }
}
