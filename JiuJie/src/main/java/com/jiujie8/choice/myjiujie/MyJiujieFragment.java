package com.jiujie8.choice.myjiujie;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.BasicNetwork;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.DialogFragment;
import com.jiujie8.choice.core.HeaderFooterListAdapter;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.core.ResourceLoadingIndicator;
import com.jiujie8.choice.core.ThrowableLoader;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.home.HomeAdapter;
import com.jiujie8.choice.home.HomeServices;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.Post;
import com.jiujie8.choice.home.entity.PostList;
import com.jiujie8.choice.http.BaseRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.persioncenter.otheruser.OtherUserCenterActivity;
import com.jiujie8.choice.util.ChoiceTypeUtil;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.NetWorkUtil;
import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by wlanjie on 14/12/4.
 */
public class MyJiujieFragment extends DialogFragment implements OnRefreshListener {

    private final static int LOADER_ID = (int) System.currentTimeMillis();

    private boolean isRefresh = false;

    private boolean hasMore = false;

    protected PullToRefreshLayout refreshLayout;

    /**
     * List view
     */
    protected ListView listView;

    /**
     * List footer loading
     */
    protected ResourceLoadingIndicator loadingIndicator;

    /**
     * Progress bar
     */
    private ProgressBar progressBar;

    /**
     * Empty View
     */
    private View emptyView;

    /**
     * Is the list currently shown?
     */
    protected boolean showList = false;

    /**
     * Is Last Item Visible
     */
    private boolean mLastItemVisible = false;

    private ChoiceMode mMode;


    private HomeAdapter mAdapter;

    @Inject
    private Activity mActivity;

    private List<Post> mItems = null;

    private ChoiceTypeUtil.ItemInfo mItemInfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            mMode = (ChoiceMode) bundle.getSerializable(IConstant.MODE_ITEM);
            mAdapter = new HomeAdapter(activity, mMode);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_item, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        listView = (ListView) view.findViewById(R.id.refresh_list);
        View mFooterView = new View(mActivity);
        //mToolbarView为下面button布局的高度
/*        final int height = ((JiuJieDetailActivity) getActivity()).mToolbarView.getMeasuredHeight();
        mFooterView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height));
        listView.addFooterView(mFooterView);*/
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        emptyView = view.findViewById(R.id.refresh_list_empty_layout);

        mItems = new ArrayList<Post>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                onListItemClick((ListView) adapterView, view, position, id);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//                return onListItemLongClick((ListView) adapterView, view, position, id);
                return false;
            }
        });

        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
//                .useViewDelegate(ListView.class, new AbsListViewDelegate())
                .setup(refreshLayout);
        loadingIndicator = new ResourceLoadingIndicator(mActivity);
        getLoaderManager().initLoader(LOADER_ID, null, mCallback);
        listView.setAdapter(createListAdapter());
        loadingIndicator.setList(getListAdapter());

    }

    @Override
    public void onRefreshStarted(View view) {
        isRefresh = true;
        hasMore = false;
        requestPage = 1;
        if (!loadingIndicator.getVisible())
            loadingIndicator.setVisible(true);
        refresh();
    }

    public void onLastItemVisible() {
        isRefresh = false;
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();

    }

    LoaderManager.LoaderCallbacks<PostList> mCallback = new LoaderManager.LoaderCallbacks<PostList>() {
        @Override
        public Loader<PostList> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<PostList>(mActivity) {
                @Override
                public PostList loadData() throws Exception {
                    final Request<PostList> mRequest = createRequest();
                    if (mRequest != null && mRequest instanceof BaseRequest) {
                        ((BaseRequest) mRequest).setOnProgressListener(new BasicNetwork.OnProgressListener() {
                            @Override
                            public void onProgress(long current, long total) {
                                if (progressBar != null) {
                                    progressBar.setMax((int) total);
                                    progressBar.setProgress((int) current);
                                }
                            }
                        });
                    }
                    return (PostList) HttpUtils.doRequest(mRequest).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<PostList> loader, PostList data) {
            refreshLayout.setRefreshComplete();
            if (!isUsable()) {
                return;
            }
            if (isRefresh && NetWorkUtil.isNetworkConnected(getActivity()))
                mAdapter.clear();
            getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
            Exception exception = getException(loader);
            if (exception != null) {
                showError(exception, R.string.loading_error);
                loadingIndicator.setVisible(false);
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

        @Override
        public void onLoaderReset(Loader<PostList> loader) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (listView != null) {
            AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int state) {
                    if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
                        onLastItemVisible();
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
                }
            };
            listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, mScrollListener));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(LOADER_ID);
        showList = false;
        emptyView = null;
        progressBar = null;
        listView = null;
        mItems.clear();
        mItems = null;
    }

    public ListView getListView() {
        return listView;
    }

    public ChoiceTypeUtil.ItemInfo getItemInfo() {
        return mItemInfo;
    }

    /**
     * create adapter to display items
     * @return
     */
    protected HeaderFooterListAdapter<MultiTypeAdapter> createListAdapter() {
        MultiTypeAdapter wrapped = createAdapter();
        HeaderFooterListAdapter<MultiTypeAdapter> adapter = new HeaderFooterListAdapter<MultiTypeAdapter>(listView, wrapped);
        if (isAddAdapterHeader()) {
            adapter.addHeader(adapterHeaderView());
        }
        return adapter;
    }

    /**
     * Get list adapter
     * @return
     */
    protected HeaderFooterListAdapter<MultiTypeAdapter> getListAdapter() {
        if (listView != null) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter instanceof HeaderFooterListAdapter) {
                return (HeaderFooterListAdapter<MultiTypeAdapter>) listAdapter;
            } else {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
                return  (HeaderFooterListAdapter<MultiTypeAdapter>) headerViewListAdapter.getWrappedAdapter();
            }
        } else {
            return null;
        }
    }

    /**
     * Set list shown or progress bar show
     * @param shown
     * @param animate
     * @return
     */
    public void setListShown(final boolean shown, final boolean animate) {
        if (!isUsable()) {
            return;
        }

        if (shown) {
            hide(progressBar).hide(emptyView).fadeIn(listView, animate).show(listView);
        } else {
            hide(listView).hide(progressBar).fadeIn(emptyView, animate).show(emptyView);
        }
    }

    private MyJiujieFragment hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    private MyJiujieFragment show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    private MyJiujieFragment fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    public void refreshList() {
        requestPage = 1;
         mItems.clear();
        isRefresh = true;
        if (mItemInfo != null) {
            final long currenCount = Long.valueOf(mItemInfo.commentView.getText().toString()) + 1;
            mItemInfo.commentView.setText(String.valueOf(currenCount));
        }
        refresh();
    }

    private void refresh() {
//        if (!isUsable()) {

            getLoaderManager().restartLoader(LOADER_ID, null, mCallback);
//        }
    }

    protected boolean isAddAdapterHeader() {
        return true;
    }

    protected View adapterHeaderView() {
        if (mMode == null) {
            return null;
        }
        mItemInfo = ChoiceTypeUtil.showType(getActivity(), mMode);
/*        if (mItemInfo.avatarLayout != null) {
            mItemInfo.avatarLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (LoginUtil.isLogin()) {
//                        startActivity(new Intents(getActivity(), OtherUserCenterActivity.class).add(IConstant.USER_ID, mMode.getChoice().getUser().getId()).toIntent());
//                    } else {
//                        startActivity(new Intents(getActivity(), LoginActivity.class).toIntent());
//                    }
                }
            });
        }*/
        return mItemInfo.headerView;
    }

    protected MultiTypeAdapter createAdapter() {
        return mAdapter;
    }

    protected Request createRequest() {
        final Map<String, String> map = new HashMap<String, String>();
        if (mMode != null) {
            final ChoiceItem mItem = mMode.getChoice();
            if (mItem != null) {
                map.put("page", requestPage + "");
                map.put("count", requestCount + "");
                map.put("choiceId", mItem.getId() + "");
            }
        }
        return HomeServices.createHomePostRequest(map);
    }

    protected void onLoadFinishedCallback(PostList data) {
        if (data != null) {
            final List<Post> mItems = data.getRs();
            if (mItems != null && !mItems.isEmpty()) {
                this.mItems.addAll(mItems);
                mAdapter.addItems(mItems);
                if (!mMode.isAnimatorRunning()) {
                    startPieGraphAnimator();
                }
            }
            if (mItems.size() < requestCount) {
                loadingIndicator.setVisible(false);
                hasMore = true;
            }
        }
    }

    private void startPieGraphAnimator() {
        if (mItemInfo != null && mItemInfo.pieGraph != null && mItemInfo.pieGraph.getVisibility() == View.VISIBLE && !mMode.isAnimatorRunning()) {
            mItemInfo.pieGraph.setDuration(1000);//default if unspecified is 300 ms
            mItemInfo.pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
            mItemInfo.pieGraph.setAnimationListener(getAnimatorListener());
            mItemInfo.pieGraph.animateToGoalValues();
        }
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
    protected Exception getException(final Loader<PostList> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<PostList>) loader).clearException();
        } else {
            return null;
        }
    }

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true, isResumed());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mMode != null) {
                if (listView != null && listView.getVisibility() == View.VISIBLE) {
                    startPieGraphAnimator();
                }
            }
        }
    }

    private Animator.AnimatorListener getAnimatorListener() {
        return new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animator) {
                if (mMode != null) {
                    mMode.setAnimatorRunning(true);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
    }
}
