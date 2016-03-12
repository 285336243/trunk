package com.socialtv.message;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.Utils;
import com.socialtv.feed.OthersFeedActivity;
import com.socialtv.message.entity.MessageItem;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.topic.ReplyPost;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-2.
 * 消息的Adapter
 */
public class MessageAdapter extends MultiTypeAdapter{

    private final static String EMPTY_TAG = "empty";
    private final static int AVATAR = 0;
    private final static int NOTICE_ICON = 1;
    private final static int NICKNAME = 2;
    private final static int MESSAGE = 3;
    private final static int POST_CONTENT = 4;
    private final static int CREATE_TIME = 5;
    private final static int ITEM_ROOT = 6;
    private final static int BADGE_LAYOUT = 7;
    private final static int EMPTY_LAYOUT = 8;
    private final static int EMPTY_TEXT = 9;

    private final Activity activity;
    private String tag;
    /**
     * Create adapter
     *
     * @param activity
     */
    @Inject
    public MessageAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.message_item;
    }

    public void addItems(List<MessageItem> items, String tag) {
        addItems(0, items);
        this.tag = tag;
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
    protected int[] getChildViewIds(int type) {
        return new int[] {
                R.id.message_item_avatar,
                R.id.message_item_notice_icon,
                R.id.message_item_nickname,
                R.id.message_item_message,
                R.id.message_item_post_content,
                R.id.message_item_create_time,
                R.id.message_item_root,
                R.id.message_item_badge_layout,
                R.id.list_empty_layout,
                R.id.list_empty_text
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, Object item, int type) {
        try {
            if (item != null) {
                MessageItem messageItem = (MessageItem) item;
                if (messageItem != null) {
                    if (EMPTY_TAG.equals(tag)) {
                        setGone(EMPTY_LAYOUT, false);
                        setGone(ITEM_ROOT, true);
                    } else {
                        setGone(EMPTY_LAYOUT, true);
                        setGone(ITEM_ROOT, false);
                        if (messageItem.getStatus() == 1) {
                            setGone(NOTICE_ICON, true);
                        } else {
                            setGone(NOTICE_ICON, false);
                        }
                        if (messageItem.getRefer() != null) {
                            if (messageItem.getRefer().getPost() != null) {
                                final User user = messageItem.getRefer().getPost().getCreateUser();
                                if (user != null) {
                                    view(AVATAR).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            activity.startActivity(new Intents(activity, OthersFeedActivity.class).add(IConstant.USER_ID, user.getUserId()).toIntent());
                                        }
                                    });
                                    ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                                    setText(NICKNAME, user.getNickname());

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

                                setText(MESSAGE, messageItem.getRefer().getPost().getMsg());
                                setText(CREATE_TIME, messageItem.getRefer().getPost().getCreateTime());

                                ReplyPost replyPost = messageItem.getRefer().getPost().getReplyPost();
                                if (replyPost != null && replyPost.getCreateUser() != null) {
                                    User replyUser = replyPost.getCreateUser();
                                    if (replyUser != null && messageItem.getRefer().getTopic() != null) {
                                        if (LoginUtil.getUserId(activity).equals(replyUser.getUserId())) {
                                            if (messageItem.getRefer().getPost().getReplyPost() != null) {
                                                setText(POST_CONTENT, "回复了你的评论 " + "\"" + messageItem.getRefer().getPost().getReplyPost().getMsg() + "\"");
                                            }

                                        } else {
                                            setText(NICKNAME, user.getNickname() + " 回复 " + replyUser.getNickname());
                                            setText(POST_CONTENT, "评论你的话题 " + "\"" + messageItem.getRefer().getTopic().getMessage() + "\"");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
