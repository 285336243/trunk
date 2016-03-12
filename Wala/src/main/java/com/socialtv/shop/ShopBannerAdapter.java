package com.socialtv.shop;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;

import com.socialtv.publicentity.Pics;
import com.socialtv.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 单个商品图片展示
 *
 *
 */
public class ShopBannerAdapter extends PagerAdapter {

    private final Activity activity;

    private List<Pics> items = new ArrayList<Pics>();

    @Inject
    public ShopBannerAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setItems(List<Pics> items) {
        if (items != null && !items.isEmpty()) {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(android.view.ViewGroup, int)}. This method is
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Pics item = items.get(position);
        View view = activity.getLayoutInflater().inflate(R.layout.shop_details_banner, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.shop_details_image);
        ImageLoader.getInstance().displayImage(item.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
