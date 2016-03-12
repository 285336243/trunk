package com.shengzhish.xyj.persionalcore;

import android.app.Activity;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.persionalcore.entity.Message;

/**
 * Created by wlanjie on 14-6-18.
 */
public class MessageNotificationAdapter extends SingleTypeAdapter<Message> {

    private final Activity activity;

    /**
     * Create adapter
     *
     * @param activity
     */
    public MessageNotificationAdapter(Activity activity) {
        super(activity, R.layout.message_notification_item);
        this.activity = activity;
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.message_notification_root,
                R.id.message_notification_content,
                R.id.message_notification_time
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, Message item) {
        if (item != null) {
            if (position % 2 == 0) {
                view(0).setBackgroundColor(activity.getResources().getColor(R.color.message_background_color));
            } else {
                view(0).setBackgroundColor(activity.getResources().getColor(R.color.transparent));
            }
            setText(1, item.getMessage());
            setText(2, item.getCreateTime());
        }
    }
}
