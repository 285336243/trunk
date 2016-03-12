package com.socialtv.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.melot.meshow.room.RoomLauncher;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.activity.ActivityDetailActivity;
import com.socialtv.core.Intents;
import com.socialtv.home.entity.Banner;
import com.socialtv.mzs.LuckyDipActivity;
import com.socialtv.mzs.RobTicketActivity;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.program.ProgramActivity;
import com.socialtv.shop.ShopDetailsActivity;
import com.socialtv.star.StarActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.MD5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14-6-22.
 * 主页中Banner的Adapter
 */
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Banner item = items.get(position);
        View view = activity.getLayoutInflater().inflate(R.layout.banner_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_image);
        if (item != null)
            ImageLoader.getInstance().displayImage(item.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null)
                    start(item);
            }
        });
        return view;
    }

    private void start(Banner item) {
        if ("ACTIVITY".equals(item.getType())) {
            activity.startActivity(new Intents(activity, ActivityDetailActivity.class).add(IConstant.ACTIVITY_ID, item.getRefer().getId()).toIntent());
        } else if ("SHOP".equals(item.getType())) {
            activity.startActivity(new Intents(activity, ShopDetailsActivity.class).add(IConstant.SHOP_ID, item.getRefer().getId()).toIntent());
        } else if ("ADV".equals(item.getType())) {
            if (item.getRefer() != null) {
                if ("WEB_VIEW".equals(item.getRefer().getOpenType())) {
                    if (item.getRefer().getRequireLogin() == 1 && !LoginUtil.isLogin(activity)) {
                        activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                    } else {
                        activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, item.getRefer().getUrl())
                                .add(IConstant.TITLE, item.getRefer().getTitle()).add(IConstant.HIDE_TITLE, item.getRefer().getHideTitle())
                                .add(IConstant.HIDE_STATUS, item.getRefer().getHideStatus()).toIntent());
                    }
                } else {
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRefer().getUrl()));
                    activity.startActivity(mIntent);
                }
            }
        } else if ("GROUP".equals(item.getType())) {
            activity.startActivity(new Intents(activity, ProgramActivity.class).add(IConstant.PROGRAM_ID, item.getRefer().getId()).toIntent());
        } else if ("CELEBRITY".equals(item.getType())) {
            activity.startActivity(new Intents(activity, StarActivity.class).add(IConstant.STAR_ID, item.getRefer().getId()).toIntent());
        } else if ("GAME".equals(item.getType())) {
            if (item.getRefer() != null) {
                if ("IDOL_DRAWLOT".equals(item.getRefer().getType())) {
                    if (LoginUtil.isLogin(activity)) {
                        activity.startActivity(new Intents(activity, RobTicketActivity.class).add(IConstant.ROB_TICKET_ID, item.getRefer().getId()).toIntent());
                    } else {
                        activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                    }
                } else if ("IDOL_VOTE".equals(item.getRefer().getType())) {
                    if (LoginUtil.isLogin(activity)) {
                        activity.startActivity(new Intents(activity, LuckyDipActivity.class).add(IConstant.LUCKY_DIP_ID, item.getRefer().getId()).toIntent());
                    } else {
                        activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                    }
                }
            }
        } else if ("KK_ROOM".equals(item.getType())) {
            if (LoginUtil.isLogin(activity)) {
                Intent intent = new Intent(activity, RoomLauncher.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", LoginUtil.getUserId(activity));
                bundle.putString("usernickname", LoginUtil.getUserNickName(activity));
                if (item.getRefer() != null) {
                    bundle.putString("roomid", item.getRefer().getId());
                }
                bundle.putString("usersessionid", MD5Util.getMD5Str(LoginUtil.getUserId(activity)));
//                bundle.putString("usergender", LoginUtil.get);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            } else {
                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
            }
        }
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
