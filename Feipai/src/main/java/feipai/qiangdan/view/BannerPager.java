package feipai.qiangdan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import feipai.qiangdan.core.ViewPager;

/**
 * Created by wlanjie on 14-5-30.
 */
public class BannerPager extends ViewPager {
    /**
     * @param context
     */
    public BannerPager(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public BannerPager(Context context, AttributeSet attrs) {
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
