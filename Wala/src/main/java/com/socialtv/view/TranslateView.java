package com.socialtv.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by wlanjie on 14-7-4.
 */
public class TranslateView extends FrameLayout {

    public TranslateView(Context context) {
        super(context);
    }

    public TranslateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mTranslationX = 0;
    private float mTranslationY = 0;
    private boolean mWillUseSDKTranslation = true;

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        if (mWillUseSDKTranslation == false) {
            // keep track of the current state
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            // translate
            canvas.translate(mTranslationX, mTranslationY);
            // draw it
            super.dispatchDraw(canvas);
            // restore to the previous state
            canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @TargetApi(11)
    public void setTranslationValue(final float aTranslationX, final float aTranslationY) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mWillUseSDKTranslation = false;
            mTranslationX = aTranslationX;
            mTranslationY = aTranslationY;
            invalidate();
        } else {
            setTranslationX(aTranslationX);
            setTranslationY(aTranslationY);
        }
    }
}
