package com.shengzhish.xyj.activity;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityItem;
import com.shengzhish.xyj.core.Utils;

/**
 * Created by wlanjie on 14-5-30.
 */
public class ActivityAdapter extends SingleTypeAdapter<ActivityItem> {

    private final FragmentActivity activity;
    private final ImageLoader imageLoader;
    private final int width;
    private DisplayImageOptions mImageOptions;

    public ActivityAdapter(FragmentActivity activity) {
        super(activity, R.layout.activity_item);
        this.activity = activity;
        this.imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        this.width = width - Utils.dip2px(activity, 8) - 8;

        mImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.load_image)
                .showImageForEmptyUri(R.drawable.load_image)
                .showImageOnFail(R.drawable.load_image)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300))
                .build();
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
        return new int[]{
                R.id.activity_image_layout,
                R.id.activity_image,
                R.id.activity_prospect,
                R.id.activity_title,
                R.id.activity_sub_title,
                R.id.activity_create_time,
                R.id.activity_joins_count,
                R.id.activity_is_over_view,
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, ActivityItem item) {
        if (item != null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width * 0.6));
            view(0).setLayoutParams(params);
            view(7).setLayoutParams(params);
            imageLoader.displayImage(item.getCoverImg(), imageView(1), mImageOptions);
            if (position % 2 == 0) {
                view(2).setBackgroundResource(R.drawable.feb_activelisty_sd);
            } else {
                view(2).setBackgroundResource(R.drawable.feb_activelistr_sd);
            }
            FrameLayout.LayoutParams prospectParams = new FrameLayout.LayoutParams((int) (width * 0.6), FrameLayout.LayoutParams.MATCH_PARENT);
            view(2).setLayoutParams(prospectParams);
            setText(3, item.getTitle());
            setText(4, item.getSubTitle());
            setText(5, item.getTime());
            setText(6, String.format(activity.getResources().getString(R.string.activity_joins_count), item.getJoinsCnt()));

            if (item.getIsOver() == 0) {
                setGone(7, true);
            } else {
                //活动已结束
                setGone(7, false);
            }
        }
    }

}
