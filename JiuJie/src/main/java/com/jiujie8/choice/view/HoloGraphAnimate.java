package com.jiujie8.choice.view;

import android.view.animation.Interpolator;

import com.nineoldandroids.animation.Animator;

/**
 * Created by DouglasW on 6/8/2014.
 */
public interface HoloGraphAnimate {

    final int ANIMATE_NORMAL = 0;
    final int ANIMATE_INSERT = 1;
    final int ANIMATE_DELETE = 2;
    int getDuration();
    void setDuration(int duration);

    Interpolator getInterpolator();
    void setInterpolator(Interpolator interpolator);

    boolean isAnimating();
    boolean cancelAnimating();
    void animateToGoalValues();
    void setAnimationListener(Animator.AnimatorListener animationListener);
}
