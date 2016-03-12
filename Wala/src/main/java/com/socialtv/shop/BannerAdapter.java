package com.socialtv.shop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;


import com.socialtv.core.Intents;
import com.socialtv.home.WebViewActivity;
import com.socialtv.home.entity.Banner;
import com.socialtv.home.entity.Refer;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.ArrayList;
import java.util.List;


public class BannerAdapter extends PagerAdapter {

    private final Activity activity;

    private List<Banner> items = new ArrayList<Banner>();

    @Inject
    public BannerAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setItems(List<Banner> items) {
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
        final Banner item = items.get(position);
        View view = activity.getLayoutInflater().inflate(R.layout.banner_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_image);
        ImageLoader.getInstance().displayImage(item.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
        container.addView(view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = item.getType();
                Refer refer = item.getRefer();
                if ("SHOP".equals(type)) {
                    activity.startActivity(new Intent(activity, ShopDetailsActivity.class).putExtra(IConstant.SHOP_ID,
                            refer.getId()));
                }
                if (type.equals("ADV")) {
                    if ("BROWSER".equals(refer.getOpenType())) {
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(refer.getUrl()));
                        activity.startActivity(mIntent);
                    } else if ("WEB_VIEW".equals(item.getRefer().getOpenType())) {
                        if (refer.getRequireLogin() == 1 && !LoginUtil.isLogin(activity)) {
                            activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                        } else {
                            activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, refer.getUrl())
                                    .add(IConstant.TITLE, refer.getTitle())
                                    .add(IConstant.HIDE_STATUS, refer.getHideStatus())
                                    .add(IConstant.HIDE_TITLE, refer.getHideTitle()).toIntent());
                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
