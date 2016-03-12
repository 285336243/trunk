package com.socialtv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.socialtv.core.ViewPager;


/**
 * Created by wlanjie on 14-5-30.
 */
public class TabHostPager extends ViewPager {
    /**
     * @param context
     */
    public TabHostPager(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public TabHostPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
