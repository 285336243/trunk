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
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.Intents;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.feed.entity.Follow;
import com.socialtv.feed.entity.OthersFeedHeader;
import com.socialtv.feed.entity.PersionFeed;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.PrivateLetterDetailActivity;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.personcenter.PhotoAlbumActivity;
import com.socialtv.publicentity.Topic;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.topic.TopicDetailActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.view.TranslateView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-3.
 * 查看别人的动态页面
 */
public class OthersFeedActivity extends SingleListActivity<PersionFeed> {

    private final static int HEADER_ID = 100;

    @Inject
    private FeedServices services;

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    @Inject
    private Activity activity;

    private PersionFeedAdapter adapter;

    @InjectView(R.id.others_dyanmic_header_layout)
    private TranslateView headerView;

    @InjectView(R.id.others_dyanmic_header_image)
    private ImageView headerImageView;

    @InjectView(R.id.others_dyanmic_avatar)
    private ImageView headerAvatarImageView;

    @InjectView(R.id.others_dyanmic_nickname)
    private TextView nicknameTextView;

    @InjectView(R.id.others_dynamic_gender)
    private ImageView genderImageView;

    @InjectView(R.id.others_dyanmic_givenscore)
    private TextView scoreTextView;

    @InjectView(R.id.others_dyanmic_avatar_nickname_layout)
    private View avatarNicknameView;

    @InjectView(R.id.others_dynamic_badge_layout)
    private LinearLayout badgeLayout;

    private View privateLetterView;

    private TextView followTextView;

    private TextView followCountTextView;

    private TextView fansCountTextView;

    private TextView signatureTextView;

    private View picsLayout;

    private ImageView picsOneImageView;

    private ImageView picsTwoImageView;

    private ImageView picsThreeImageView;

    private View picsLineView;

    private List<Topic> feedItems = new ArrayList<Topic>();

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

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int mMinHeaderTranslation = -headerView.getMeasuredHeight() + getSupportActionBar().getHeight();
            int y = getScrollY();
            headerView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
            View topChild = view.getChildAt(0);
            if (topChild == null) {
                onNewScroll(0, -Math.max(-y, mMinHeaderTranslation));
            } else {
                onNewScroll(-topChild.getTop(), -Math.max(-y, mMinHeaderTranslation));
            }
        }
    };

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
        public void onLoadFinished(Loader<OthersFeedHeader> userLoader, final OthersFeedHeader header) {
            if (header != null) {
                show(headerView);
                final User user = header.getUser();
                if (user != null) {
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

                    if (user.getIsFollow() == 1) {
                        followTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_following_tomato), null, null, null);
                        followTextView.setText(getString(R.string.then_follow));
                    } else {
                        followTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_profollow_tomato), null, null, null);
                        followTextView.setText(getString(R.string.follow));
                    }

                    followCountTextView.setText(String.format(getResources().getString(R.string.follow_count), user.getFollowCnt()));
                    fansCountTextView.setText(String.format(getResources().getString(R.string.fans_count), user.getFansCnt()));

                    if (user.getPics() != null && !user.getPics().isEmpty()) {
                        for (int i = 0; i < user.getPics().size(); i++) {
                            if (i == 0) {
                                ImageLoader.getInstance().displayImage(user.getPics().get(0).getImg(), picsOneImageView, ImageUtils.imageLoader(activity, 0));
                            } else if (i == 1) {
                                ImageLoader.getInstance().displayImage(user.getPics().get(1).getImg(), picsTwoImageView, ImageUtils.imageLoader(activity, 0));
                            } else if (i == 2) {
                                ImageLoader.getInstance().displayImage(user.getPics().get(2).getImg(), picsThreeImageView, ImageUtils.imageLoader(activity, 0));
                            }
                        }
                    } else {
                        hide(picsLayout);
                        hide(picsLineView);
                    }

                    picsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intents(activity, PhotoAlbumActivity.class).add(IConstant.USER_ID, user.getUserId()).toIntent());
                        }
                    });

                    privateLetterView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (LoginUtil.isLogin(activity)) {
                                    startActivity(new Intents(activity, PrivateLetterDetailActivity.class).add(IConstant.USER_ID, header.getUser().getUserId())
                                            .add(IConstant.USER_NAME, header.getUser().getNickname()).toIntent());
                                } else {
                                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    followTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (LoginUtil.isLogin(activity)) {
                                followRequest(header.getUser());
                            } else {
                                startActivity(new Intents(activity, LoginActivity.class).toIntent());
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<OthersFeedHeader> userLoader) {

        }
    };

    private void followRequest(final User user) {
        if (user.getIsFollow() == 0) {
            followTextView.setText(getString(R.string.then_follow));
            followTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_profollowing_tomato), null, null, null);
            user.setIsFollow(1);
            new AbstractRoboAsyncTask<Follow>(activity){
                @Override
                protected Follow run(Object data) throws Exception {
                    return (Follow) HttpUtils.doRequest(services.createOthersFollowRequest(user.getUserId())).result;
                }

                @Override
                protected void onSuccess(Follow follow) throws Exception {
                    super.onSuccess(follow);
                    if (follow != null) {
                        if (follow.getResponseCode() == 0) {
                            fansCountTextView.setText(String.format(getString(R.string.fans_count), follow.getFansCnt()));
                                setResult(RESULT_OK);
                        } else {
                            ToastUtils.show(activity, follow.getResponseMessage());
                        }
                    }
                }
            }.execute();
        } else {
            new AbstractRoboAsyncTask<Follow>(activity){
                @Override
                protected Follow run(Object data) throws Exception {
                    return (Follow) HttpUtils.doRequest(services.createOthersUnFollowRequest(user.getUserId())).result;
                }

                @Override
                protected void onSuccess(Follow follow) throws Exception {
                    super.onSuccess(follow);
                    if (follow != null) {
                        if (follow.getResponseCode() == 0) {
                            fansCountTextView.setText(String.format(getString(R.string.fans_count), follow.getFansCnt()));
                            setResult(RESULT_OK);
                        } else {
                            ToastUtils.show(activity, follow.getResponseMessage());
                        }
                    }
                }
            }.execute();
            followTextView.setText(getString(R.string.follow));
            followTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_profollow_tomato), null, null, null);
            user.setIsFollow(0);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.others_dynamic_list;
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
        View v = getLayoutInflater().inflate(R.layout.others_feed_header, null);
        View emptyView = v.findViewById(R.id.others_feed_header_empty_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (metrics.widthPixels * 0.7) - Utils.dip2px(activity, 48)));
        privateLetterView = v.findViewById(R.id.others_feed_header_private_letter_layout);
        followTextView = (TextView) v.findViewById(R.id.others_feed_header_follow_text);
        followCountTextView = (TextView) v.findViewById(R.id.others_feed_header_follow_count);
        fansCountTextView = (TextView) v.findViewById(R.id.others_feed_header_fans_count);
        signatureTextView = (TextView) v.findViewById(R.id.others_feed_header_signature);
        picsLineView = v.findViewById(R.id.others_feed_header_pics_line);
        picsLayout = v.findViewById(R.id.others_feed_header_pics_layout);
        picsOneImageView = (ImageView) v.findViewById(R.id.others_feed_pics_one);
        picsTwoImageView = (ImageView) v.findViewById(R.id.others_feed_pics_two);
        picsThreeImageView = (ImageView) v.findViewById(R.id.others_feed_pics_three);
        int width = (metrics.widthPixels - Utils.dip2px(activity, 64)) / 3;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width / 4 * 3);
        picsOneImageView.setLayoutParams(params);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width / 4 * 3);
        layoutParams.leftMargin = Utils.dip2px(activity, 8);
        picsTwoImageView.setLayoutParams(layoutParams);
        picsThreeImageView.setLayoutParams(layoutParams);
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
                    feedItems.addAll(data.getTopics());
                    adapter.setItems(feedItems, "topics");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED && data != null) {
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
