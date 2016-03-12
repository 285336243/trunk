package com.mzs.guaji.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.android.volley.Response;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.HomeAdapter;
import com.mzs.guaji.adapter.HomeBannerAdapter;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.entity.ActivityTopicHome;
import com.mzs.guaji.entity.Banners;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.entity.GameList;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.entity.Home;
import com.mzs.guaji.entity.HomeRecommend;
import com.mzs.guaji.entity.ShakeResult;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.LoveExponentActivity;
import com.mzs.guaji.ui.ScanLogoActivity;
import com.mzs.guaji.ui.SearchActivity;
import com.mzs.guaji.ui.ShakeActivity;
import com.mzs.guaji.ui.TopicHomeActivity;
import com.mzs.guaji.ui.WebViewActivity;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-15.
 */
public class HomeFragment extends GuaJiFragment {

    private Context context;
    private TextView mSearchText;
    private TextView mInteractionText;
    private ViewPager mViewPager;
    private TextView mImageTitleText;
    private LinearLayout mDotLayout;
    private int width;
    private int mCurrentItem;
    private List<View> mRoundViews;
    private Handler mTimerHandler;
    private Dialog mDialog;
    private HomeAdapter mListAdapter;
    private HomeBannerAdapter mBannersAdapter;
    private PopupWindow mInteractionWindow;
    private TextView mShakeText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager mWindowManger = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mMetrics = new DisplayMetrics();
        mWindowManger.getDefaultDisplay().getMetrics(mMetrics);
        width = mMetrics.widthPixels;
        mRoundViews = new ArrayList<View>();
        mTimerHandler = new Handler();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, null);
        mRootView = view;
        mRefreshListView = (PullToRefreshListView) view.findViewById(R.id.home_listview);
        super.onCreateView(inflater, container, savedInstanceState);

        mSearchText = (TextView) view.findViewById(R.id.home_search_layout);
        mInteractionText = (TextView) view.findViewById(R.id.home_interaction_layout);
        findListHeadView(inflater);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchText.setOnClickListener(mSearchClickListener);
        mInteractionText.setOnClickListener(mInteractionClickListener);
    }

    @Override
    protected void onInitialization() {
        super.onInitialization();
        requestHomeV4();
    }

    private void requestHomeV4() {
        mApi.requestGetData(getHomeV4Request(), Home.class, new Response.Listener<Home>() {
            @Override
            public void onResponse(final Home response) {
                setOnLaodingGone();
                if (response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getCategorys() != null) {
                            if (response.getBanners() != null) {
                                setListHeadContent(response.getBanners());
                            }
                            mListAdapter = new HomeAdapter(getActivity(), response.getCategorys(), width);
                            SwingBottomInAnimationAdapter mAnimAtionAdapter = new SwingBottomInAnimationAdapter(mListAdapter);
                            mAnimAtionAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                            mRefreshListView.setAdapter(mAnimAtionAdapter);

                            final SharedPreferences preferences = getActivity().getSharedPreferences(IConstant.GUIDE, Context.MODE_PRIVATE);
                            final int guide = preferences.getInt(IConstant.HOME_GUIDE, 0);
                            if (guide == 0) {
                                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                                dialog.setContentView(R.layout.main_guice);
                                View v = dialog.findViewById(R.id.home_guide_root);
                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                    }
                                });
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        final SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt(IConstant.HOME_GUIDE, 1).commit();
                                        if(response.getRecommend() != null && response.getRecommend().get(0) != null) {
                                            showHomeRecommendDialog(response.getRecommend().get(0));
                                        }
                                    }
                                });
                                if (!dialog.isShowing())
                                    dialog.show();
                            } else {
                                if(response.getRecommend() != null && response.getRecommend().get(0) != null) {
                                    showHomeRecommendDialog(response.getRecommend().get(0));
                                }
                            }
                        }
                    }else {
                        ToastUtil.showToast(getActivity(), response.getResponseMessage());
                    }
                }
            }
        }, this);
    }

    public void setEnabledFalse() {
        mSearchText.setEnabled(false);
        mInteractionText.setEnabled(false);
    }

    public void setEnabledTrue() {
        mSearchText.setEnabled(true);
        mInteractionText.setEnabled(true);
    }

    private void findListHeadView(LayoutInflater inflater) {
        View headerView = inflater.inflate(R.layout.home_item_head, null);
        mViewPager = (ViewPager) headerView.findViewById(R.id.home_head_pager);
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(width, width * 9 / 20));
        mImageTitleText = (TextView) headerView.findViewById(R.id.home_header_image_title);
        mDotLayout = (LinearLayout) headerView.findViewById(R.id.home_header_dot_layout);
        mRefreshListView.getRefreshableView().addHeaderView(headerView);
    }

    private void setListHeadContent(List<Banners> mBanners) {
        mBannersAdapter = new HomeBannerAdapter(context, mBanners);
        mViewPager.setAdapter(mBannersAdapter);
        mViewPager.setOnPageChangeListener(new HomePageChangeListener(mBanners));
        bindViewPagerGestureDetector(mViewPager);
        mTimerHandler.postDelayed(mTimerRunnable, 6000);
        for (int i = 0; i < mBanners.size(); i++) {
            View mRoundView = new View(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.leftMargin = 8;
            params.rightMargin = 8;
            if(i == 0) {
                mRoundView.setBackgroundResource(R.drawable.imager_seleter_focused);
            }else {
                mRoundView.setBackgroundResource(R.drawable.image_seleter_normal);
            }
            mRoundViews.add(mRoundView);
            mDotLayout.addView(mRoundView, params);
        }
    }

    private void bindViewPagerGestureDetector(ViewPager mViewPager) {
        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        final View.OnTouchListener mGestureDetectorListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
        mViewPager.setOnTouchListener(mGestureDetectorListener);
    }
    private void showHomeRecommendDialog(HomeRecommend mRecommend) {
        DisplayMetrics mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        int width = mMetrics.widthPixels;
        mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.home_recommend);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        RelativeLayout mRootLayout = (RelativeLayout) mDialog.findViewById(R.id.home_recommend_root_layout);
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(width, width * 4 / 3);
        mRootLayout.setLayoutParams(mParams);
        mParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        ImageView mImageView = (ImageView) mDialog.findViewById(R.id.home_recommend_image);
        mImageLoader.displayImage(mRecommend.getImg(), mImageView, ImageUtils.imageLoader(getActivity(), 4));
        Button mSkipButton = (Button) mDialog.findViewById(R.id.home_recommend_skip_btn);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        Button mSeeDetailsButton = (Button) mDialog.findViewById(R.id.home_recommend_see_details_btn);
        setRecommendClickListener(mSeeDetailsButton, mRecommend);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void setRecommendClickListener(Button mSeeDetailsButton, final HomeRecommend mRecommend) {
        View.OnClickListener mBannerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("ACTIVITY".equals(mRecommend.getType())) {
                    startActivity(mRecommend);
                } else if ("TOPIC".equals(mRecommend.getType())) {
                    startTopic(mRecommend);
                } else if ("GROUP".equals(mRecommend.getType())) {
                    startGroup(mRecommend);
                } else if ("ENTRY_FORM".equals(mRecommend.getType())) {
                    startEntryForm(mRecommend);
                } else if ("GAME".equals(mRecommend.getType())) {
                    startGame(mRecommend);
                }
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        };
        mSeeDetailsButton.setOnClickListener(mBannerClickListener);
    }

    private void startActivity(HomeRecommend mRecommend) {
        Activity mActivity = mGson.fromJson(mRecommend.getTarget(), Activity.class);
        if (mActivity != null) {
            if("BROWSER".equals(mActivity.getType())) {
                JsonElement mElement = mActivity.getParam();
                if(mElement != null) {
                    SkipBrowser mBrowser = mGson.fromJson(mElement, SkipBrowser.class);
                    if(mBrowser != null) {
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowser.getLink()));
                        context.startActivity(mIntent);
                    }
                }
            }else if("WEB_VIEW".equals(mActivity.getType())) {
                JsonElement mElement = mActivity.getParam();
                if(mElement != null) {
                    SkipWebView mWebView = mGson.fromJson(mElement, SkipWebView.class);
                    if(mWebView != null) {
                        Intent mIntent = new Intent(context, WebViewActivity.class);
                        mIntent.putExtra("url", mWebView.getLink());
                        context.startActivity(mIntent);
                    }
                }
            } else if ("TOPIC_HOME".equals(mActivity.getType())) {
                JsonElement element = mActivity.getParam();
                if (element != null) {
                    ActivityTopicHome topicHome = mGson.fromJson(element, ActivityTopicHome.class);
                    if (topicHome != null) {
                        Intent intent = new Intent(context, TopicHomeActivity.class);
                        intent.putExtra("topic_home_image", topicHome.getImg());
                        intent.putExtra("activityId", mActivity.getId());
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void startGame(final HomeRecommend mRecommend) {
        if (LoginUtil.isLogin(context)) {
            MobclickAgent.onEvent(context, "game_lovetest");
            GameList gameList = mGson.fromJson(mRecommend.getTarget(), GameList.class);
            if (gameList != null) {
                if ("AFFECTION_INDEX".equals(gameList.getType())) {
                    Intent mIntent = new Intent(context, LoveExponentActivity.class);
                    startActivity(mIntent);
                } else if ("WEB_VIEW".equals(gameList.getType())) {
                    startWebViewActivity(gameList);
                }
            }
        } else {
            Intent mIntent = new Intent(context, LoginActivity.class);
            startActivity(mIntent);
        }
    }

    private void startWebViewActivity(GameList gameList) {
        if (LoginUtil.isLogin(context)) {
            Map<String, String> maps= GsonUtils.createGson().fromJson(gameList.getParam(), new TypeToken<Map<String, String>>(){}.getType());
            if (maps != null) {
                String link = maps.get("link");
                String noTitle = maps.get("noTitle");
                String title = maps.get("title");
                String backKey = maps.get("backKey");
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", link);
                intent.putExtra("noTitle", noTitle);
                intent.putExtra("title", title);
                intent.putExtra("backKey", backKey);
                context.startActivity(intent);
            }
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void startTopic(HomeRecommend mRecommend) {
        Topic mTopic = mGson.fromJson(mRecommend.getTarget(), Topic.class);
        if(mTopic != null) {
            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
            mIntent.putExtra("topicId", mTopic.getId());
            context.startActivity(mIntent);
        }
    }

    private void startGroup(HomeRecommend mRecommend) {
        Group mGroup = mGson.fromJson(mRecommend.getTarget(), Group.class);
        if(mGroup != null) {
            if("OFFICIAL".equals(mGroup.getType())) {
                Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
                mIntent.putExtra("img", mGroup.getImg());
                mIntent.putExtra("id", mGroup.getId());
                mIntent.putExtra("name", mGroup.getName());
                context.startActivity(mIntent);
            }
        }
    }

    private void startEntryForm(HomeRecommend mRecommend) {
        EntryForm mEntryForm = mGson.fromJson(mRecommend.getTarget(), EntryForm.class);
        if(mEntryForm != null && "GSTX_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent applyIntent = new Intent(context, GSTXApplyActivity.class);
            applyIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(applyIntent);
        }else if(mEntryForm != null && "AQBWZ_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, AQBWApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            mIntent.putExtra("clause", mEntryForm.getClause());
            context.startActivity(mIntent);
        }else if(mEntryForm != null && "FNMS_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, FNMSApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(mIntent);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        mApi.requestGetData(getHomeV4Request(), Home.class, new Response.Listener<Home>() {
            @Override
            public void onResponse(Home response) {
                setOnLaodingGone();
                refreshView.onRefreshComplete();
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        if (response.getCategorys() != null) {
                            if (response.getBanners() != null && mBannersAdapter != null) {
                                mBannersAdapter.addBanners(response.getBanners());
                            }
                            if (mListAdapter != null) {
                                mListAdapter.addCategoryGroups(response.getCategorys());
                            }
                        }
                    } else {
                        ToastUtil.showToast(getActivity(), response.getResponseMessage());
                    }
                }
            }
        }, this);
    }

    /**
     * 搜索点击事件
     */
    View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(context, "home_search");
            Intent mIntent = new Intent(getActivity(), SearchActivity.class);
            startActivity(mIntent);
        }
    };

    View.OnClickListener mInteractionClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showPopupWindow();
        }
    };

    public void showPopupWindow() {
        MobclickAgent.onEvent(context, "home_interactive");
        View mPopupView = (getActivity()).getLayoutInflater().inflate(R.layout.home_popup_item, null);
        TextView mScanText = (TextView) mPopupView.findViewById(R.id.home_scan_layout);
        mScanText.setOnClickListener(mScanClickListener);
        mShakeText = (TextView) mPopupView.findViewById(R.id.home_shake_layout);
        mShakeText.setOnClickListener(mShakeClickListener);
        mInteractionWindow = new PopupWindow(context);
        mInteractionWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mInteractionWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mInteractionWindow.setContentView(mPopupView);
        mInteractionWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mInteractionWindow.setOutsideTouchable(true);
        mInteractionWindow.setFocusable(true);
        mInteractionWindow.showAsDropDown(mInteractionText, 0, 0);
    }

    View.OnClickListener mShakeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissInteractionWindow();
            mShakeText.setEnabled(false);
            mApi.requestGetData(getShakeRequest(), ShakeResult.class, new Response.Listener<ShakeResult>() {
                @Override
                public void onResponse(ShakeResult response) {
                    mShakeText.setEnabled(true);
                    if(response != null && response.getResponseCode() == 0) {
                        if(response.getShake() != null) {
                            if("DRAW_LOT".equals(response.getShake().getType())) {
                                if(LoginUtil.isLogin(context)) {
                                    Intent mIntent = new Intent(context, ShakeActivity.class);
                                    startActivity(mIntent);
                                }else {
                                    Intent mIntent = new Intent(context, LoginActivity.class);
                                    startActivity(mIntent);
                                }
                            }else if ("WEB_VIEW".equals(response.getShake().getType())) {
                                JsonElement mElement = response.getShake().getParam();
                                if(mElement != null) {
                                    SkipWebView mWebView = mGson.fromJson(mElement, SkipWebView.class);
                                    Intent mIntent = new Intent(context, WebViewActivity.class);
                                    mIntent.putExtra("url", mWebView.getLink());
                                    context.startActivity(mIntent);
                                }
                            }else if ("BROWSER".equals(response.getShake().getType())) {
                                JsonElement mElement = response.getShake().getParam();
                                if(mElement != null) {
                                    SkipBrowser mBrowser = mGson.fromJson(mElement, SkipBrowser.class);
                                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowser.getLink()));
                                    context.startActivity(mIntent);
                                }
                            }else if ("QUESTION".equals(response.getShake().getType())) {

                            }
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }, null);

        }
    };

    private void dismissInteractionWindow() {
        if(mInteractionWindow != null) {
            mInteractionWindow.dismiss();
        }
    }

    /**
     * 扫一扫点击事件
     */
    View.OnClickListener mScanClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissInteractionWindow();
            Intent mIntent = new Intent(getActivity(), ScanLogoActivity.class);
            startActivity(mIntent);
        }
    };

    /**
     * handler 设置viewpager当前的页面
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(msg.what);
        }
    };

    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentItem = (mViewPager.getCurrentItem() +1) % mViewPager.getAdapter().getCount();
            handler.sendEmptyMessage(mCurrentItem);
            mTimerHandler.postDelayed(this, 6000);
        }
    };

    /**
     * viewpager滑动监听
     * @author lenovo
     *
     */
    private class HomePageChangeListener implements ViewPager.OnPageChangeListener {

        private List<Banners> mBanners;
        public HomePageChangeListener(List<Banners> mBanners) {
            this.mBanners = mBanners;
            if(mBanners.get(0) != null) {
                mImageTitleText.setText(mBanners.get(0).getTitle());
            }
        }

        private int oldPosition = 0;

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentItem = position;
            mRoundViews.get(oldPosition).setBackgroundResource(R.drawable.image_seleter_normal);
            mRoundViews.get(position).setBackgroundResource(R.drawable.imager_seleter_focused);
            oldPosition = position;
            Banners mBanner = mBanners.get(position);
            if (mBanner != null) {
                mImageTitleText.setText(mBanner.getTitle());
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mTimerHandler.removeCallbacks(mTimerRunnable);
            mTimerHandler.postDelayed(mTimerRunnable, 6000);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private String getHomeRequest() {
        return DOMAIN + "home/v1.json?p=ANDROID";
    }

    private String getNewHomeRequest() {
        return DOMAIN + "home/v2.json?p=ANDROID";
    }

    private String getHomeV3Request() {
        return DOMAIN + "home/v3.json?p=ANDROID";
    }

    private String getHomeV4Request() {
        return DOMAIN + "home/v4.json?p=ANDROID";
    }

    private String getShakeRequest() {
        return DOMAIN + "shake/request.json?platform=ANDROID";
    }
}
