package com.mzs.guaji.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.CelebrityPost;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collection;

/**
 * Created by wlanjie on 14-3-12.
 */
public class StarDetailAdapter extends SingleTypeAdapter<CelebrityPost> {

    private Activity activity;

    public StarDetailAdapter(Activity activity) {
        super(activity, R.layout.star_item_layout);
        this.activity = activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.iv_star_item_avatar,
                R.id.tv_star_item_nickname,
                R.id.tv_star_item_create_time,
                R.id.tv_star_item_message,
                R.id.rl_star_voice,
                R.id.tv_star_voice_time
        };
    }

    @Override
    protected void update(int position, final CelebrityPost item) {
        if (item != null) {
            if (!TextUtils.isEmpty(item.getCelebrityAvatar())) {
                ImageLoader.getInstance().displayImage(item.getCelebrityAvatar(), imageView(0), ImageUtils.imageLoader(activity, 4));
            }
            textView(1).setText(item.getCelebrityName());
            textView(2).setText(item.getCreateTime());
            textView(3).setText(item.getTopic().getTitle());
            int voiceTime;
            if (item.getVideoTime() < 10) {
                voiceTime = 48;
            }
            int time = 200 * item.getVideoTime() / 45;
            if (time > 200) {
                voiceTime = 200;
            } else {
                voiceTime = time;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dip2px(voiceTime), dip2px(48));
            params.leftMargin = dip2px(6);
            view(4).setLayoutParams(params);
            textView(5).setText(item.getVideoTime()+"\"");
            view(4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, TopicDetailsActivity.class);
                    Topic topic = item.getTopic();
                    if (topic != null) {
                        intent.putExtra("topicId", topic.getId());
                    }
                    intent.putExtra("player", item.getVideo());
                    activity.startActivity(intent);
                }
            });
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
