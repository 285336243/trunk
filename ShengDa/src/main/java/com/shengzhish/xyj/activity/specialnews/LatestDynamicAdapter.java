package com.shengzhish.xyj.activity.specialnews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.FavoriteCntBean;
import com.shengzhish.xyj.activity.entity.Status;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.util.LoginUtilSh;

/**
 * 最新动态adapter
 */
public class LatestDynamicAdapter extends MultiTypeAdapter {
    private static final int INFORMATION_LIST = 0;
    private final ImageLoader imageLoader;
    private Activity activity;
    private final int width;

    public LatestDynamicAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.special_latestdynamic_layout;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        return new int[]{
                R.id.img_imageview, //前面的大图片  0
                R.id.share_textview, //分享        1
                R.id.comment_textview, //评论      2
                R.id.favorite_textview, //喜欢数      3
                R.id.user_avatar, //头像 imageview       4
                R.id.user_nickname, //名字 textview      5
                R.id.user_message, //信息textview        6
                R.id.publish_date,  //日期textview        7
                R.id.progress_bar  //加载进度条progressbar     8
        };
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected View initialize(int type, View view) {
        return super.initialize(type, view);

    }

    public void addItemObject(Object object) {
        if (object != null) {
            if (object instanceof Status)
                addItem((Status) object);
           /* if (object instanceof String)
                addItem((String) object);*/

        }
    }

    private void addItem(Status item) {
        addItem(INFORMATION_LIST, item);
    }


    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        if (item instanceof Status) {
            switch (type) {
                case INFORMATION_LIST:
                    setStatusContent((Status) item);
            }
        }
    }

    private void setStatusContent(final Status item) {
        if (item != null) {
            int intenalmWidrh = width - Utils.dip2px(activity, 6) * 2;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(intenalmWidrh, (int) (intenalmWidrh * 20 / 19));
            imageView(0).setLayoutParams(params);
            imageLoader.displayImage(item.getImg(), imageView(0), ImageUtils.imageLoader(activity, 10),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            view(8).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            super.onLoadingStarted(imageUri, view);
                            view(8).setVisibility(View.VISIBLE);

                        }

                    });
            String avatarUrl = item.getUser().getAvatar();
            if (avatarUrl.indexOf("jpg") != -1) {
                imageLoader.displayImage(item.getUser().getAvatar(), imageView(4), ImageUtils.imageLoader(activity, 10));
            }
            setText(6, item.getMessage());//信息textview        6
            setText(7, item.getCreateTime());//日期textview        7
            setText(5, item.getUser().getNickname()); //名字 textview      5

            setText(3, item.getFavoriteCnt()); //喜欢数      3

            textView(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, DynamicCommendListActivity.class)
                            .putExtra(IConstant.STATUS_ID, item.getId()));
                }
            });
            textView(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, ShareActivity.class)
                            .putExtra(IConstant.SHARE_IMG_URL, item.getImg()).putExtra(IConstant.SHARE_NIKENAME, item.getUser().getNickname()));


                }
            });

            if (item.getIsFavor() == 1) {
                textView(3).setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.favorite_red_icon), null, null, null);
            } else {
                textView(3).setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.icon_favorite), null, null, null);
            }
            textView(3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginUtilSh.isLogin(activity)) {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    } else {
                        haHa(item);
                    }
                }
            });
        }
    }

    private void haHa(final Status item) {
        if (item.getIsFavor() == 0) {
            String favoritUrl = String.format("activity/status_favor.json?id=%s", item.getId());
            HttpboLis.getInstance().getHttp(activity, FavoriteCntBean.class, favoritUrl, new HttpboLis.OnCompleteListener<FavoriteCntBean>() {
                @Override
                public void onComplete(FavoriteCntBean response) {
                    item.setFavoriteCnt(response.getFavoriteCnt());
                    LatestDynamicAdapter.this.notifyDataSetChanged();
                }
            });
            item.setIsFavor(1);
        } else {
            String unfavoritUrl = String.format("activity/status_unfavor.json?id=%s", item.getId());
            HttpboLis.getInstance().getHttp(activity, FavoriteCntBean.class, unfavoritUrl, new HttpboLis.OnCompleteListener<FavoriteCntBean>() {
                @Override
                public void onComplete(FavoriteCntBean response) {
                    item.setFavoriteCnt(response.getFavoriteCnt());
                    LatestDynamicAdapter.this.notifyDataSetChanged();
                }
            });
            item.setIsFavor(0);
        }
    }
}
