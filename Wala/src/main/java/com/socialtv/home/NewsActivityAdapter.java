package com.socialtv.home;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.program.entity.News;
import com.socialtv.util.ImageUtils;

import java.util.List;

/**
 * Created by wlanjie on 14-7-25.
 * 新闻List的Adapter
 */
public class NewsActivityAdapter extends MultiTypeAdapter {

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
    public NewsActivityAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }

    public void addItem(List<News> items) {
        for (News item : items) {
            addItem(0, item);
        }
    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.program_news_item;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        return new int[] {
                R.id.program_news_item_image_layout,
                R.id.program_news_item_image,
                R.id.program_news_item_title,
                R.id.program_news_items_source,
                R.id.program_news_item_create_time,
                R.id.program_news_item_tag
        };
    }

    @Override
    protected void update(int position, Object item, int type) {
        News news = (News) item;
        if (item != null) {
            int height = width / 2 - 36;
            view(IMAGE_LAYOUT).setLayoutParams(new LinearLayout.LayoutParams(height, height));
            ImageLoader.getInstance().displayImage(news.getImg(), imageView(IMAGE), ImageUtils.imageLoader(activity, 0));
            setText(TITLE, news.getName());
            if ("VIDEO".equals(news.getType())) {
                setGone(TAG, false);
            } else if ("NEWS".equals(news.getType())) {
                setGone(TAG, true);
            }
            setText(SOURCE, news.getSource());
            setText(CREATE_TIME, news.getCreateTime());
        }
    }
}
