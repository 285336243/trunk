package com.socialtv.program;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by wlanjie on 14-7-28.
 */
public class BannerView extends LinearLayout {

    private Scroller scroller;
    private Context context;

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (scroller == null) {
            final DecelerateInterpolator interpolator = new DecelerateInterpolator();
            scroller = new Scroller(context, interpolator);
        }
    }

    private float mTranslationX = 0;
    private float mTranslationY = 0;
    private boolean mWillUseSDKTranslation = true;

//    @Override
//    protected void dispatchDraw(final Canvas canvas) {
//        if (mWillUseSDKTranslation == false) {
//            // keep track of the current state
//            canvas.save(Canvas.MATRIX_SAVE_FLAG);
//            // translate
//            canvas.translate(mTranslationX, mTranslationY);
//            // draw it
//            super.dispatchDraw(canvas);
//            // restore to the previous state
//            canvas.restore();
//        } else {
//            super.dispatchDraw(canvas);
//        }
//    }
//
//    public void setTranslationValue(final float aTranslationX, final float aTranslationY) {
//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            mWillUseSDKTranslation = false;
//            mTranslationX = aTranslationX;
//            mTranslationY = aTranslationY;
//            invalidate();
//        } else {
//            setTranslationX(aTranslationX);
//            setTranslationY(aTranslationY);
//        }
//    }

//    @Override
//    protected void dispatchDraw(final Canvas canvas) {
//        if (mWillUseSDKTranslation == false) {
//            // keep track of the current state
//            canvas.save(Canvas.MATRIX_SAVE_FLAG);
//            // translate
//            canvas.translate(mTranslationX, mTranslationY);
//            // draw it
//            super.dispatchDraw(canvas);
//            // restore to the previous state
//            canvas.restore();
//        } else {
//            super.dispatchDraw(canvas);
//        }
//    }

    @TargetApi(11)
    public void setTranslationValue(final float aTranslationX, final float aTranslationY) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mWillUseSDKTranslation = false;
            mTranslationX = -aTranslationX;
            mTranslationY = -aTranslationY;
            //invalidate();
            this.scrollTo((int)-aTranslationX, (int)-aTranslationY);
        } else {
            setTranslationX(aTranslationX);
            setTranslationY(aTranslationY);
        }
    }

//    @Override
//    public void computeScroll() {
//        if (scroller.computeScrollOffset()) {
//            scrollTo(scroller.getCurrX(), scroller.getCurrY());
//            postInvalidate();
//        }
//        super.computeScroll();
//    }
//
//    public int getCurrY() {
//        return scroller.getCurrY();
//    }
//
//    public void beginScroller() {
//        //getHeight() - Utils.dip2px(context, 52)
//        scroller.startScroll(0, 0, 0, getHeight() - Utils.dip2px(context, 52), 1000);
//        invalidate();
//    }
}
