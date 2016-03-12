package com.socialtv.program;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.program.entity.News;
import com.socialtv.util.ImageUtils;

/**
 * Created by wlanjie on 14-7-25.
 * 节目组中新闻的Adapter
 */
public class ProgramNewsAdapter extends SingleTypeAdapter<News> {

    private final static int IMAGE_LAYOUT = 0;
    private final static int IMAGE = 1;
    private final static int TITLE = 2;
    private final static int SOURCE = 3;
    private final static int CREATE_TIME = 4;
    private final static int TAG = 5;

    private final Activity activity;
    private final int width;
    /**
     * Create adapter
     *
     * @param activity
     */
    @Inject
    public ProgramNewsAdapter(Activity activity) {
        super(activity, R.layout.program_news_item);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
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
                R.id.program_news_item_image_layout,
                R.id.program_news_item_image,
                R.id.program_news_item_title,
                R.id.program_news_items_source,
                R.id.program_news_item_create_time,
                R.id.program_news_item_tag
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, News item) {
        if (item != null) {
            int height = width / 2 - 36;
            view(IMAGE_LAYOUT).setLayoutParams(new LinearLayout.LayoutParams(height, height));
            ImageLoader.getInstance().displayImage(item.getImg(), imageView(IMAGE), ImageUtils.imageLoader(activity, 0));
            setText(TITLE, item.getName());
            if ("VIDEO".equals(item.getType())) {
                setGone(TAG, false);
            } else if ("NEWS".equals(item.getType())) {
                setGone(TAG, true);
            }
            setText(SOURCE, item.getSource());
            setText(CREATE_TIME, item.getCreateTime());
        }
    }
}
