package com.socialtv.program;

import android.app.Activity;
 import android.content.Intent;
 import android.os.Bundle;
 import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
 import android.util.DisplayMetrics;
 import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
 import android.widget.ImageView;
 import android.widget.ListView;

 import com.android.volley.Request;
 import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
 import com.google.inject.Inject;
 import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.socialtv.R;
 import com.socialtv.core.Intents;
 import com.socialtv.core.MultiTypeRefreshListFragment;
 import com.socialtv.core.ToastUtils;
 import com.socialtv.core.Utils;
 import com.socialtv.personcenter.LoginActivity;
 import com.socialtv.program.entity.Program;
 import com.socialtv.publicentity.Topic;
 import com.socialtv.topic.SubmitTopicActivity;
 import com.socialtv.topic.TopicDetailActivity;
 import com.socialtv.util.IConstant;
 import com.socialtv.util.LoginUtil;

 import java.util.ArrayList;
 import java.util.List;

 import roboguice.inject.InjectView;

/**
  * Created by wlanjie on 14-7-25.
 * 节目组中话题的Fragment
  */
 public class ProgramTopicFragment extends MultiTypeRefreshListFragment<Program> {

     private String programId;

     @Inject
     private ProgramServices services;

     private ProgramTopicAdapter adapter;

     @InjectView(R.id.program_start_topic)
     private View startSubmitTopicView;

     private BannerView bannerView;

     private View emptyView;

     private boolean isResult = false;

     private final List<Topic> groupTopicItems = new ArrayList<Topic>();

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
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         Bundle bundle = getArguments();
         if (bundle != null) {
             programId = bundle.getString(IConstant.PROGRAM_ID);
         }
         adapter = new ProgramTopicAdapter(this);
     }

     public void setBannerView(final BannerView bannerView) {
         this.bannerView = bannerView;
     }

     @Override
     protected int getContentView() {
         return R.layout.program_topic_list;
     }



     @Override
     public void onViewCreated(View view, Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
         refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
         startSubmitTopicView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (LoginUtil.isLogin(getActivity())) {
                     startActivityForResult(new Intents(getActivity(), SubmitTopicActivity.class).add(IConstant.PROGRAM_ID, programId).toIntent(), 0);
                 } else {
                     startActivity(new Intents(getActivity(), LoginActivity.class).toIntent());
                 }
             }
         });

         setOnScrollListener(new AbsListView.OnScrollListener() {
             @Override
             public void onScrollStateChanged(AbsListView view, int scrollState) {
             }

             @Override
             public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                 if (bannerView != null && ProgramTopicFragment.this.getUserVisibleHint()) {
                     int mMinHeaderTranslation = -bannerView.getMeasuredHeight() + Utils.dip2px(getActivity(), 52);
                     int y = getScrollY();
                     bannerView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
                 }
             }
         });
     }

     private int getScrollY() {
         View v = listView.getChildAt(0);
         if (v == null)
             return 0;
         int positon = listView.getFirstVisiblePosition();
         int top = v.getTop();
         int y = 0;
         if (positon - 1 >= 1) {
             y = bannerView.getHeight();
         }
         return y + (-top + (positon - 1) * v.getHeight());
     }

     public void scrollView(final int y) {
         if (listView != null && bannerView != null) {

             int maxHeight = listView.getMeasuredHeight();
             int showHeight = 0;
             int firstPosition = listView.getFirstVisiblePosition();
             int lastPostion = listView.getLastVisiblePosition();
             int count = lastPostion-firstPosition+1;
             for(int i=0;i<count;i++){
                 View v = listView.getChildAt(i);
                 if(v != null){
                     showHeight +=v.getMeasuredHeight();
                 }
             }
             if(adapter.isEmpty()){
                 bannerView.setTranslationValue(0, y);
             }else if(showHeight>=maxHeight) {
                 bannerView.setTranslationValue(0, y);
                 listView.setSelectionFromTop(0, y);
             }else{
                 bannerView.setTranslationValue(0,0);
             }

 //            if (this.items.isEmpty() ) {
 //
 //            }
         }

     }

     public void refreshList() {
         hasMore = false;
         requestPage = 1;
         groupTopicItems.clear();
         if (bannerView != null)
             bannerView.setTranslationValue(0, 0);
         listView.setSelectionFromTop(0, 0);
         refresh();
     }

     /**
      * Create Request
      *
      * @return
      */
     @Override
     protected Request<Program> createRequest() {
         return services.createProgramListRequest(programId, requestPage, requestCount);
     }

     @Override
     public void onLoadFinished(Loader<Program> listLoader, Program data) {
         super.onLoadFinished(listLoader, data);
         if (data != null) {
             if (data.getResponseCode() == 0) {
                 ProgramTopicAdapter adapter = (ProgramTopicAdapter) getListAdapter().getWrappedAdapter();
                 if (data.getTopics() != null && !data.getTopics().isEmpty()) {
                     this.items = data.getTopics();
                     //设置isResult是为了清除老的数据,如果发布话题成功不调用clear,新发布的话题不会显示的
                     if (isResult) {
                         groupTopicItems.clear();
                         adapter.clear();
                         isResult = false;
                     }
                     groupTopicItems.addAll(data.getTopics());
                     adapter.setItems(groupTopicItems, "topic");
                     if (data.getTopics().size() < requestCount) {
                         loadingIndicator.setVisible(false);
                     }
                 } else {
                     hasMore = true;
                     if (!this.items.isEmpty()) {
                         loadingIndicator.loadingAllFinish();
                     } else {
                         loadingIndicator.setVisible(false);
                         List<Topic> topics = new ArrayList<Topic>();
                         Topic topic = new Topic();
                         topic.setMessage("暂无话题");
                         topics.add(topic);
                         this.items = topics;
                         adapter.addItems(topics, "empty");
                     }

                 }
             } else {
                 ToastUtils.show(getActivity(), data.getResponseMessage());
             }
         }
         showList();
         show(startSubmitTopicView);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == Activity.RESULT_OK && resultCode != Activity.RESULT_CANCELED) {
             isResult = true;
             requestPage = 1;
             forceRefresh();
         }
     }

     /**
      * Create adapter to display items
      *
      * @return adapter
      */
     @Override
     protected MultiTypeAdapter createAdapter() {
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
         emptyView = new View(getActivity());
         final DisplayMetrics metrics = new DisplayMetrics();
         getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
         emptyView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, metrics.widthPixels / 2 + Utils.dip2px(getActivity(), 52)));
         return emptyView;
     }

     @Override
     public void onDestroyView() {
         getListAdapter().removeHeader(emptyView);
         super.onDestroyView();
     }

     /**
      * Get error message to display for exception
      *
      * @param exception
      * @return string resource id
      */
     @Override
     protected int getErrorMessage(Exception exception) {
         return R.string.topic_error;
     }

     /**
      * Get resource id of {@link String} to display when loading
      *
      * @return string resource id
      */
     @Override
     protected int getLoadingMessage() {
         return R.string.star_topic_loading;
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
