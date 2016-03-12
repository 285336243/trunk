package com.socialtv.feed;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.Intents;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.feed.entity.OthersFeedHeader;
import com.socialtv.feed.entity.PersionFeed;
import com.socialtv.http.HttpUtils;
import com.socialtv.publicentity.Topic;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.topic.TopicDetailActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.view.TranslateView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-4.
 * 自己的动态的页面
 */
public class SelfFeedActivity extends SingleListActivity<PersionFeed> {

    private final static int HEADER_ID = 100;

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    @Inject
    private Activity activity;

    @Inject
    private FeedServices services;

    private PersionFeedAdapter adapter;

    @InjectView(R.id.self_dyanmic_header_layout)
    private TranslateView headerView;

    @InjectView(R.id.self_dyanmic_header_image)
    private ImageView headerImageView;

    @InjectView(R.id.self_dyanmic_avatar)
    private ImageView headerAvatarImageView;

    @InjectView(R.id.self_dyanmic_nickname)
    private TextView nicknameTextView;

    @InjectView(R.id.self_dynamic_gender)
    private ImageView genderImageView;

    @InjectView(R.id.self_dyanmic_givenscore)
    private TextView scoreTextView;

    @InjectView(R.id.self_dynamic_header_avatar_nickname_layout)
    private View avatarNicknameView;

    @InjectView(R.id.self_dynamic_badge_layout)
    private LinearLayout badgeLayout;

    private TextView followCountTextView;

    private TextView fansCountTextView;

    private TextView signatureTextView;

    @InjectView(R.id.feed_like_anim_full)
    private ImageView likFullAnimView;

    @InjectView(R.id.feed_un_like_anim_layout)
    private View unLikeAnimView;

    @InjectView(R.id.feed_like_anim_left)
    private ImageView likeLeftAnimView;

    @InjectView(R.id.feed_like_anim_right)
    private ImageView likeRightAnimView;

    private AnimatorSet likeFullSet;

    private AnimatorSet unLikeLeftSet;

    private AnimatorSet unLikeRightSet;

    private List<Topic> feedItems = new ArrayList<Topic>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_roundback_tomato);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(HEADER_ID, null, headerCallbacks);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(metrics.widthPixels, (int) (metrics.widthPixels * 0.7));
        headerImageView.setLayoutParams(params);

        setOnScrollListener(scrollListener);
    }

    /**
     * ListView中的滑动监听
     */
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int mMinHeaderTranslation = -headerView.getMeasuredHeight() + getSupportActionBar().getHeight();
            int y = getScrollY();
            View topChild = view.getChildAt(0);
            headerView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
            if (topChild == null) {
                onNewScroll(0, -Math.max(-y, mMinHeaderTranslation));
            } else {
                onNewScroll(-topChild.getTop(), -Math.max(-y, mMinHeaderTranslation));
            }
        }
    };

    /**
     * 计算悬停控件的位置
     * @param scrollPosition
     * @param y
     */
    @TargetApi(11)
    private void onNewScroll(int scrollPosition, int y) {
        int currentHeaderHeight = headerView.getHeight();
        int headerHeight = currentHeaderHeight - getSupportActionBar().getHeight();
        float ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);
        float alpha = (float) (headerHeight - y) / headerHeight;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(1);
            animation.setFillAfter(true);
            avatarNicknameView.startAnimation(animation);
        } else {
            avatarNicknameView.setAlpha(alpha);
        }
    }

    private int getScrollY() {
        View v = listView.getChildAt(0);
        if (v == null)
            return 0;
        int positon = listView.getFirstVisiblePosition();
        int top = v.getTop();
        int y = 0;
        if (positon - 1 >= 1) {
            y = headerView.getHeight();
        }
        return y + (-top + (positon - 1) * v.getHeight());
    }

    LoaderManager.LoaderCallbacks<OthersFeedHeader> headerCallbacks = new LoaderManager.LoaderCallbacks<OthersFeedHeader>() {
        @Override
        public Loader<OthersFeedHeader> onCreateLoader(int i, Bundle bundle) {
            return new ThrowableLoader<OthersFeedHeader>(activity) {
                @Override
                public OthersFeedHeader loadData() throws Exception {
                    return (OthersFeedHeader) HttpUtils.doRequest(services.createOthersFeedHeader(userId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<OthersFeedHeader> userLoader, OthersFeedHeader header) {
            if (header != null) {
                User user = header.getUser();
                if (user != null) {
                    show(headerView);
                    ImageLoader.getInstance().displayImage(user.getBgImg(), headerImageView, ImageUtils.imageLoader(activity, 0));
                    ImageLoader.getInstance().displayImage(user.getAvatar(), headerAvatarImageView, ImageUtils.avatarImageLoader());
                    nicknameTextView.setText(user.getNickname());

                    if (user.getBadges() != null && !user.getBadges().isEmpty()) {
                        show(badgeLayout);
                        badgeLayout.removeAllViews();
                        for (UserBadge badge : user.getBadges()) {
                            if (badge != null) {
                                ImageView imageView = new ImageView(activity);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, 16), Utils.dip2px(activity, 16));
                                params.gravity = Gravity.CENTER_VERTICAL;
                                imageView.setLayoutParams(params);
                                ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
                                badgeLayout.addView(imageView);
                            }
                        }
                    } else {
                        hide(badgeLayout);
                    }

                    if (TextUtils.isEmpty(user.getScore())) {
                        scoreTextView.setText("0 番茄币");
                    } else {
                        scoreTextView.setText(user.getScore() + " 番茄币");
                    }
                    if (TextUtils.isEmpty(user.getGender())) {
                        hide(genderImageView);
                    } else {
                        show(genderImageView);
                        if ("f".equals(user.getGender())) {
                            genderImageView.setImageResource(R.drawable.icon_lady_tomato);
                        } else {
                            genderImageView.setImageResource(R.drawable.icon_man_tomato);
                        }
                    }
                    if (!TextUtils.isEmpty(user.getSignature())) {
                        signatureTextView.setText(user.getSignature());
                    } else {
                        hide(signatureTextView);
                    }

                    followCountTextView.setText(String.format(getResources().getString(R.string.follow_count), user.getFollowCnt()));
                    fansCountTextView.setText(String.format(getResources().getString(R.string.fans_count), user.getFansCnt()));
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<OthersFeedHeader> userLoader) {

        }
    };

    /**
     * 刷新列表
     * @param topicId
     */
    public void refreshList(String topicId) {
        Iterator<Topic> iterator = feedItems.iterator();
        while (iterator.hasNext()) {
            Topic topic = iterator.next();
            if (topicId.equals(topic.getId())) {
                iterator.remove();
            }
        }
        if (!feedItems.isEmpty()) {
            adapter.setItems(feedItems, "topics");
        } else {
            adapter.setItems(feedItems, "empty");
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.self_feed_list;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        adapter = new PersionFeedAdapter(this);
        return adapter;
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
        View v = getLayoutInflater().inflate(R.layout.self_feed_header, null);
        View emptyView = v.findViewById(R.id.self_feed_header_empty_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (metrics.widthPixels * 0.7)  - Utils.dip2px(activity, 48)));
        followCountTextView = (TextView) v.findViewById(R.id.self_feed_header_follow_count);
        fansCountTextView = (TextView) v.findViewById(R.id.self_feed_header_fans_count);
        signatureTextView = (TextView) v.findViewById(R.id.self_feed_header_signature);
        return v;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<PersionFeed> createRequest() {
        return services.createOthersFeedRequest(userId, requestPage, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<PersionFeed> loader, PersionFeed data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                    this.items = data.getTopics();
                    this.feedItems.addAll(data.getTopics());
//                    getListAdapter().getWrappedAdapter().setItems(this.feedItems);
                    adapter.setItems(this.feedItems, "topics");
                    if (data.getTopics().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty()) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        loadingIndicator.setVisible(false);
                        List<Topic> feedItems = new ArrayList<Topic>();
                        Topic item = new Topic();
                        item.setMessage("暂无动态");
                        feedItems.add(item);
                        this.items = feedItems;
                        adapter.setItems(feedItems, "empty");
                    }
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        feedItems.clear();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.dynamic_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.dynamic_loading;
    }

    /**
     * Get provider of the currently selected fragment
     *
     * @return fragment provider
     */
    @Override
    protected FragmentProvider getProvider() {
        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.items.clear();
        this.feedItems.clear();
        this.feedItems = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED && data != null) {
            if (IConstant.DELETE_OK.equals(data.getAction())) {
                final String topicId = data.getStringExtra(IConstant.TOPIC_ID);
                if (!TextUtils.isEmpty(topicId)) {
                    ListIterator<Topic> iterator = feedItems.listIterator();
                    while (iterator.hasNext()) {
                        Topic topic = iterator.next();
                        if (topicId.equals(topic.getId())) {
                            iterator.remove();
                        }
                    }
                    if (!feedItems.isEmpty()) {
                        adapter.setItems(feedItems, "topics");
                    } else {
                        adapter.setItems(feedItems, "empty");
                    }
                }
            } else {
                //喜欢更新
                final Topic mTopic = (Topic) data.getSerializableExtra(IConstant.TOPIC_LIKE_UPDATE);
                //评论更新
                final Topic postTopic = (Topic) data.getSerializableExtra(IConstant.TOPIC_POST_UPDATE);
                ListIterator<Topic> iterator = feedItems.listIterator();
                while (iterator.hasNext()) {
                    Topic topic = iterator.next();
                    if (mTopic != null) {
                        if (topic.getId().equals(mTopic.getId())) {
                            topic.setFavoritsCnt(mTopic.getFavoritsCnt());
                            topic.setIsFavorit(mTopic.getIsFavorit());
                        }
                    }
                    if (postTopic != null) {
                        if (topic.getId().equals(postTopic.getId())) {
                            if (postTopic.getPosts() != null && !postTopic.getPosts().isEmpty()) {
                                topic.setPosts(postTopic.getPosts());
                            }
                        }
                    }
                }
                if (!feedItems.isEmpty()) {
                    adapter.setItems(feedItems, "topics");
                } else {
                    adapter.setItems(feedItems, "empty");
                }
                getSupportLoaderManager().restartLoader(HEADER_ID, null, headerCallbacks);
            }
        }
    }

    private void createFullAnimation() {
        if (likeFullSet == null) {
            ObjectAnimator alphaFirst = ObjectAnimator.ofFloat(likFullAnimView, "alpha", 0.0f, 1.0f);
            ObjectAnimator scaleFirstX = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 1.0f, 1.5f);
            ObjectAnimator scaleFirstY = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 1.0f, 1.5f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 1.5f, 0.8f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 1.5f, 0.8f);
            ObjectAnimator zoomOut = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 0.8f, 1.5f);
            ObjectAnimator zoomIn = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 0.8f, 1.5f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(likFullAnimView, "alpha", 1.0f, 0.0f);
            likeFullSet = new AnimatorSet();
            likeFullSet.setInterpolator(new DecelerateInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(alphaFirst).with(scaleFirstX).with(scaleFirstY);
            set.setDuration(200);
            AnimatorSet set1 = new AnimatorSet();
            set1.play(scaleX).with(scaleY);
            set1.setDuration(200);
            AnimatorSet set2 = new AnimatorSet();
            set2.play(zoomOut).with(zoomIn);
            set2.setDuration(400);
            AnimatorSet set3 = new AnimatorSet();
            set3.play(scaleX).with(scaleY);
            set3.setDuration(200);
            AnimatorSet set4 = new AnimatorSet();
            set4.play(zoomOut).with(zoomIn);
            set4.setDuration(200);
            AnimatorSet alphaSet = new AnimatorSet();
            alphaSet.setDuration(400);
            alphaSet.play(scaleX).with(scaleY).with(alpha);
            likeFullSet.playSequentially(set, set1, set2, set3, set4, alphaSet);
            likeFullSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    likFullAnimView.setVisibility(View.VISIBLE);
                    unLikeAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    likFullAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void startFullAnimation() {
        createFullAnimation();
        if (likeFullSet.isRunning()) {
            likFullAnimView.setVisibility(View.GONE);
            likeFullSet.cancel();
        }
        likeFullSet.start();
    }

    public void startUnLikeAnimation() {
        createUnLikeAnimation();
        if (unLikeLeftSet.isRunning() && unLikeRightSet.isRunning()) {
            unLikeAnimView.setVisibility(View.GONE);
            unLikeLeftSet.cancel();
            unLikeRightSet.cancel();
        }
        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(unLikeLeftSet, unLikeRightSet);
        set.playTogether(unLikeLeftSet, unLikeRightSet);
        set.start();
    }

    private void createUnLikeAnimation() {
        if (unLikeLeftSet == null) {
//            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(likeLeftAnimView, "translationY", ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 500);
            ObjectAnimator viewTranslateAnimation = ObjectAnimator.ofFloat(unLikeAnimView, "translationY", ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 100);

            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeLeftAnimView, "rotationX", 0.0F, -15.0F);
            unLikeLeftSet = new AnimatorSet();
//            unLikeLeftSet.setInterpolator(new AccelerateInterpolator());
            unLikeLeftSet.setDuration(2000);
            unLikeLeftSet.playTogether(viewTranslateAnimation, rotateAnimation);
            unLikeLeftSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    likFullAnimView.setVisibility(View.GONE);
                    unLikeAnimView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    unLikeAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (unLikeRightSet == null) {
//            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(likeRightAnimView, "translationY",  ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 500);
            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeRightAnimView, "rotationX", 0.0F, 15.0F);
            unLikeRightSet = new AnimatorSet();
//            unLikeRightSet.setInterpolator(new AccelerateInterpolator());
            unLikeRightSet.setDuration(2000);
            unLikeRightSet.playTogether(rotateAnimation);
        }
    }
}
