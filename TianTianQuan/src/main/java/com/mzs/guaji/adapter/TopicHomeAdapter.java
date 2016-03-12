package com.mzs.guaji.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.Utils;
import com.mzs.guaji.entity.ActivityTopicItem;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wlanjie on 14-4-9.
 */
public class TopicHomeAdapter extends SingleTypeAdapter<ActivityTopicItem> {

    private final Activity activity;
    private final ImageLoader imageLoader;
    private final int width;

    /**
     * Create adapter
     *
     * @param activity
     */
    @Inject
    public TopicHomeAdapter(Activity activity) {
        super(activity, R.layout.find_topic);
        this.activity = activity;
        this.imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.find_avatar, //头像 0
                R.id.find_nickname, //昵称 1
                R.id.find_action, //action 2
                R.id.find_title, //话题title 3
                R.id.find_desc, //话题内容 4
                R.id.find_image, //话题图片 5
                R.id.find_image_layout, //话题图片layout 6
                R.id.find_see_more, // 查看全部 7
                R.id.find_createtime, //创建时间 8
                R.id.topic_user_pic_see_more, //查看全部 9
                R.id.find_post_cnt, //评论数量 10
                R.id.find_like_cnt // 喜欢数量 11

        };
    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, ActivityTopicItem item) {
        if (item != null) {
            setContent(item);
        }
    }

    private void setContent(final ActivityTopicItem topicItem) {
        imageLoader.displayImage(topicItem.getUserAvatar(), imageView(0), ImageUtils.imageLoader(activity, 4));
        setText(1, topicItem.getUserNickname());
        if (TextUtils.isEmpty(topicItem.getTitle())) {
            setGone(3, true);
        } else {
            setGone(3, false);
            setText(3, topicItem.getTitle());
        }
        setImageVisibility(topicItem.getImg());
        setText(4, topicItem.getDesc());
        setText(8, topicItem.getCreateTime());
        setText(10, topicItem.getPostsCnt() + "");
        setText(11, topicItem.getSupportsCnt() + "");
    }

    private void setImageVisibility(final String img) {
        if (!TextUtils.isEmpty(img)) {
            setGone(6, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 3 * 2, width / 3 * 2 / 4 * 3);
            params.leftMargin = Utils.dip2px(activity, 8);
            params.topMargin = Utils.dip2px(activity, 6);

            if (view(6).getVisibility() == View.GONE)
                params.bottomMargin = Utils.dip2px(activity, 12);
            view(6).setLayoutParams(params);
            imageLoader.displayImage(img, imageView(5), ImageUtils.imageLoader(activity, 0));
            view(6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(activity, ImageDetailsActivity.class);
                    mIntent.putExtra("imageUrl", img);
                    activity.startActivity(mIntent);
                }
            });
        } else {
            setGone(6, true);
        }
    }
}
