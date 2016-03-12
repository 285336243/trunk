package com.socialtv.activity;

import android.annotation.TargetApi;
 import android.content.Intent;
 import android.os.Build;
 import android.os.Bundle;
 import android.support.v4.app.LoaderManager;
 import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
 import android.util.DisplayMetrics;
 import android.util.TypedValue;
 import android.view.View;
 import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
 import android.widget.FrameLayout;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.RadioGroup;

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
 import com.socialtv.activity.entitiy.Activity;
 import com.socialtv.activity.entitiy.ActivityDetail;
 import com.socialtv.core.FragmentProvider;
 import com.socialtv.core.Intents;
 import com.socialtv.core.MultiListActivity;
 import com.socialtv.core.ThrowableLoader;
 import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
 import com.socialtv.personcenter.LoginActivity;
 import com.socialtv.publicentity.Topic;
 import com.socialtv.topic.SubmitTopicActivity;
 import com.socialtv.util.IConstant;
 import com.socialtv.util.ImageUtils;
 import com.socialtv.util.LoginUtil;
 import com.socialtv.view.ExpandableTextView;
 import com.socialtv.view.TranslateView;

 import java.util.ArrayList;
 import java.util.List;

 import roboguice.inject.InjectExtra;
 import roboguice.inject.InjectView;

/**
  * Created by wlanjie on 14-6-25.
 *
 * 活动详情的页面
  */
 public class ActivityDetailActivity extends MultiListActivity<Activity> {

     private final static int HEADER_ID = 100;

     private final static int LATEST_ID = 200;

     /**
      * ==== List header weight
      */
     @InjectView(R.id.activity_header_image)
     private ImageView headerImage;

     @InjectView(R.id.participation_button)
     private View participationView;

     private ExpandableTextView expandableTextView;

     @InjectView(R.id.activity_header_image_layout)
     private TranslateView headerImageLayout;

     @InjectExtra(value = IConstant.ACTIVITY_ID, optional = true)
     private String activityId;

     @InjectView(R.id.activity_start_topic)
     private View startSubmitTopicView;

     @Inject
     private android.app.Activity activity;

     @Inject
     private ActivityServices services;

     private int hotPage = 1;

     private DisplayMetrics metrics;

     private List<Topic> hotItems = new ArrayList<Topic>();

     private final List<Topic> emptyItems = new ArrayList<Topic>();

     private boolean hotHasMore = false;

     private int mActionBarHeight;

     private TypedValue mTypedValue = new TypedValue();

     private boolean isHotChecked = false;

     private boolean isNewChecked = true;

     private boolean isCheckedChange = false;

     private boolean isLastItemVisible = false;

     private boolean isResult = false;

     private String type = "TOPIC_ACTIVITY";

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
         headerImage.setLayoutParams(params);

         setOnScrollListener(scrollListener);

         startSubmitTopicView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (LoginUtil.isLogin(activity)) {
                     if ("TOPIC_ACTIVITY".equals(type)) {
                         startActivityForResult(new Intents(activity, SubmitTopicActivity.class).add(IConstant.ACTIVITY_ID, activityId).toIntent(), 0);
                     } else if ("CAPTURE_ACTIVITY".equals(type)) {
                         startActivityForResult(new Intents(activity, ScreenShotActivity.class).add(IConstant.ACTIVITY_ID, activityId).toIntent(), 1);
                     }
                 } else {
                     startActivity(new Intents(activity, LoginActivity.class).toIntent());
                 }
             }
         });

         participationView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (LoginUtil.isLogin(activity)) {
                     if ("TOPIC_ACTIVITY".equals(type)) {
                         startActivityForResult(new Intents(activity, SubmitTopicActivity.class).add(IConstant.ACTIVITY_ID, activityId).toIntent(), 0);
                     } else if ("CAPTURE_ACTIVITY".equals(type)) {
                         startActivityForResult(new Intents(activity, ScreenShotActivity.class).add(IConstant.ACTIVITY_ID, activityId).toIntent(), 1);
                     }
                 } else {
                     startActivity(new Intents(activity, LoginActivity.class).toIntent());
                 }
             }
         });
     }

     @Override
     public void onDestroy() {
         super.onDestroy();
         hotItems.clear();
         hotItems = null;
         emptyItems.clear();
     }

     @Override
     protected int getContentView() {
         return R.layout.pinned_header_list;
     }

    /**
     * 滚动悬停的监听
     */
     AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(AbsListView view, int scrollState) {

         }

         @Override
         public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                 int mMinHeaderTranslation = -headerImageLayout.getMeasuredHeight() + getActionBarHeight();
                 int y = getScrollY();
                 headerImageLayout.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
                 View topChild = view.getChildAt(0);
                 if (topChild == null) {
                     onNewScroll(0, -Math.max(-y, mMinHeaderTranslation));
                 } else {
                     onNewScroll(-topChild.getTop(), -Math.max(-y, mMinHeaderTranslation));
                 }
 //                if(Math.max(-y, mMinHeaderTranslation) == mMinHeaderTranslation){
 //                    participationView.setAlpha(0.0f);
 //                }else{
 //                    participationView.setAlpha(1.0f);
 //                }
         }
     };

     @TargetApi(11)
     private void onNewScroll(int scrollPosition, int y) {
         int currentHeaderHeight = headerImageLayout.getHeight();
         int headerHeight = currentHeaderHeight - getSupportActionBar().getHeight();
 //        float ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
 //        int newAlpha = (int) (ratio * 255);
 //        editDrawable.setAlpha(newAlpha);
         float alpha = (float) (headerHeight - y) / headerHeight;
         if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
             final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
             animation.setDuration(1);
             animation.setFillAfter(true);
             participationView.startAnimation(animation);
         } else {
             participationView.setAlpha(alpha);
         }
     }

    /**
     * 获取actionbar的高度
     * @return
     */
     public int getActionBarHeight() {
         if (mActionBarHeight != 0) {
             return mActionBarHeight;
         }
         getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
         mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
         return mActionBarHeight;
     }

    /**
     * 获取控件距离顶部的距离
     * @return
     */
     private int getScrollY() {
         View v = getListView().getChildAt(0);
         if (v == null)
             return 0;
         int positon = getListView().getFirstVisiblePosition();
         int top = v.getTop();
         int y = 0;
         if (positon - 1 >= 1) {
             y = headerImageLayout.getHeight();
         }
         return y + (-top + (positon - 1) * v.getHeight());
     }

    /**
     * 刷新最新的列表
     * @return
     */
     public ActivityDetailActivity restartNewLoader() {
         refresh();
         return this;
     }

    /**
     * 刷新最热的列表
     * @return
     */
     public ActivityDetailActivity restartHotLoader() {
         getSupportLoaderManager().restartLoader(LATEST_ID, null, hotCallbacks);
         return this;
     }

     /**
      * 最热loader
      */
     LoaderManager.LoaderCallbacks<Activity> hotCallbacks = new LoaderManager.LoaderCallbacks<Activity>() {
         @Override
         public Loader<Activity> onCreateLoader(int id, Bundle args) {
             return new ThrowableLoader<Activity>(activity) {
                 @Override
                 public Activity loadData() throws Exception {
                     return (Activity) HttpUtils.doRequest(services.createHotRequest(activityId, hotPage, requestCount)).result;
                 }
             };
         }

         @Override
         public void onLoadFinished(Loader<Activity> loader, final Activity data) {
             setSupportProgressBarIndeterminateVisibility(false);
             if (data != null) {
                 if (data.getResponseCode() == 0) {
                     if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                         final ActivityAdapter adapter = (ActivityAdapter) getListAdapter().getWrappedAdapter();
                         if (isCheckedChange && !isLastItemVisible) {
                             if (adapter != null)
                                 adapter.clear();
                             hotItems = data.getTopics();
                             adapter.addItem(data.getTopics(), "");
                         }

                         if (isResult) {
                             adapter.clear();
                             isResult = false;
                         }

                     if (data.getTopics().size() < requestCount)
                         loadingIndicator.setVisible(false);
                     } else {
                         hotHasMore = true;
                         if (!hotItems.isEmpty()) {
                             loadingIndicator.loadingAllFinish();
                         } else {
                             loadingIndicator.setVisible(false);
                             emptyItems.clear();
                             Topic item = new Topic();
                             item.setMessage("暂无任何话题");
                             emptyItems.add(item);

                             final ActivityAdapter adapter = (ActivityAdapter) getListAdapter().getWrappedAdapter();
                             if (adapter != null) {
                                 adapter.clear();
                                 adapter.addItem(emptyItems, "empty");
                             }
                             hotItems = emptyItems;
                         }
                     }
                 } else {
                     ToastUtils.show(activity, data.getResponseMessage());
                 }
             }
         }

         @Override
         public void onLoaderReset(Loader<Activity> loader) {

         }
     };

     /**
      * 头部loader
      */
     LoaderManager.LoaderCallbacks<ActivityDetail> headerCallbacks = new LoaderManager.LoaderCallbacks<ActivityDetail>() {
         @Override
         public Loader<ActivityDetail> onCreateLoader(final int id, Bundle args) {
             return new ThrowableLoader<ActivityDetail>(activity) {
                 @Override
                 public ActivityDetail loadData() throws Exception {
                     return (ActivityDetail) HttpUtils.doRequest(services.createHeaderRequest(activityId)).result;
                 }
             };
         }

         @Override
         public void onLoadFinished(Loader<ActivityDetail> loader, ActivityDetail data) {
             if (data != null) {
                 if (data.getResponseCode() == 0) {
                     if (data.getActivity() != null) {
                         type = data.getActivity().getType();
                         ImageLoader.getInstance().displayImage(data.getActivity().getImg(), headerImage, ImageUtils.imageLoader(activity, 0));
                         expandableTextView.setText(data.getActivity().getDesc());
                     }
                 } else {
                     ToastUtils.show(activity, data.getResponseMessage());
                 }
             }
         }

         @Override
         public void onLoaderReset(Loader<ActivityDetail> loader) {

         }
     };

     /**
      * Create adapter to display items
      *
      * @return adapter
      */
     protected MultiTypeAdapter createAdapter() {
         return new ActivityAdapter(this);
     }

     /**
      * Is add adapter header
      *
      * @return
      */
     protected boolean isAddAdapterHeader() {
         return true;
     }

     /**
      * Adapter header view
      *
      * @return
      */
     protected View adapterHeaderView() {
         View v = getLayoutInflater().inflate(R.layout.activity_header, null);
         final View emptyView = v.findViewById(R.id.header_empty_view);
         DisplayMetrics metrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(metrics);
         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels, (int) (metrics.widthPixels * 0.7) - getActionBarHeight());
         emptyView.setLayoutParams(params);
         expandableTextView = (ExpandableTextView) v.findViewById(R.id.expand_text_view);
         RadioGroup group = (RadioGroup) v.findViewById(R.id.new_hot_group);
         group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(RadioGroup group, int checkedId) {
                 isCheckedChange = true;
                 requestPage = 1;
                 hotPage = 1;
                 hasMore = false;
                 hotHasMore = false;
                 isLastItemVisible = false;
                 loadingIndicator.setVisible(true);
                 loadingIndicator.setMessage(getLoadingMessage());
                 emptyItems.clear();
                 items.clear();
                 setSupportProgressBarIndeterminateVisibility(true);

                 isNewChecked = checkedId == R.id.new_buuton ? true : false;
                 isHotChecked = checkedId == R.id.hot_button ? true : false;
                 switch (checkedId) {
                     case R.id.new_buuton:
                         restartNewLoader();
                         break;
                     case R.id.hot_button:
                         Loader<Activity> loader = getSupportLoaderManager().getLoader(LATEST_ID);
                         if (loader == null) {
                             getSupportLoaderManager().initLoader(LATEST_ID, null, hotCallbacks);
                         } else {
                             restartHotLoader();
                         }
                         break;
                 }
             }
         });
         return v;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 finish();
                 break;
         }
         return super.onOptionsItemSelected(item);
     }

     /**
      * Create Request
      *
      * @return
      */
     @Override
     protected Request<Activity> createRequest() {
         return services.createLatestRequest(activityId, requestPage, requestCount);
     }

     @Override
     public void onLoadFinished(Loader<Activity> loader, Activity data) {
         super.onLoadFinished(loader, data);
         setSupportProgressBarIndeterminateVisibility(false);
         show(headerImageLayout);
         if (data != null) {
             if (data.getResponseCode() == 0) {
                 if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                     final ActivityAdapter adapter = (ActivityAdapter) getListAdapter().getWrappedAdapter();
                     if (isCheckedChange) {
                         if (!isLastItemVisible) {
                             if (adapter != null)
                                 adapter.clear();
                         }
                     }
                     if (isResult) {
                         adapter.clear();
                         isResult = false;
                     }
                     this.items = data.getTopics();
                     adapter.addItem(data.getTopics(), "");
                     if (data.getTopics().size() < requestCount)
                         loadingIndicator.setVisible(false);
                 } else {
                     hasMore = true;
                     if (!this.items.isEmpty()) {
                         loadingIndicator.loadingAllFinish();
                     } else {
                         loadingIndicator.setVisible(false);
                         emptyItems.clear();
                         Topic item = new Topic();
                         item.setMessage("暂无任何话题");
                         emptyItems.add(item);

                         final ActivityAdapter adapter = (ActivityAdapter) getListAdapter().getWrappedAdapter();
                         if (adapter != null) {
                             adapter.clear();
                             adapter.addItem(emptyItems, "empty");
                         }
                         this.items = emptyItems;
                     }
                 }
             } else {
                 ToastUtils.show(activity, data.getResponseMessage());
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
         return R.string.activity_error;
     }

     /**
      * Get resource id of {@link String} to display when loading
      *
      * @return string resource id
      */
     @Override
     protected int getLoadingMessage() {
         return R.string.activity_loading;
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

    public void refreshList() {
        isResult = true;
        if (isNewChecked) {
            refresh();
        }
        if (isHotChecked) {
            restartHotLoader();
        }
    }

     @Override
     public void onLastItemVisible() {
         isLastItemVisible = true;
         if (isNewChecked) {
             if (hasMore)
                 return;
             if (getSupportLoaderManager().hasRunningLoaders())
                 return;
             requestPage++;
             refresh();
         }
         if (isHotChecked) {
             if (hotHasMore)
                 return;
             if (getSupportLoaderManager().hasRunningLoaders())
                 return;
             hotPage++;
             restartHotLoader();
         }
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
             isResult = true;
             if (isNewChecked) {
                 requestPage = 1;
                 refresh();
             } else if (isHotChecked) {
                 hotPage = 1;
                 restartHotLoader();
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
