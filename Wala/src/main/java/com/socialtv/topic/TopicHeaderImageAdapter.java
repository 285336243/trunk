package com.socialtv.topic;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.AbsListView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.ImageUtils;

/**
 * Created by wlanjie on 14-7-1.
 */
public class TopicHeaderImageAdapter extends MultiTypeAdapter {

    private final Activity activity;
    private final int width;

    /**
     * Create adapter
     *
     * @param activity
     */
    public TopicHeaderImageAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.image;
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        return new int[] {
                R.id.imageview
        };
    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        if (item != null) {
            Pics pics = (Pics) item;
            view(0).setLayoutParams(new AbsListView.LayoutParams((width - Utils.dip2px(activity, 32)) / 3 , (int) (width * 0.225)));
            if (pics != null)
                ImageLoader.getInstance().displayImage(pics.getImg(), imageView(0), ImageUtils.imageLoader(activity, 0));
        }
    }
}
