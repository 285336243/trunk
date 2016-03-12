package com.socialtv.home;

import android.app.Activity;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.home.entity.Entry;
import com.socialtv.util.ImageUtils;
import com.socialtv.view.HorizontalGridView;

/**
 * Created by wlanjie on 14-6-23.
 * 主页中活动横滑ListView的Adapter
 */
public class ActivityAdapter extends SingleTypeAdapter<Entry> {

    private final static int ROOT = 0;
    private final static int IMAGE = 1;
    private final static int TITLE = 2;
    private final Activity activity;
    private final int width;
    private final int height;

    /**
     * Create adapter
     *
     * @param activity
     */
    public ActivityAdapter(Activity activity, final int width) {
        super(activity, R.layout.home_activity_item);
        this.activity = activity;
        this.width = (int) (width * 0.3 * 10 / 8);
        this.height = (int) ((width * 0.425 ));
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
                R.id.home_activity_item_root,
                R.id.home_activity_item_image,
                R.id.home_activity_item_title
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, Entry item) {
        if (item != null) {
            HorizontalGridView.LayoutParams params = null;
            if (position == 0 || position == getCount() - 1) {
                params = new HorizontalGridView.LayoutParams(width + Utils.dip2px(activity, 6), (height - 12) / 2);
            } else {
                params = new HorizontalGridView.LayoutParams(width, (height - 12) / 2);
            }
            view(ROOT).setLayoutParams(params);

            FrameLayout.LayoutParams imageParams;
            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT);
            if (position == 0) {
                imageParams = new FrameLayout.LayoutParams(width, height );
            } else {
                imageParams = new FrameLayout.LayoutParams(width, (height - 12) / 2);
            }

            if (position == 0) {
                imageParams.gravity = Gravity.RIGHT;
                textParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            } else if (position == getCount() - 1) {
                imageParams.gravity = Gravity.LEFT;
                textParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
            } else {
                textParams.gravity = Gravity.BOTTOM;
            }
            imageView(IMAGE).setLayoutParams(imageParams);

            view(TITLE).setLayoutParams(textParams);
            view(TITLE).setPadding(4, 2, 4, 2);

            ImageLoader.getInstance().displayImage(item.getImg(), imageView(IMAGE), ImageUtils.imageLoader(activity, 6));
            setText(TITLE, item.getName());
        }
    }
}
