package com.socialtv.program;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.program.entity.GroupChatItem;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StartOtherFeedUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-8.
 * 节目组中聊天室的Adapter
 */
public class GroupChatAdapter extends MultiTypeAdapter {
    private final static int RIGHT = 0;
    private final static int LEFT = 1;

    private final static int ROOT = 0;
    private final static int AVATAR = 1;
    private final static int MESSAGE = 2;
    private final static int CREATE_TIME = 3;
    private final static int NICKNAME = 4;
    private final static int BADGE_LAYOUT = 5;

    private final Activity activity;
    /**
     * Create adapter
     *
     * @param activity
     */
    public GroupChatAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void addItems(final List<GroupChatItem> items) {
        for (GroupChatItem item : items) {
            if (item != null && item.getCreateUser() != null) {
                if (item.getCreateUser().getUserId().equals(LoginUtil.getUserId(activity))) {
                    addItem(RIGHT, item);
                } else {
                    addItem(LEFT, item);
                }
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        if (type == RIGHT) {
            return R.layout.group_chat_right;
        } else if (type == LEFT) {
            return R.layout.group_chat_left;
        }
        return 0;
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
        if (type == RIGHT) {
            return new int[]{
                    R.id.group_chat_right_root,
                    R.id.group_chat_right_avatar,
                    R.id.group_chat_right_message,
                    R.id.group_chat_right_create_time,
                    R.id.group_chat_right_nickname,
                    R.id.group_chat_right_badge_layout
            };
        } else if (type == LEFT) {
            return new int[]{
                    R.id.group_chat_left_root,
                    R.id.group_chat_left_avatar,
                    R.id.group_chat_left_message,
                    R.id.group_chat_left_create_time,
                    R.id.group_chat_left_nickname,
                    R.id.group_chat_left_badge_layout
            };
        }
        return new int[0];
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
        try {
            if (item != null) {
                GroupChatItem chatItem = (GroupChatItem) item;
                final User user = chatItem.getCreateUser();
                if (user != null) {
                    ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                    setText(NICKNAME, user.getNickname());
                    view(AVATAR).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StartOtherFeedUtil.startOtherFeed(activity, user.getUserId());
                        }
                    });

                    if (user.getBadges() != null && !user.getBadges().isEmpty()) {
                        setGone(BADGE_LAYOUT, false);
                        ((LinearLayout) view(BADGE_LAYOUT)).removeAllViews();
                        for (UserBadge badge : user.getBadges()) {
                            if (badge != null) {
                                ImageView imageView = new ImageView(activity);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, 12), Utils.dip2px(activity, 12));
                                params.gravity = Gravity.CENTER_VERTICAL;
                                imageView.setLayoutParams(params);
                                ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
                                ((LinearLayout) view(BADGE_LAYOUT)).addView(imageView);
                            }
                        }
                    } else {
                        setGone(BADGE_LAYOUT, true);
                    }
                }
                setText(MESSAGE, chatItem.getMsg());
                setText(CREATE_TIME, chatItem.getCreateTime());
                if (type == LEFT) {
                    view(ROOT).setBackgroundColor(activity.getResources().getColor(R.color.white));
                } else if (type == RIGHT) {
                    view(ROOT).setBackgroundColor(activity.getResources().getColor(R.color.baby_white));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
