package com.shengzhish.xyj.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


/**
 * 动画类型
 */
public final class AnimationUtil {
    /**
     * view 横向进入退出动画  右进右出
     *
     * @param view    执行动画的view naer
     * @param isEnter true为view进入动画，false为view退出动画
     */
    public static void horizontalAnimation(View view, boolean isEnter) {
        TranslateAnimation translateAnimation;

        if (isEnter) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//         translateAnimation.setDuration(400);
        } else {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//         translateAnimation.setDuration(400);
        }
        translateAnimation.setDuration(400);
        view.startAnimation(translateAnimation);
    }

    /**
     * view 横向进入退出动画  左进右出
     *
     * @param view    执行动画的view naer
     * @param isEnter true为view进入动画，false为view退出动画
     */
    public static void horizontalAnimation2(View view, boolean isEnter) {
        TranslateAnimation translateAnimation;

        if (isEnter) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
//         translateAnimation.setDuration(400);
        } else {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
//         translateAnimation.setDuration(400);
        }
        translateAnimation.setDuration(400);
        view.startAnimation(translateAnimation);
    }

    /**
     * view 纵向进入退出动画  下进下出
     *
     * @param view    执行动画的view
     * @param isEnter true为view进入动画，false为view退出动画
     */
    public static void verticalAnimation(View view, boolean isEnter) {
        TranslateAnimation translateAnimation;

        if (isEnter) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//         translateAnimation.setDuration(400);
        } else {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
//         translateAnimation.setDuration(400);
        }
        translateAnimation.setDuration(400);
        view.startAnimation(translateAnimation);
    }
    /**
     * view 纵向进入退出动画  上进下出
     *
     * @param view    执行动画的view
     * @param isEnter true为view进入动画，false为view退出动画
     */
    public static void verticalAnimation2(View view, boolean isEnter) {
        TranslateAnimation translateAnimation;

        if (isEnter) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
//         translateAnimation.setDuration(400);
        } else {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                    0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
//         translateAnimation.setDuration(400);
        }
        translateAnimation.setDuration(400);
        view.startAnimation(translateAnimation);
    }


}
