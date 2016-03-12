package com.socialtv.star;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiListActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.publicentity.Topic;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.star.entity.Star;
import com.socialtv.star.entity.StarHeader;
import com.socialtv.topic.SubmitTopicActivity;
import com.socialtv.topic.TopicDetailActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.view.TranslateView;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-6-27.
 * 明星页面
 */
public class StarActivity extends MultiListActivity<Star> {

    private final static int HEADER_ID = 100;
    private final static int TOPICS_ID = 200;

    private final static int FOLLOW_MENU = 0;

    @InjectExtra(value = IConstant.STAR_ID, optional = true)
    private String starId;

    @Inject
    private Activity activity;

    @Inject
    private StarServices services;

    @InjectView(R.id.star_header_image)
    private ImageView headerImageView;

    @InjectView(R.id.star_avatar)
    private ImageView avatarImageView;

    @InjectView(R.id.star_nickname)
    private TextView nicknameTextView;

    @InjectView(R.id.star_title)
    private TextView titleTextView;

    @InjectView(R.id.star_header_layout)
    private TranslateView headerImageLayout;

    @InjectView(R.id.star_header_avatar_nickname_layout)
    private View avatarNicknameView;

    @InjectView(R.id.star_start_topic)
    private View startSubmitTopicView;

    @InjectView(R.id.star_badge)
    private LinearLayout badgeLayout;

    private int topicsPage = 1;

    private DisplayMetrics metrics;

    private List<Topic> starTopicsItems = new ArrayList<Topic>();

    private List<Topic> emptyItems = new ArrayList<Topic>();

    private boolean starTopicsHasMore = false;

    private boolean isTopicsChecked = false;

    private boolean isDynamicChecked = true;

    private boolean isCheckedChange = false;

    private boolean isLastItemVisible = false;

    private boolean isResult = false;

    private int isFollow = -1;

    @InjectView(R.id.topic_like_anim_full)
    private ImageView likFullAnimView;

    @InjectView(R.id.topic_un_like_anim_layout)
    private View unLikeAnimView;

    @InjectView(R.id.topic_like_anim_left)
    private ImageView likeLeftAnimView;

    @InjectView(R.id.topic_like_anim_right)
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

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(metrics.widthPixels, (int) (metrics.widthPixels * 0.7));
        headerImageView.setLayoutParams(params);

        setOnScrollListener(scrollListener);
        startSubmitTopicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginUtil.isLogin(activity)) {
                    startActivityForResult(new Intents(activity, SubmitTopicActivity.class).add(IConstant.STAR_ID, starId).toIntent(), 0);
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        starTopicsItems.clear();
        starTopicsItems = null;
    }

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(headerImageLayout.getVisibility() != View.VISIBLE){
                return;
            }
            int mMinHeaderTranslation = -headerImageLayout.getMeasuredHeight() + getSupportActionBar().getHeight();
            int y = getScrollY();
            headerImageLayout.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
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
        int currentHeaderHeight = headerImageLayout.getHeight();
        int headerHeight = currentHeaderHeight - getSupportActionBar().getHeight();
        float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);
//        editDrawable.setAlpha(newAlpha);
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
            y = headerImageLayout.getHeight();
        }
        return y + (-top + (positon - 1) * v.getHeight());
    }

    public StarActivity restartDynamicLoader() {
        refresh();
        return this;
    }

    public StarActivity restartTopicsLoader() {
        getSupportLoaderManager().restartLoader(TOPICS_ID, null, topicsCallbacks);
        return this;
    }

    LoaderManager.LoaderCallbacks<StarHeader> headerCallbacks = new LoaderManager.LoaderCallbacks<StarHeader>() {
        @Override
        public Loader<StarHeader> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<StarHeader>(activity) {
                @Override
                public StarHeader loadData() throws Exception {
                    return (StarHeader) HttpUtils.doRequest(services.createStarHeaderRequest(starId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<StarHeader> loader, StarHeader data) {
            if (data != null) {
                if (data.getCelebrity() != null) {
                    isFollow = data.getCelebrity().getIsfollow();
                    invalidateOptionsMenu();

                    if (data.getCelebrity().getBadges() != null && !data.getCelebrity().getBadges().isEmpty()) {
                        show(badgeLayout);
                        badgeLayout.removeAllViews();
                        for (UserBadge badge : data.getCelebrity().getBadges()) {
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

                    ImageLoader.getInstance().displayImage(data.getCelebrity().getBgImg(), headerImageView, ImageUtils.imageLoader(activity, 0));
                    ImageLoader.getInstance().displayImage(data.getCelebrity().getAvatar(), avatarImageView, ImageUtils.avatarImageLoader());
                    nicknameTextView.setText(data.getCelebrity().getName());
                    titleTextView.setText(data.getCelebrity().getTitle());
                    show(headerImageLayout);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<StarHeader> loader) {

        }
    };

    LoaderManager.LoaderCallbacks<Star> topicsCallbacks = new LoaderManager.LoaderCallbacks<Star>() {
        @Override
        public Loader<Star> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<Star>(activity) {
                @Override
                public Star loadData() throws Exception {
                    return (Star) HttpUtils.doRequest(services.createStarTopicsRequest(starId, topicsPage, requestCount)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Star> loader, Star data) {
            setSupportProgressBarIndeterminateVisibility(false);
            if (data != null) {
                if (data.getResponseCode() == 0) {
                    emptyItems.clear();

                    if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                        StarAdapter adapter = (StarAdapter) getListAdapter().getWrappedAdapter();
                        if (isCheckedChange) {
                            if (!isLastItemVisible) {
                                if (adapter != null)
                                    adapter.clear();
                            }
                            if (isResult) {
                                adapter.clear();
                                isRefresh = false;
                            }
                            starTopicsItems = data.getTopics();
                            adapter.addItem(data.getTopics(), "topics");
                        }
                        if (data.getTopics().size() < requestCount) {
                            loadingIndicator.setVisible(false);
                        }
                    } else {
                        starTopicsHasMore = true;
                        if (!starTopicsItems.isEmpty()) {
                            loadingIndicator.loadingAllFinish();
                        } else {
                            loadingIndicator.setVisible(false);
                            //设置空的adapter
                            Topic topic = new Topic();
                            topic.setMessage("暂无任何话题");
                            emptyItems.add(topic);
                            StarAdapter adapter = (StarAdapter) getListAdapter().getWrappedAdapter();
                            if (adapter != null) {
                                adapter.clear();
                                adapter.addItem(emptyItems, "empty");
                            }
                            starTopicsItems = emptyItems;
                        }
                    }
                } else {
                    ToastUtils.show(activity, data.getResponseMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Star> loader) {

        }
    };

    @Override
    protected int getContentView() {
        return R.layout.star_list;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new StarAdapter(this);
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
        View v = getLayoutInflater().inflate(R.layout.star_header, null);
        View emptyView = v.findViewById(R.id.star_empty_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (metrics.widthPixels * 0.7) - Utils.dip2px(activity, 48)));
        RadioGroup group = (RadioGroup) v.findViewById(R.id.dynamic_topics_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isCheckedChange = true;
                requestPage = 1;
                topicsPage = 1;
                hasMore = false;
                starTopicsHasMore = false;
                isLastItemVisible = false;
                loadingIndicator.setVisible(true);
                loadingIndicator.setMessage(getLoadingMessage());
                items.clear();
                starTopicsItems.clear();

                setSupportProgressBarIndeterminateVisibility(true);
                isDynamicChecked = checkedId == R.id.dynamic_buuton ? true : false;
                isTopicsChecked = checkedId == R.id.topics_button ? true : false;

                loadingIndicator.setVisible(true);
                switch (checkedId) {
                    case R.id.dynamic_buuton:
                        loadingIndicator.setMessage(getLoadingMessage());
                        restartDynamicLoader();
                        break;
                    case R.id.topics_button:
                        loadingIndicator.setMessage(getTopicLoadingMessage());
                        restartTopicsLoader();
                        break;
                }
            }
        });
        return v;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<Star> createRequest() {
        return services.createStarDynamicRequest(starId, requestPage, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<Star> loader, Star data) {
        super.onLoadFinished(loader, data);
        setSupportProgressBarIndeterminateVisibility(false);
        emptyItems.clear();
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                    StarAdapter adapter = (StarAdapter) getListAdapter().getWrappedAdapter();
                    if (isCheckedChange) {
                        if (!isLastItemVisible) {
                            if (adapter != null)
                                adapter.clear();
                        }
                    }
                    if (isResult) {
                        adapter.clear();
                        isRefresh = false;
                    }
                    this.items = data.getTopics();
                    adapter.addItem(data.getTopics(), "dynamic");
                    if (data.getTopics().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty()) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        loadingIndicator.setVisible(false);
                        //设置空的adapter
                        Topic topic = new Topic();
                        topic.setMessage("暂无任何动态");
                        emptyItems.add(topic);
                        StarAdapter adapter = (StarAdapter) getListAdapter().getWrappedAdapter();
                        if (adapter != null) {
                            adapter.clear();
                            adapter.addItem(emptyItems, "empty");
                        }
                        items = emptyItems;
                    }
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
        show(startSubmitTopicView);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.star_dynamic;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.star_dynamic_loading;
    }

    protected int getTopicLoadingMessage() {
        return R.string.star_topic_loading;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFollow == 0) {
            menu.add(Menu.NONE, FOLLOW_MENU, 0, "关注")
                    .setIcon(R.drawable.btn_follow_tomato)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else if (isFollow == 1) {
            menu.add(Menu.NONE, FOLLOW_MENU, 0, "关注")
                    .setIcon(R.drawable.btn_unfollow_tomato)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void followRequest() {
        if (LoginUtil.isLogin(activity)) {
            if (isFollow == 0) {
                isFollow = 1;
                invalidateOptionsMenu();
                new AbstractRoboAsyncTask<Response>(activity) {
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Response run(Object data) throws Exception {
                        return (Response) HttpUtils.doRequest(services.createStarFollowRequest(starId)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        setResult(RESULT_OK);
                    }
                }.execute();
            } else {
                isFollow = 0;
                invalidateOptionsMenu();
                new AbstractRoboAsyncTask<Response>(activity) {
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Response run(Object data) throws Exception {
                        return (Response) HttpUtils.doRequest(services.createStarUnFollowRequest(starId)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        setResult(RESULT_OK);
                    }
                }.execute();
            }
        } else {
            startActivity(new Intents(activity, LoginActivity.class).toIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
            case FOLLOW_MENU:
                followRequest();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {
        isResult = true;
        if (isDynamicChecked) {
            refresh();
        }
        if (isTopicsChecked) {
            restartTopicsLoader();
        }
    }

    @Override
    public void onLastItemVisible() {
        isLastItemVisible = true;
        if (isDynamicChecked) {
            if (hasMore)
                return;
            if (getSupportLoaderManager().hasRunningLoaders())
                return;
            requestPage++;
            refresh();
        }
        if (isTopicsChecked) {
            if (starTopicsHasMore)
                return;
            if (getSupportLoaderManager().hasRunningLoaders())
                return;
            topicsPage++;
            restartTopicsLoader();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            isResult = true;
            if (isDynamicChecked) {
                requestPage = 1;
                refresh();
            } else if (isTopicsChecked) {
                topicsPage = 1;
                restartTopicsLoader();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            if (isDynamicChecked) {
                Topic topics = (Topic) l.getItemAtPosition(position);
                startActivityForResult(new Intents(activity, TopicDetailActivity.class).add(IConstant.TOPIC_ID, topics.getId()).add(IConstant.USER_ID, topics.getUser().getUserId()).toIntent(), 0);
            }
        } catch (Exception e) {

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
