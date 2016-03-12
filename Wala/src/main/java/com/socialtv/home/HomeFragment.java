package com.socialtv.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.melot.meshow.room.RoomLauncher;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.socialtv.R;
import com.socialtv.activity.ActivityDetailActivity;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.HeaderFooterListAdapter;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiTypeRefreshListFragment;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ResourceLoadingIndicator;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.home.entity.Banner;
import com.socialtv.home.entity.Entries;
import com.socialtv.home.entity.Home;
import com.socialtv.home.entity.HomeRecommand;
import com.socialtv.home.entity.Search;
import com.socialtv.http.HttpUtils;
import com.socialtv.mzs.LuckyDipActivity;
import com.socialtv.mzs.RobTicketActivity;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.personcenter.entity.UpdateResponse;
import com.socialtv.program.ProgramActivity;
import com.socialtv.publicentity.User;
import com.socialtv.services.NotificationDownloadService;
import com.socialtv.shop.ShopDetailsActivity;
import com.socialtv.star.StarActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.MD5Util;
import com.socialtv.util.StartOtherFeedUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

/**
 * Created by wlanjie on 14-6-22.
 * 发现的Fragment
 */
public class HomeFragment extends MultiTypeRefreshListFragment<Home> {

    private final static int LOGIN = 100;

    private final static int SCAN = 200;

    private final static int SEARCH_LOADER_ID = 300;

    @Inject
    private LayoutInflater inflater;

    @Inject
    private HomeServices services;

    @Inject
    private BannerAdapter bannerAdapter;

    @Inject
    private Activity activity;

    @Inject
    private SearchAdapter searchAdapter;

    private ViewPager pager;

    private CirclePageIndicator indicator;

    private int width;

    private int currentItem = 0;

    private final Handler timerHandler = new Handler();

    private DefineBroadcastReceiver mReceiver;

    private Dialog recommandDialog;

    private View recommandImageLayout;

    private ImageView recommandImage;

    private ImageButton recommandCloseButton;

    //search dialog
    private Dialog searchDialog;

    private PullToRefreshListView searchRefreshListView;

    private AutoCompleteTextView searchEditText;

    private TextView cancelAndSearchText;

    private View editView;

    private boolean searchHasMore = false;

    private ResourceLoadingIndicator searchLoadingIndicator;

    private ProgressBar searchProgressBar;

    private View searchEmptyView;

    private TextView searchEmptyText;

    private boolean isRecommandDialog = false;

    private Home home = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmptyText(R.string.home_empty);
        hasMore = true;
        if (getActivity() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
        }

        mReceiver = new DefineBroadcastReceiver();
        getActivity().registerReceiver(mReceiver, new IntentFilter(IConstant.USER_LOGIN));
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        final HomeActivity homeActivity = (HomeActivity) activity;
        if (homeActivity.isShowUpdateDialog)
            return;

        new AbstractRoboAsyncTask<UpdateResponse>(activity){
            @Override
            protected UpdateResponse run(Object data) throws Exception {
                return (UpdateResponse) HttpUtils.doRequest(services.createCheckUpdateRequest()).result;
            }

            @Override
            protected void onSuccess(UpdateResponse updateResponse) throws Exception {
                super.onSuccess(updateResponse);
                if (updateResponse != null) {
                    if (updateResponse.getResponseCode() == 0) {
                        int code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
                        if (updateResponse.getVersionCode() > code) {
                            createUpdateDialog(homeActivity, updateResponse.getUpgradeUrl(), updateResponse.getVersionNo(), updateResponse.getUpgradeMsg());
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * 创建升级Dialog
     * @param activity
     * @param updateUrl
     * @param versionNo
     * @param updateMessage
     */
    private void createUpdateDialog(final HomeActivity activity, final String updateUrl, final String versionNo, final String updateMessage) {
        final Dialog mDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.update_version_view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        TextView updateButton = (TextView) mDialog.findViewById(R.id.update_dialog_ok);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NotificationDownloadService.class);
                intent.putExtra("id", 1);
                intent.putExtra("url", updateUrl);
                activity.startService(intent);
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        TextView cancelButton = (TextView) mDialog.findViewById(R.id.update_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.isShowUpdateDialog = true;
            }
        });

        TextView mVersionNoText = (TextView) mDialog.findViewById(R.id.update_version);
        mVersionNoText.setText("版本 : " + versionNo);
        TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
        mUpdateMessageText.setText(updateMessage);
    }

    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 4000);

    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, 4000);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void bindViewPagerGestureDetector(ViewPager mViewPager) {
        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        final View.OnTouchListener mGestureDetectorListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timerHandler.removeCallbacks(timerRunnable);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timerHandler.postDelayed(timerRunnable, 4000);
                }
                return mGestureDetector.onTouchEvent(event);
            }
        };
        mViewPager.setOnTouchListener(mGestureDetectorListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!LoginUtil.isLogin(getActivity())) {
            menu.add(Menu.NONE, LOGIN, 0, "登录").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            menu.add(Menu.NONE, SCAN, 0, "扫一扫").setIcon(R.drawable.btn_scan_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isUsable())
            return false;
        if (item.getItemId() == LOGIN) {
            startActivityForResult(new Intents(getActivity(), LoginActivity.class).toIntent(), 0);
        } else if (item.getItemId() == SCAN) {
            startActivity(new Intents(getActivity(), ScanLogoActivity.class).toIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            HomeActivity activity = (HomeActivity) getActivity();
            activity.showHost();
            activity.supportInvalidateOptionsMenu();
        }
    }

    private class DefineBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (IConstant.USER_LOGIN.equals(intent.getAction())) {
                    HomeActivity activity = (HomeActivity) getActivity();
                    activity.showHost();
                    activity.supportInvalidateOptionsMenu();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        View view = inflater.inflate(R.layout.home_header, null);
        ViewFinder finder = new ViewFinder(view);
        pager = finder.find(R.id.home_header_pager);
        bindViewPagerGestureDetector(pager);
        pager.setAdapter(bannerAdapter);
        indicator = finder.find(R.id.banner_indicator);
        indicator.setViewPager(pager);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, (int) (width * 0.6 + Utils.dip2px(getActivity(), 12)));
        pager.setLayoutParams(params);
        View searchView = view.findViewById(R.id.home_search_layout);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });
        return view;
    }

    /**
     * 搜索Dialog
     */
    private void showSearchDialog() {
        if (searchDialog == null) {
            searchDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            searchDialog.setCanceledOnTouchOutside(false);
            searchDialog.setContentView(R.layout.search_dialog);
            editView = searchDialog.findViewById(R.id.search_edit_layout);
            searchEditText = (AutoCompleteTextView) searchDialog.findViewById(R.id.search_edit);
            cancelAndSearchText = (TextView) searchDialog.findViewById(R.id.search_cancel);
            searchRefreshListView = (PullToRefreshListView) searchDialog.findViewById(R.id.refresh_list);
            searchProgressBar = (ProgressBar) searchDialog.findViewById(R.id.pb_loading);
            searchEmptyView = searchDialog.findViewById(R.id.list_empty_layout);
            searchEmptyText = (TextView) searchDialog.findViewById(R.id.list_empty_text);
            searchEmptyText.setText("没有搜索到任何内容\n用其他关键字试试看");
            searchRefreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
            searchRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
            HeaderFooterListAdapter<MultiTypeAdapter> headerFooterListAdapter = new HeaderFooterListAdapter<MultiTypeAdapter>(searchRefreshListView.getRefreshableView(), searchAdapter);
            searchRefreshListView.setAdapter(headerFooterListAdapter);
            searchLoadingIndicator = new ResourceLoadingIndicator(getActivity(), R.string.please_wait);
            searchLoadingIndicator.setList(headerFooterListAdapter);
            searchRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    if (getLoaderManager().hasRunningLoaders())
                        return;
                    if (searchHasMore)
                        return;
                    requestPage++;
                    getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, searchCallbacks);
                }
            });
            searchRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User user = (User) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(user.getUserId())) {
                        StartOtherFeedUtil.startOtherFeed(getActivity(), user.getUserId());
                    }
                }
            });
            searchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    searchAdapter.clear();
                    searchEditText.setText("");
                    requestPage = 1;
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchEmptyText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        final String content = searchEditText.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            ToastUtils.show(getActivity(), "搜索内容不能为空");
                        } else {
                            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(searchEmptyText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            searchAdapter.clear();
                            searchProgressBar.setVisibility(View.VISIBLE);
                            searchEmptyView.setVisibility(View.GONE);
                            searchRefreshListView.setVisibility(View.GONE);
                            searchLoadingIndicator.setVisible(true);
                            requestPage = 1;
                            searchHasMore = false;
                            if (getLoaderManager().getLoader(SEARCH_LOADER_ID) == null) {
                                getLoaderManager().initLoader(SEARCH_LOADER_ID, null, searchCallbacks);
                            } else {
                                getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, searchCallbacks);
                            }
                        }
                    }
                    return false;
                }
            });
        }
        searchRefreshListView.setVisibility(View.GONE);
        searchLoadingIndicator.setVisible(true);
        if (!searchDialog.isShowing()) {
            searchDialog.show();
        }
//        final ScaleAnimation animation =new ScaleAnimation(1.0f, 0.8f, 1.0f,
//                1.0f, Animation.RELATIVE_TO_SELF, 0f,
//                Animation.RELATIVE_TO_SELF, 1.0f);
//        animation.setDuration(800);
//        animation.setFillAfter(true);
//        editText.startAnimation(animation);
//
        //56是搜索框的高和距离顶部的
        final int y = Utils.dip2px(getActivity(), 56);
//        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f,0.0f, -y);
//        translateAnimation.setDuration(800);
//        translateAnimation.setFillAfter(true);
//        editView.startAnimation(translateAnimation);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(editView, "translationY", 0, -y, -y).setDuration(800), ObjectAnimator.ofFloat(searchEditText, "scaleX", 1.0f, 1.0f, 1.0f).setDuration(800));
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cancelAndSearchText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelAndSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDialog != null && searchDialog.isShowing()) {
                    searchDialog.dismiss();
                }
            }
        });

        new AbstractRoboAsyncTask<Search>(getActivity()){
            @Override
            protected Search run(Object data) throws Exception {
                return (Search) HttpUtils.doRequest(services.createSearchRecommendRequest()).result;
            }

            @Override
            protected void onSuccess(Search search) throws Exception {
                super.onSuccess(search);
                if (search != null) {
                    if (search.getResponseCode() == 0) {
                        searchProgressBar.setVisibility(View.GONE);
                        searchEditText.setEnabled(true);
                        searchHasMore = true;
                        searchAdapter.clear();
                        searchAdapter.addItem(search.getUsers(), "recommend");
                        searchLoadingIndicator.setVisible(false);
                        searchRefreshListView.setVisibility(View.VISIBLE);
                        searchRefreshListView.getRefreshableView().setVisibility(View.VISIBLE);
                    }
                }

            }
        }.execute();
    }

    LoaderManager.LoaderCallbacks<Search> searchCallbacks = new LoaderManager.LoaderCallbacks<Search>() {
        @Override
        public Loader onCreateLoader(int i, Bundle bundle) {
            return new ThrowableLoader<Search>(getActivity()) {
                @Override
                public Search loadData() throws Exception {
                    final String content = searchEditText.getText().toString();
                    return (Search) HttpUtils.doRequest(services.createSearchRequest(content, requestPage, requestCount)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader loader, Search search) {
            if (search != null) {
                if (search.getResponseCode() == 0) {
                    if (search.getUsers() != null && !search.getUsers().isEmpty()) {
                        searchAdapter.addItem(search.getUsers(), "search");
                        if (search.getUsers().size() < requestCount) {
                            searchLoadingIndicator.setVisible(false);
                        }
                        searchRefreshListView.setVisibility(View.VISIBLE);
                        searchRefreshListView.getRefreshableView().setVisibility(View.VISIBLE);
                    } else {
                        searchHasMore = true;
                        searchLoadingIndicator.setVisible(false);
                        if (searchAdapter.getCount() <= 0) {
                            searchEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    ToastUtils.show(getActivity(), search.getResponseMessage());
                }
            }
            searchProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    /**
     * Banner自动滚动的Handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pager.setCurrentItem(msg.what, true);
        }
    };

    /**
     * 控制Banner滚动的Runnable
     */
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (pager.getAdapter().getCount() != 0) {
                currentItem = (pager.getCurrentItem() + 1) % pager.getAdapter().getCount();
                handler.sendEmptyMessage(currentItem);
                timerHandler.postDelayed(this, 4000);
            }
        }
    };

    /**
     * 设置Banner中的内容
     * @param banners
     */
    private void setBannerConent(final List<Banner> banners) {
        bannerAdapter.setItems(banners);
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 4000);
        pager.setCurrentItem(currentItem);
    }

    @Override
    public void onLoadFinished(Loader<Home> loader, Home data) {
        super.onLoadFinished(loader, data);
        loadingIndicator.setVisible(false);
        isRefresh = false;
        home = data;
        if (data != null) {
            // 当只有下拉刷新的时候才可以弹出推荐框
            if (data.getRecommand() != null && isRecommandDialog) {
                showRecommandDialog(data.getRecommand());
            }
            if (data.getBanners() != null && !data.getBanners().isEmpty()) {
                setBannerConent(data.getBanners());
            }

            if (data.getEntries() != null && !data.getBanners().isEmpty()) {
                this.items = data.getEntries();

                HomeListAdapter listAdapter = (HomeListAdapter) getListAdapter().getWrappedAdapter();
                for (Entries entries : data.getEntries()) {
                    listAdapter.addItem(entries, isRefresh);
                }
            }
        }
        showList();
    }

    /**
     * 只有发现页面可见的时候,和推荐框没有显示过,和网络请求成功才能弹出推荐框
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isRecommandDialog && home != null) {
            if (home.getRecommand() != null) {
                showRecommandDialog(home.getRecommand());
            }

        }
    }

    /**
     * 创建和显示推荐框
     * @param recommand
     */
    private void showRecommandDialog(final HomeRecommand recommand) {
        if (recommandDialog == null) {
            recommandDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            recommandDialog.setContentView(R.layout.home_recommand_dialog);
            recommandImageLayout = recommandDialog.findViewById(R.id.recommand_dialog_image_layout);
            recommandImage = (ImageView) recommandDialog.findViewById(R.id.recommand_dialog_image);
            recommandCloseButton = (ImageButton) recommandDialog.findViewById(R.id.recommand_dialog_close);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.widthPixels * 4 / 3);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            recommandImageLayout.setLayoutParams(params);
            recommandDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    isRecommandDialog = true;
                }
            });
        }
        ImageLoader.getInstance().displayImage(recommand.getImg(), recommandImage, ImageUtils.imageLoader(getActivity(), 4));
        recommandCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recommandDialog != null && recommandDialog.isShowing()) {
                    recommandDialog.dismiss();
                }
            }
        });
        recommandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecommand(recommand);
            }
        });
        if (recommandDialog != null && !recommandDialog.isShowing()) {
            recommandDialog.show();
        }
    }

    /**
     * 根据指定的类型来跳转到相应的页面
     * @param item
     */
    private void startRecommand(final HomeRecommand item) {
        if ("ACTIVITY".equals(item.getType())) {
            startActivity(new Intents(activity, ActivityDetailActivity.class).add(IConstant.ACTIVITY_ID, item.getRefer().getId()).toIntent());
        } else if ("SHOP".equals(item.getType())) {
            activity.startActivity(new Intents(activity, ShopDetailsActivity.class).add(IConstant.SHOP_ID, item.getRefer().getId()).toIntent());
        } else if ("ADV".equals(item.getType())) {
            if (item.getRefer() != null) {
                if (!TextUtils.isEmpty(item.getRefer().getUrl())) {
                    if ("WEB_VIEW".equals(item.getRefer().getOpenType())) {
                        if (item.getRefer().getRequireLogin() == 1 && !LoginUtil.isLogin(activity)) {
                            startActivity(new Intents(activity, LoginActivity.class).toIntent());
                        } else {
                            activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, item.getRefer().getUrl())
                                    .add(IConstant.TITLE, item.getRefer().getTitle()).add(IConstant.HIDE_TITLE, item.getRefer().getHideTitle())
                                    .add(IConstant.HIDE_STATUS, item.getRefer().getHideStatus()).toIntent());
                        }
                    } else {
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRefer().getUrl()));
                        activity.startActivity(mIntent);
                    }
                }
            }
        } else if ("GROUP".equals(item.getType())) {
            activity.startActivity(new Intents(activity, ProgramActivity.class).add(IConstant.PROGRAM_ID, item.getRefer().getId()).toIntent());
        } else if ("CELEBRITY".equals(item.getType())) {
            activity.startActivity(new Intents(activity, StarActivity.class).add(IConstant.STAR_ID, item.getRefer().getId()).toIntent());
        } else if ("GAME".equals(item.getType())) {
            if (item.getRefer() != null) {
                if ("IDOL_DRAWLOT".equals(item.getRefer().getType())) {
                    if (LoginUtil.isLogin(activity)) {
                        activity.startActivity(new Intents(activity, RobTicketActivity.class).add(IConstant.ROB_TICKET_ID, item.getRefer().getId()).toIntent());
                    } else {
                        activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                    }
                } else if ("IDOL_VOTE".equals(item.getRefer().getType())) {
                    if (LoginUtil.isLogin(activity)) {
                        activity.startActivity(new Intents(activity, LuckyDipActivity.class).add(IConstant.LUCKY_DIP_ID, item.getRefer().getId()).toIntent());
                    } else {
                        activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                    }
                }
            }
        } else if ("KK_ROOM".equals(item.getType())) {
            if (LoginUtil.isLogin(activity)) {
                Intent intent = new Intent(activity, RoomLauncher.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", LoginUtil.getUserId(activity));
                bundle.putString("usernickname", LoginUtil.getUserNickName(activity));
                if (item.getRefer() != null) {
                    bundle.putString("roomid", item.getRefer().getId());
                }
                bundle.putString("usersessionid", MD5Util.getMD5Str(LoginUtil.getUserId(activity)));
//                bundle.putString("usergender", LoginUtil.get);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            } else {
                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
            }
        }
        if (LoginUtil.isLogin(activity)) {
            if (recommandDialog != null && recommandDialog.isShowing()) {
                recommandDialog.dismiss();
            }
        }
    }

    @Override
    protected Exception getException(Loader<Home> loader) {
        isRefresh = false;
        return super.getException(loader);
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new HomeListAdapter(getActivity());
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<Home> createRequest() {
        return services.createHomeRequest();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.home_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.home_loading;
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefresh = true;
        currentItem = pager.getCurrentItem();
        timerHandler.removeCallbacks(timerRunnable);
        refresh();
    }
}
