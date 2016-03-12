package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.android.volley.PagedRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.core.FragmentProvider;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.MultiListActivity;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourceLoadingIndicator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.ThrowableLoader;
import com.mzs.guaji.core.Utils;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.CelebrityPost;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.OthersPersonCenterInfo;
import com.mzs.guaji.offical.OfficialTvCircleSubmitTopicActivity;
import com.mzs.guaji.topic.StarDetailsService;
import com.mzs.guaji.topic.StarListAdapter;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.entity.DynamicToic;
import com.mzs.guaji.topic.entity.Feed;
import com.mzs.guaji.topic.entity.StarTopicList;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.entity.TopicAction;
import com.mzs.guaji.topic.entity.TopicPost;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * 名人页面
 */
public class StarCenterActivity extends MultiListActivity<OthersPersonCenterInfo> implements Response.ErrorListener {

    @Inject
    StarDetailsService service;
    @Inject
    Context context;
    @InjectView(R.id.publish_topic_layout)
    View publishTopic;
    @InjectView(R.id.star_root_layout)
    View mRootLayout;
    @InjectView(R.id.tv_star_return)
    TextView returnText;
    @InjectView(R.id.tv_star_attention)
    TextView attentionStar;
    @InjectView(R.id.star_title_layout)
    RelativeLayout titleLayout;

    private TextView nicknameText;
    private TextView signatureText;
    private TextView attentionNumbers;
    private ImageView backgroundImage;
    private ImageView avatarImage;
    private int width;
    /**
     * 名人list集合
     */
    private List<CelebrityPost> posts = new ArrayList<CelebrityPost>();
    private GuaJiAPI mApi;
    private ResourcePager<StarTopicList> starTopicList;
    private ResourcePager<DynamicToic> starDynamicListPage;
    private static final int STAR_DYNAMIC = 2;
    private static final int STAR_TOPIC = 1;
    private boolean isDynamicCondition = true;
    private int topicPage = 1;
    private int requestTopicCount = 20;
    private List<Feed> dynamicFeedData = new LinkedList<Feed>();
    private List<Topic> starTopicData = new LinkedList<Topic>();
    private boolean isLoadFinish;
    private boolean isTopicLoadFinish;
    private boolean isEmpty = true;
    private boolean isTopicEmpty = true;
    private boolean showEmptyTopics = false;
    private boolean isFollow;
    private Gson gson;
    private long userId;
    private String starName;
    private boolean showEmptyDynamic = false;
    private RelativeLayout headerView;

    private static final int Start_NOTIFIER = 0x101;
    private MyHand myHandler;
    private MyThread m;
    private boolean isChange = true;
    private int fixedHeight;
    private int headerHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getLongExtra("userId");
        setEmptyText(R.string.star_empty);
        WindowManager mWindowManger = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mMetrics = new DisplayMetrics();
        mWindowManger.getDefaultDisplay().getMetrics(mMetrics);
        gson = GsonUtils.createGson();
        starTopicList = createStarTopicList();
        starDynamicListPage = createStarDynamicList();
        addStarContentList();
        mApi = GuaJiAPI.newInstance(this);
        width = mMetrics.widthPixels;
        listView.setDivider(null);
        listView.setDividerHeight(0);
        configureList(this, getListView());
        publishTopic.setOnClickListener(publishTopicListener);
        m = new MyThread();
        new Thread(m).start();
        myHandler = new MyHand();
    }

    private View.OnClickListener publishTopicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isDynamicCondition) {
                Intent mIntent = new Intent(context, OfficialTvCircleSubmitTopicActivity.class);
                mIntent.putExtra("groupName", starName);
                mIntent.putExtra("tvcircleId", userId);
                mIntent.putExtra("isStar", true);
                startActivityForResult(mIntent, 0);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            StarListAdapter starListAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
            topicPage = 1;
            starListAdapter.clear();
            starTopicData.clear();
            getSupportLoaderManager().restartLoader(STAR_TOPIC, null, starTopicListContent);
        }
    }

    private void addStarContentList() {

        //使用loader请求话题列表
        getSupportLoaderManager().initLoader(STAR_TOPIC, null, starTopicListContent);
        getSupportLoaderManager().initLoader(STAR_DYNAMIC, null, starDynamicListContent);
    }

    @Override
    protected int getContentView() {
        return R.layout.starcenter_list_layout;
    }

    @Override
    protected ResourcePager<OthersPersonCenterInfo> createPager() {
        return new ResourcePager<OthersPersonCenterInfo>() {
            @Override
            public PageIterator<OthersPersonCenterInfo> createIterator(int page, int size) {
                PagedRequest<OthersPersonCenterInfo> request = new PagedRequest<OthersPersonCenterInfo>();

                request.setUri(getStarRequest(userId));
                request.setClazz(OthersPersonCenterInfo.class);
                return new PageIterator<OthersPersonCenterInfo>(context, request);
            }
        };
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.star_error;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.star_loading;
    }

    @Override
    protected MultiTypeAdapter createAdapter() {
        return new StarListAdapter(this);
    }

    @Override
    public void onLoadFinished(Loader<OthersPersonCenterInfo> loader, OthersPersonCenterInfo data) {
        super.onLoadFinished(loader, data);
        refreshListView.onRefreshComplete();


        headerHeight = getHeaderView(headerView) - Utils.dip2px(this, 6);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int statusBarHeight = frame.top;
        fixedHeight = titleLayout.getHeight() + frame.top;
        if (data != null) {
            setStarHeaderContent(data);
        }

    }

    private int getHeaderView(RelativeLayout view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    private void setStarHeaderContent(OthersPersonCenterInfo data) {
        starName = data.getNickname();
        nicknameText.setText(data.getNickname());
        signatureText.setText(data.getSignature());
        attentionNumbers.setText(data.getFansCnt() + "  " + getResources().getString(R.string.attention_number));
        if (!TextUtils.isEmpty(data.getBgImg())) {
            ImageLoader.getInstance().displayImage(data.getBgImg(), backgroundImage, ImageUtils.imageLoader(this, 0));
        }
        if (!TextUtils.isEmpty(data.getAvatar())) {
            ImageLoader.getInstance().displayImage(data.getAvatar(), avatarImage, ImageUtils.imageLoader(this, 1000));
        }
        /**
         *  关注
         */
        if (data.getIsFollowed() != 0) {
            attentionStar.setText(R.string.others_un_follow);
            isFollow = true;
        } else {
            attentionStar.setText(R.string.attention);
            isFollow = false;
        }

    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }


    @Override
    protected View adapterHeaderView() {
        headerView = (RelativeLayout) getLayoutInflater().inflate(R.layout.starcenter_header_layout, null);
        ViewFinder viewFinder = new ViewFinder(headerView);
        nicknameText = viewFinder.textView(R.id.tv_star_nickname);
        signatureText = viewFinder.textView(R.id.tv_star_signature);
        backgroundImage = viewFinder.imageView(R.id.iv_star_background);
        avatarImage = viewFinder.imageView(R.id.iv_star_avatar);
        attentionNumbers = viewFinder.textView(R.id.attention_person_numbers);
        RelativeLayout parentLayout = viewFinder.find(R.id.rl_star_parent);
        parentLayout.setLayoutParams(new AbsListView.LayoutParams(width, width * 13 / 16));

        final RelativeLayout starDynamic = viewFinder.find(R.id.star_dynamic_layout);
        final RelativeLayout starTopic = viewFinder.find(R.id.star_topic_layout);
        final ImageView dynamicIcon = viewFinder.find(R.id.dynamic_icon);
        final ImageView topicIcon = viewFinder.find(R.id.topic_icon);
        starDynamic.setEnabled(false);
        starDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dynamicIcon.setVisibility(View.VISIBLE);
                topicIcon.setVisibility(View.INVISIBLE);
                publishTopic.setVisibility(View.GONE);
                starDynamic.setEnabled(false);
                starTopic.setEnabled(true);
                isDynamicCondition = true;
                loadingIndicator.setVisible(false);
                loadingIndicator = new ResourceLoadingIndicator(StarCenterActivity.this, getLoadingMessage());
                loadingIndicator.setList(getListAdapter());
                StarListAdapter starListAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                if (!starListAdapter.isEmpty())
                    starListAdapter.clear();

                if (dynamicFeedData != null) {
                    if (dynamicFeedData.size() < 20)
                        loadingIndicator.setVisible(false);
                }

                starDynamicDataInformation(dynamicFeedData);
            }
        });
        starTopic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dynamicIcon.setVisibility(View.INVISIBLE);
                topicIcon.setVisibility(View.VISIBLE);
                publishTopic.setVisibility(View.VISIBLE);
                starDynamic.setEnabled(true);
                starTopic.setEnabled(false);
                isDynamicCondition = false;
                loadingIndicator.setVisible(false);
                loadingIndicator = new ResourceLoadingIndicator(StarCenterActivity.this, getLoadingMessage());
                loadingIndicator.setList(getListAdapter());
                StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                if (!dynamicAdapter.isEmpty())
                    dynamicAdapter.clear();
                if (starTopicData != null) {
                    if (starTopicData.size() < 20)
                        loadingIndicator.setVisible(false);
                }
                starTopicDataInfor(starTopicData);
            }
        });
        /**
         *  关注明星
         */
        attentionStar.setOnClickListener(mAddAttentionActionListener);
        returnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return headerView;
    }

    /**
     * 加关注点击事件
     */
    View.OnClickListener mAddAttentionActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!LoginUtil.isLogin(context)) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            } else {
                attentionStar.setEnabled(false);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("userId", userId + "");
                if (isFollow) {
                    TipsUtil.showPopupWindow(context, mRootLayout, R.string.delete_follow);
                    mApi.requestPostData(getUnFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
                        @Override
                        public void onResponse(DefaultReponse response) {
                            attentionStar.setEnabled(true);
                            TipsUtil.dismissPopupWindow();
                            if (response != null && response.getResponseCode() == 0) {
                                isFollow = false;
                                ToastUtil.showToast(context, R.string.delete_follow_succeed);
                                attentionStar.setText(R.string.attention);
                            } else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }, StarCenterActivity.this);
                } else {
                    TipsUtil.showPopupWindow(context, mRootLayout, R.string.add_follow);
                    mApi.requestPostData(getFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
                        @Override
                        public void onResponse(DefaultReponse response) {
                            attentionStar.setEnabled(true);
                            TipsUtil.dismissPopupWindow();
                            if (response != null && response.getResponseCode() == 0) {
                                isFollow = true;
                                ToastUtil.showToast(context, R.string.add_follow_succeed);
                                attentionStar.setText(R.string.others_un_follow);
                            } else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }, StarCenterActivity.this);
                }
            }
        }
    };

    @Override
    protected FragmentProvider getProvider() {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isChange = false;
        if (!dynamicFeedData.isEmpty()) {
            dynamicFeedData.clear();
            dynamicFeedData = null;
        }
        if (!starTopicData.isEmpty()) {
            starTopicData.clear();
            starTopicData = null;
        }
    }

    @Override
    public void onLastItemVisible() {
        if (isDynamicCondition) {
            if (showEmptyDynamic) {
                loadingIndicator.setVisible(false);
            } else {
                loadingIndicator.setVisible(true);
            }
            if (isLoadFinish) {
                return;
            }
            if (getSupportLoaderManager().hasRunningLoaders()) {
                return;
            }
            requestPage = requestPage + 1;
            getSupportLoaderManager().restartLoader(STAR_DYNAMIC, null, starDynamicListContent);
        } else {
            if (showEmptyTopics) {
                loadingIndicator.setVisible(false);
            } else {
                loadingIndicator.setVisible(true);
            }
            if (isTopicLoadFinish) {
                return;
            }
            if (getSupportLoaderManager().hasRunningLoaders()) {
                return;
            }
            topicPage = topicPage + 1;
            getSupportLoaderManager().restartLoader(STAR_TOPIC, null, starTopicListContent);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        posts.clear();
        if (isDynamicCondition) {
            if (!dynamicFeedData.isEmpty()) {
                dynamicFeedData.clear();
            }
            requestPage = 1;
            getSupportLoaderManager().restartLoader(STAR_DYNAMIC, null, starDynamicListContent);
        } else {
            if (!starTopicData.isEmpty()) {
                starTopicData.clear();
            }
            topicPage = 1;
            getSupportLoaderManager().restartLoader(STAR_TOPIC, null, starTopicListContent);
        }
    }


    /**
     * 名人动态 数据
     *
     * @return
     */
    private ResourcePager<DynamicToic> createStarDynamicList() {
        return new ResourcePager<DynamicToic>() {
            @Override
            public PageIterator<DynamicToic> createIterator(int page, int count) {
                return service.starDynamic(userId, requestPage, requestCount);
            }
        };
    }

    /**
     * 名人动态列表
     */
    LoaderManager.LoaderCallbacks<DynamicToic> starDynamicListContent = new LoaderManager.LoaderCallbacks<DynamicToic>() {
        @Override
        public Loader<DynamicToic> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<DynamicToic>(context, null) {
                @Override
                public DynamicToic loadData() throws Exception {
                    starDynamicListPage.next();
                    return starDynamicListPage.get();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<DynamicToic> loader, DynamicToic data) {
            refreshListView.onRefreshComplete(); //   隐藏刷新头
            if (data != null) {

                dynamicFeedData.addAll(data.getFeeds());
                if (isDynamicCondition) {
                    StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                    if (!dynamicAdapter.isEmpty()) {
                        dynamicAdapter.clear();
                    }
                    starDynamicDataInformation(dynamicFeedData);
                    if (!isEmpty && (data.getFeeds().isEmpty())) {
                        loadingIndicator.setVisible(true);
                        loadingIndicator.loadingAllFinish();
                    }
                }
            }

        }

        @Override
        public void onLoaderReset(Loader<DynamicToic> loader) {

        }
    };

    /**
     * 名人话题 数据
     *
     * @return
     */
    private ResourcePager<StarTopicList> createStarTopicList() {
        return new ResourcePager<StarTopicList>() {
            @Override
            protected Object getId(StarTopicList resource) {
                return null;
            }

            @Override
            public PageIterator<StarTopicList> createIterator(int page, int count) {
                return service.starTopicList(userId, topicPage, requestTopicCount);
            }
        };
    }

    /**
     * 名人话题列表
     */
    LoaderManager.LoaderCallbacks<StarTopicList> starTopicListContent = new LoaderManager.LoaderCallbacks<StarTopicList>() {
        @Override
        public Loader<StarTopicList> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<StarTopicList>(context, null) {
                @Override
                public StarTopicList loadData() throws Exception {
                    starTopicList.next();
                    return starTopicList.get();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<StarTopicList> loader, StarTopicList data) {
            refreshListView.onRefreshComplete(); //   隐藏刷新头
            if (data != null) {
                if (data.getTopics() != null) {
                    starTopicData.addAll(data.getTopics());
                }
                if (!isDynamicCondition) {
                    StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                    if (!dynamicAdapter.isEmpty()) {
                        dynamicAdapter.clear();
                    }
                    starTopicDataInfor(starTopicData);

                    if ((!isTopicEmpty) && (data.getTopics() == null)) {
                        loadingIndicator.setVisible(true);
                        loadingIndicator.loadingAllFinish();
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<StarTopicList> loader) {

        }
    };

    /**
     * 把名人动态数据放到适配器中
     *
     * @param data 数据
     */

    private void starDynamicDataInformation(List<Feed> data) {

        if (data != null && data.size() > 0) {
            this.items = data;
            if (isEmpty && data.size() < 20) {
                loadingIndicator.setVisible(false);
            }
            isEmpty = false;
            StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
            for (Feed feed : data) {
                dynamicAdapter.addItemObject(feed);
            }

        } else {
            List<Object> objects = new ArrayList<Object>();
            objects.add(new Object());
            StarCenterActivity.this.items = objects;
            isLoadFinish = true;
            if (isEmpty) {
                showEmptyDynamic = true;
                List<String> noInformationList = new LinkedList<String>();
                if (!noInformationList.isEmpty()) {
                    noInformationList.clear();
                }
                noInformationList.add("暂无动态信息");
                StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                dynamicAdapter.addItemObject(noInformationList.get(0));
                ViewUtils.setGone(progressBar, true);  //true为   不可见，
                ViewUtils.setGone(refreshListView, false);//false为   可见，
                ViewUtils.setGone(listView, false); //false为   可见，
                loadingIndicator.setVisible(false);
            }
        }

        showList();
    }

    /**
     * 把名人话题数据放到适配器中
     *
     * @param data 数据
     */

    private void starTopicDataInfor(List<Topic> data) {
        if (data != null && data.size() > 0) {
            this.items = data;
            StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();

            if (isTopicEmpty && data.size() < 20) {
                loadingIndicator.setVisible(false);
            }

            isTopicEmpty = false;
            for (Topic topic : data) {
                dynamicAdapter.addItemObject(topic);
            }
        } else {
            List<Object> objects = new ArrayList<Object>();
            objects.add(new Object());
            StarCenterActivity.this.items = objects;
            isTopicLoadFinish = true;
            if (isTopicEmpty) {
                showEmptyTopics = true;
                List<String> noinformationlist = new LinkedList<String>();
                if (!noinformationlist.isEmpty()) {
                    noinformationlist.clear();
                }
                noinformationlist.add("暂无话题信息");

                StarListAdapter dynamicAdapter = (StarListAdapter) getListAdapter().getWrappedAdapter();
                dynamicAdapter.addItemObject(noinformationlist.get(0));
                ViewUtils.setGone(progressBar, true);  //true为   不可见，
                ViewUtils.setGone(refreshListView, false);//false为   可见，
                ViewUtils.setGone(listView, false); //false为   可见，
                loadingIndicator.setVisible(false);
            }
        }
        showList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = null;
        if (isDynamicCondition) {
            Feed feed = (Feed) l.getItemAtPosition(position);
            if ("TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction = gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).toIntent();
            } else if ("ACTIVITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction = gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
            } else if ("CELEBRITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction = gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
            } else if ("TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).toIntent();
            } else if ("ACTIVITY_TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
            } else if ("CELEBRITY_TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                if ("TOPIC".equals(topicPost.getType())) {
                    intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).toIntent();
                } else if ("CELEBRITY_TOPIC".equals(topicPost.getType())) {
                    intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
                }

            }
        } else {
            Topic topic = (Topic) l.getItemAtPosition(position);
            intent = new Intents(this, TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topic.getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
        }
        if (intent != null)
            startActivity(intent);

    }

    /**
     * 添加关注
     *
     * @return
     */
    private String getFollowRequestUrl() {
        return DOMAIN + "user/follow.json";
    }

    /**
     * 取消关注
     *
     * @return
     */
    private String getUnFollowRequestUrl() {
        return DOMAIN + "user/unfollow.json";
    }

    /**
     * 名人数据 Uri
     *
     * @return
     */
    private String getStarRequest(long userId) {
        return "user/read.json" + "?userId=" + userId;
    }

    /**
     * 网络异常接口实现
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {

    }

    class MyHand extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { //判断接收的消息
                case StarCenterActivity.Start_NOTIFIER:
                    if (listView != null && listView.getChildAt(0) != null) {
                        View childView = listView.getChildAt(0);
                        if (childView == headerView) {
                            int[] location = new int[2];
                            childView.getLocationOnScreen(location);
                            if (Math.abs(location[1]) > headerHeight) {
                                return;
                            }
                            if ((location[1] + headerHeight) >= fixedHeight) {
                                titleLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                            } else {
                                titleLayout.setBackgroundColor(getResources().getColor(R.color.title_background));
                            }
                        } else {
                            titleLayout.setBackgroundColor(getResources().getColor(R.color.title_background));
                        }

                    }
                    break;
            }
            super.handleMessage(msg);
        }

    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            while (isChange) {

                Message msg = myHandler.obtainMessage();
                msg.what = StarCenterActivity.Start_NOTIFIER;
                myHandler.sendMessage(msg);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}