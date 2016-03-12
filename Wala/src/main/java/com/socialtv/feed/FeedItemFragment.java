package com.socialtv.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.socialtv.R;
import com.socialtv.core.SingleTypeRefreshListFragment;
import com.socialtv.feed.entity.Feed;
import com.socialtv.publicentity.Topic;
import com.socialtv.util.IConstant;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-10-9.
 * 动态中的最新的Fragment
 */
public abstract class FeedItemFragment extends SingleTypeRefreshListFragment<Feed> {

    @Inject
    protected FeedServices services;

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

    protected List<Topic> topics = null;

    @Override
    protected int getContentView() {
        return R.layout.feed_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().registerReceiver(receiver, new IntentFilter(IConstant.DELETE_OK));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * 在话题详情里删除话题时的广播
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && IConstant.DELETE_OK.equals(intent.getAction())) {
            }
        }
    };

    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    @Override
    protected View adapterHeaderView() {
        return null;
    }

    @Override
    public void refresh() {
        if (!isUsable())
            return;

        getLoaderManager().restartLoader(loaderId, null, this);
    }

    /**
     * adapter删除item时的回调
     * @param topicId
     */
    public abstract void refreshList(final String topicId);

    /**
     * 父fragment的activityresult回调
     */
    public abstract void refreshResult();

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.feed_error;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.feed_loading;
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
        set.playTogether(unLikeLeftSet, unLikeRightSet);
        set.start();
    }

    private void createUnLikeAnimation() {
        if (unLikeLeftSet == null) {
            ObjectAnimator viewTranslateAnimation = ObjectAnimator.ofFloat(unLikeAnimView, "translationY",  ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 100);

            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeLeftAnimView, "rotationX", 0.0F, -15.0F);
            unLikeLeftSet = new AnimatorSet();
            unLikeLeftSet.setDuration(2000);
            unLikeLeftSet.playTogether( viewTranslateAnimation, rotateAnimation);
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
            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeRightAnimView, "rotationX", 0.0F, 15.0F);
            unLikeRightSet = new AnimatorSet();
            unLikeRightSet.setDuration(2000);
            unLikeRightSet.playTogether( rotateAnimation);
        }
    }
}
