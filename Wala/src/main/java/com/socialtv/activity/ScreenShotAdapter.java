package com.socialtv.activity;

import android.app.Activity;
import android.widget.FrameLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.util.ImageUtils;

/**
 * Created by wlanjie on 14-7-21.
 *
 * 截图的Adapter
 */
public class ScreenShotAdapter extends SingleTypeAdapter<String> {

    private final static int IMAGE = 0;
    private final static int MARK = 1;
    private int position = 0;

    private final Activity activity;

    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    @Inject
    public ScreenShotAdapter(Activity activity) {
        super(activity, R.layout.screen_shot_list_item);
        this.activity = activity;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.screen_shot_list_item_image,
                R.id.screen_shot_list_item_mark_image
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, String item) {
        if (position == this.position) {
            view(MARK).setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        } else {
            view(MARK).setBackgroundColor(activity.getResources().getColor(R.color.black_6));
        }
        view(IMAGE).setLayoutParams(new FrameLayout.LayoutParams(Utils.dip2px(activity, 96), Utils.dip2px(activity, 120)));
        view(MARK).setLayoutParams(new FrameLayout.LayoutParams(Utils.dip2px(activity, 96), Utils.dip2px(activity, 120)));
        ImageLoader.getInstance().displayImage(item, imageView(IMAGE), ImageUtils.imageLoader(activity, 0));
    }
}
