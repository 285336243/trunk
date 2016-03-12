package com.shengzhish.xyj.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.ActivityDetailsActivity;
import com.shengzhish.xyj.activity.BarrageActivity;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.home.entity.FeedItem;
import com.shengzhish.xyj.news.DissertationActivity;
import com.shengzhish.xyj.news.NewsActivity;
import com.shengzhish.xyj.util.IConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14-5-30.
 */
public class BannerAdapter extends PagerAdapter {

    private List<FeedItem> banners = new ArrayList<FeedItem>();

    private final Activity activity;

    private final DisplayImageOptions mImageOptions;

    public BannerAdapter(final Activity activity) {
        this.activity = activity;

        mImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.load_image)
                .showImageForEmptyUri(R.drawable.load_image)
                .showImageOnFail(R.drawable.load_image)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    public void setItems(List<FeedItem> banners) {
        if (banners != null && !banners.isEmpty()) {
            this.banners = banners;
            notifyDataSetChanged();
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
     public int getCount() {
        return banners.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(android.view.ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final FeedItem item = banners.get(position);
        View view = View.inflate(activity, R.layout.banner_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_image);
        ImageLoader.getInstance().displayImage(item.getImg(), imageView, mImageOptions);
        ((ViewPager)container).addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("news".equals(item.getType())) {
                    activity.startActivity(new Intents(activity, NewsActivity.class).add(IConstant.NEWS_EXTRA, String.format(IConstant.NEWS_URL, item.getReferId())).add(IConstant.NEWS_ID, item.getReferId()).toIntent());
                } else if ("dissertation".equals(item.getType())) {
                    activity.startActivity(new Intents(activity, DissertationActivity.class).add(IConstant.DISSERTATION_ID, item.getReferId()).add(IConstant.DISSERTATION_EXTRA, String.format(IConstant.DISSERTATION_URL, item.getReferId())).toIntent());
                } else if ("activity_bili".equals(item.getType())) {
                    activity.startActivity(new Intents(activity, BarrageActivity.class).add(IConstant.BILI_id, item.getReferId()).toIntent());
                } else if ("activity_show_time".equals(item.getType())) {
                    activity.startActivity(new Intents(activity, ActivityDetailsActivity.class).add(IConstant.ACTIVITY_ID, item.getId()).toIntent());
                }
            }
        });
        return view;
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate(android.view.ViewGroup)}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by
     *                  {@link #instantiateItem(android.view.View, int)}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((View) object);
    }

    //    private List<FeedItem> banners;
//
//    /**
//     * @param activity
//     */
//    public BannerAdapter(SherlockFragmentActivity activity, List<FeedItem> banners) {
//        super(activity.getSupportFragmentManager());
//        this.banners = banners;
//    }
//
//    /**
//     * Return the Fragment associated with a specified position.
//     *
//     * @param position
//     */
//    @Override
//    public Fragment getItem(int position) {
//        return BannerImageFragment.newInstance(banners.get(position).getImg());
//    }
//
//    /**
//     * Return the number of views available.
//     */
//    @Override
//    public int getCount() {
//        return banners.size();
//    }
//
//    public void setItems(List<FeedItem> items) {
//        if (items != null && !items.isEmpty()) {
//            this.banners = items;
//        }
//        notifyDataSetChanged();
//    }
}
