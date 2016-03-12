package com.socialtv.topic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by wlanjie on 14-6-3.
 * 查看大图的Adapter
 */
public class ImageDetailAdapter extends PagerAdapter {

    private final Activity activity;

    private List<Pics> pics = new ArrayList<Pics>();

    private OnPhotoTapListener listener;

    public ImageDetailAdapter(final Activity activity) {
        super();
        this.activity = activity;
    }

    public void clear() {
        this.pics.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<Pics> pics) {
        if (pics != null && !pics.isEmpty()) {
            this.pics = pics;
        }
        notifyDataSetChanged();
    }

    public interface OnPhotoTapListener {
        public void onPhotoTap(View view, float x, float y);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.listener = listener;
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
        final Pics pic = pics.get(position);
        View v = activity.getLayoutInflater().inflate(R.layout.image_detail_item, null);
        final ImageView photoView = (ImageView) v.findViewById(R.id.image_details_photo_view);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.image_details_progress);
        ImageLoader.getInstance().displayImage(pic.getImg(), photoView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mAttacher.update();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                progressBar.setVisibility(View.VISIBLE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                progressBar.setMax(total);
                progressBar.setProgress(current);
            }
        });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (listener != null)
                    listener.onPhotoTap(view, x, y);
            }
        });
        container.addView(v);
        return v;
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
