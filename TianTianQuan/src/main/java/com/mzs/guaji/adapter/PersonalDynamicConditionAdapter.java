package com.mzs.guaji.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.GsonUtils;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.gson.Gson;
import com.mzs.guaji.R;
import com.mzs.guaji.core.Utils;
import com.mzs.guaji.topic.entity.Feed;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.entity.TopicAction;
import com.mzs.guaji.topic.entity.TopicPost;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PersonalDynamicConditionAdapter extends MultiTypeAdapter {

    private final static int USER_PIC = 0;

    private static final int TOPIC_POST = 1;

    private static final int TOPIC_ACTION = 2;

    private static final int ACTIVITY_TOPIC_POST = 3;

    private static final int CELEBRITY_TOPIC_POST = 4;

    private static final int ACTIVITY_TOPIC_ACTION = 5;

    private static final int CELEBRITY_TOPIC_ACTION = 6;

    private final Gson gson;

    private final ImageLoader imageLoader;

    private final Activity activity;

    private final int width;
    /**
     * Create adapter
     *
     * @param activity
     */
    public PersonalDynamicConditionAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        gson = GsonUtils.createGson();
        imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    public void addFeeds(List<Feed> feeds) {
        for (Feed feed : feeds) {
            addItem(feed);
        }
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.topic_user_pic;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    public void addItem(Feed feed) {
        if ("USER_PIC".equals(feed.getTargetType())) {
            addItem(USER_PIC, feed);
        } else if ("TOPIC_POST".equals(feed.getTargetType())) {
            addItem(TOPIC_POST, feed);
        } else if ("TOPIC_ACTION".equals(feed.getTargetType())) {
            addItem(TOPIC_ACTION, feed);
        } else if ("ACTIVITY_TOPIC_POST".equals(feed.getTargetType())) {
            addItem(ACTIVITY_TOPIC_POST, feed);
        } else if ("CELEBRITY_TOPIC_POST".equals(feed.getTargetType())) {
            addItem(CELEBRITY_TOPIC_POST, feed);
        } else if ("ACTIVITY_TOPIC_ACTION".equals(feed.getTargetType())) {
            addItem(ACTIVITY_TOPIC_ACTION, feed);
        } else if ("CELEBRITY_TOPIC_ACTION".equals(feed.getTargetType())) {
            addItem(CELEBRITY_TOPIC_ACTION, feed);
        }
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
                R.id.topic_user_pic_avatar, //头像 0
                R.id.topic_user_pic_nickname, //昵称 1
                R.id.topic_user_pic_action, //action 2
                R.id.topic_user_pic_title, //话题title 3
                R.id.topic_user_pic_desc, //话题内容 4
                R.id.topic_user_pic_image, //话题图片 5
                R.id.topic_user_pic_image_layout, //话题图片layout 6
                R.id.topic_user_pic_see_more, // 查看全部 7
                R.id.topic_user_pic_createtime, //创建时间 8
                R.id.topic_user_pic_groupname, // 来自哪里 9
                R.id.topic_user_pic_post_layout, // 评论layout 10
                R.id.topic_user_pic_post_title, //评论title 11
                R.id.topic_user_pic_post_desc, // 评论内容 12
                R.id.topic_user_voice_layout,  //语音layout 13
                R.id.topic_user_voice_time, // 语音时间 14
                R.id.topic_user_voice_icon, // 语音icon 15
                R.id.topic_user_pic_see_more //查看全部 16

        };
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        Feed feed = (Feed) item;
        switch (type) {
            case USER_PIC:
            case CELEBRITY_TOPIC_ACTION:
            case ACTIVITY_TOPIC_ACTION:
            case TOPIC_ACTION:
                setUserTopicContent(feed);
                break;
            case TOPIC_POST:
            case ACTIVITY_TOPIC_POST:
            case CELEBRITY_TOPIC_POST:
                setCelebrityPostContent(feed);
                break;
        }
    }
    private void setCelebrityPostContent(Feed feed) {
        TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
        if (topicPost != null) {
            setGone(10, false);
            imageLoader.displayImage(topicPost.getUserAvatar(), imageView(0), ImageUtils.imageLoader(activity, 4));
            setText(1, topicPost.getUserNickname());
            setText(2, feed.getAction());
            setGone(3, true);
            setText(4, topicPost.getMessage());
            textView(4).setTextColor(activity.getResources().getColor(R.color.tvcircle_grid_item_text_color));
            setGone(6, true);
            setText(8, topicPost.getCreateTime());
            if (!TextUtils.isEmpty(topicPost.getMessage())) {
                setGone(4, false);
            } else {
                setGone(4, true);
            }
            if (!TextUtils.isEmpty(topicPost.getAudio())) {
                setGone(13, false);
                int voiceTime;
                if (Integer.valueOf(topicPost.getAudioTime()) < 10) {
                    voiceTime = 48;
                }
                int time = 200 * Integer.valueOf(topicPost.getAudioTime()) / 45;
                if (time > 200) {
                    voiceTime = 200;
                } else {
                    voiceTime = time;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, voiceTime), Utils.dip2px(activity, 38));
                params.leftMargin = Utils.dip2px(activity, 6);
                view(13).setLayoutParams(params);

                RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                iconParams.rightMargin = Utils.dip2px(activity, 8);
                view(15).setLayoutParams(iconParams);

                setText(14, topicPost.getAudioTime() + "\"");
            } else {
                setGone(13, true);
            }
            Topic topic = topicPost.getTopic();
            if (topic != null) {
                if ("ACTIVITY_TOPIC_POST".equals(feed.getTargetType())) {
                    setText(9, topic.getActivityName());
                } else if ("CELEBRITY_TOPIC_POST".equals(feed.getTargetType())) {
                    setText(9, topic.getCelebrityUserNickname());
                } else if ("TOPIC_POST".equals(feed.getTargetType())) {
                    setText(9, topic.getGroupName());
                }
                setText(11, topic.getTitle());
                setText(12, topic.getDesc());
            }
        }
    }

    private void setImageVisibility(final String img) {
        if (!TextUtils.isEmpty(img)) {
            setGone(6, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 3 * 2, width / 3 * 2 / 4 * 3);
            params.leftMargin = Utils.dip2px(activity, 8);
            params.topMargin = Utils.dip2px(activity, 6);

            if (view(16).getVisibility() == View.GONE)
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

    private void setUserTopicContent(Feed feed) {
        TopicAction topicAction = gson.fromJson(feed.getTarget(), TopicAction.class);
        if (topicAction != null) {
            setGone(10, true);
            imageLoader.displayImage(topicAction.getUserAvatar(), imageView(0), ImageUtils.imageLoader(activity, 4));
            setText(1, topicAction.getUserNickname());
            setText(2, feed.getAction());
            final Topic topic = topicAction.getTopic();
            if (topic != null) {
                if (TextUtils.isEmpty(topic.getTitle())) {
                    setGone(3, true);
                } else {
                    setGone(3, false);
                    setText(3, topic.getTitle());
                }
                setImageVisibility(topic.getImg());
                setText(4, topic.getDesc());
                setText(8, topic.getCreateTime());
                if ("ACTIVITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                    setText(9, topic.getActivityName());
                } else if ("TOPIC_ACTION".equals(feed.getTargetType())) {
                    setText(9, topic.getGroupName());
                } else if ("CELEBRITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                    setText(9, topic.getCelebrityUserNickname());
                }
            }
            if ("USER_PIC".equals(feed.getTargetType())) {
                setGone(16, true);
                setGone(3, true);
                setGone(4, true);
                setGone(9, true);
                setText(8, topicAction.getCreateTime());
                setImageVisibility(topicAction.getImg());
            }
        }
    }

}
