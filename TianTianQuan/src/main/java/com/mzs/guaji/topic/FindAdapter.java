package com.mzs.guaji.topic;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.GsonUtils;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.gson.Gson;
import com.mzs.guaji.R;
import com.mzs.guaji.core.Utils;
import com.mzs.guaji.topic.entity.FindTopicItem;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wlanjie on 14-5-20.
 */
public class FindAdapter extends MultiTypeAdapter {

    private final static int TOPIC = 0;
    private final static int ACTIVITY_TOPIC = 1;
    private final static int CELEBRITY_TOPIC = 2;

    private final Activity activity;
    private final Gson gson;
    private final ImageLoader imageLoader;
    private final int width;

    /**
     * Create adapter
     *
     * @param activity
     */
    public FindAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.gson = GsonUtils.createGson();
        this.imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    public void addItem(final FindTopicItem topicItem) {
        if ("TOPIC".equals(topicItem.getType())) {
            addItem(TOPIC, topicItem);
        } else if ("ACTIVITY_TOPIC".equals(topicItem.getType())) {
            addItem(ACTIVITY_TOPIC, topicItem);
        } else if ("CELEBRITY_TOPIC".equals(topicItem.getType())) {
            addItem(CELEBRITY_TOPIC, topicItem);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.find_topic;
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
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        FindTopicItem topicItem = (FindTopicItem) item;
        if (topicItem != null) {
            setContent(topicItem);
        }
    }

    private void setContent(final FindTopicItem topicItem) {
        Topic topic = gson.fromJson(topicItem.getTopic(), Topic.class);
        if (topic != null) {
            imageLoader.displayImage(topic.getUserAvatar(), imageView(0), ImageUtils.imageLoader(activity, 4));
            setText(1, topic.getUserNickname());
            if ("TOPIC".equals(topicItem.getType())) {
                setText(2, "来自 : " + topic.getGroupName());
            } else if ("ACTIVITY_TOPIC".equals(topicItem.getType())) {
                setText(2, "来自 : " + topic.getActivityName());
            } else if ("CELEBRITY_TOPIC".equals(topicItem.getType())) {
               setText(2, "来自 : " + topic.getCelebrityUserNickname());
            }
            if (TextUtils.isEmpty(topic.getTitle())) {
                setGone(3, true);
            } else {
                setGone(3, false);
                setText(3, topic.getTitle());
            }
            setImageVisibility(topic.getImg());
            setText(4, topic.getDesc());
            setText(8, topic.getCreateTime());
            setText(10, topic.getPostsCnt() + "");
            setText(11, topic.getSupportsCnt() + "");
        }
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
