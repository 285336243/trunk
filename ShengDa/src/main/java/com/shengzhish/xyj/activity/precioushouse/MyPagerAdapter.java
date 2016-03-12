package com.shengzhish.xyj.activity.precioushouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.CodeItem;
import com.shengzhish.xyj.util.ImageUtils;

import java.util.List;

/**
 *
 *
 *
 */
public class MyPagerAdapter extends PagerAdapter {

    private final LayoutInflater mInflater;
    private final ImageLoader imageLoader;
    private final Context context;
    List<CodeItem> pics;

    public MyPagerAdapter(Context context, List<CodeItem> pics) {
        this.pics = pics;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {

        return Integer.MAX_VALUE;
//        return pics.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
        System.out.println("pos:" + position);
//			View view = views.get(position);

        CodeItem codeItem = pics.get(position % pics.size());
//        CodeItem codeItem = pics.get(position);
        View view = mInflater.inflate(R.layout.viewpager_item, null);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        ImageView backgound = (ImageView) view.findViewById(R.id.back_ground);
        imageLoader.displayImage(codeItem.getImg(), backgound, ImageUtils.imageLoader(context, 0), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                        progressBar.setMax(total);
//                        progressBar.setProgress(current);
                    }
                }
        );

        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        itemName.setText(codeItem.getName());
        if (codeItem.getIsGot() == 0) {
            ImageView blackFrontGround = (ImageView) view.findViewById(R.id.black_front_ground);
//            blackFrontGround.setImageResource(R.drawable.black_front_background);
            blackFrontGround.setBackgroundResource(R.drawable.black_front_background);
        }
   /*     if ((position == 1)){
            ImageView redFrontGround = (ImageView) view.findViewById(R.id.red_frame_front_ground);
            redFrontGround.setImageResource(R.drawable.forground);
        }*/
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
