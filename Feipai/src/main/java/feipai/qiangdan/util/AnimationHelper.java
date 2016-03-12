package feipai.qiangdan.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {

    // 从右边出来滑动效果
    public static Animation inFromRight() {
        Animation inFRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFRight.setDuration(500);
        inFRight.setInterpolator(new AccelerateInterpolator());
        return inFRight;
    }

    // 从左边出来滑动效果
    public static Animation inFromLeft() {
        Animation inFLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFLeft.setDuration(500);
        inFLeft.setInterpolator(new AccelerateInterpolator());
        return inFLeft;
    }

    // 从左边出去效果
    public static Animation outToLeft() {
        Animation outToLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToLeft.setDuration(500);
        outToLeft.setInterpolator(new AccelerateInterpolator());
        return outToLeft;
    }

    // 从右边出去
    public static Animation outToRight() {
        Animation outTRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outTRight.setDuration(500);
        outTRight.setInterpolator(new AccelerateInterpolator());
        return outTRight;
    }

}