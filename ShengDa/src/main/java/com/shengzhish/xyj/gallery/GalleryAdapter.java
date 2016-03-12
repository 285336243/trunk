package com.shengzhish.xyj.gallery;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.gallery.entity.Pic;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.util.ScaleImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14-6-3.
 */
public class GalleryAdapter extends PagerAdapter {

    private final Activity activity;

    private List<Pic> pics = new ArrayList<Pic>();

    private final ImageLoader imageLoader = ImageLoader.getInstance();

    public GalleryAdapter(final Activity activity) {
        super();
        this.activity = activity;
    }

    public void setItems(List<Pic> pics) {
        if (pics != null && !pics.isEmpty()) {
            this.pics = pics;
        }
        notifyDataSetChanged();
    }

    public Pic getItem(int i){
        if(this.pics == null || this.pics.size() == 0){
            return null;
        }
        return this.pics.get(i);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return pics.size();
    }

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
        final Pic pic = pics.get(position);
        View view = View.inflate(activity, R.layout.gallery_item, null);
        TextView titleText = (TextView) view.findViewById(R.id.gallery_item_title);
        titleText.setText(pic.getTitle());
        TextView createTimeText = (TextView) view.findViewById(R.id.gallery_item_create_time);
        createTimeText.setText(pic.getCreateTime());
        ImageView imageView = (ImageView) view.findViewById(R.id.gallery_item_image);
        ScaleImageUtil.updateBaseMatrix(imageView);
        imageLoader.displayImage(pic.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
        container.addView(view);
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
        container.removeView((View) object);
    }
}
