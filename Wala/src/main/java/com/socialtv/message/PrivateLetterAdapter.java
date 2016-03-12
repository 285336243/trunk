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
import com.socialtv.core.Utils;
import com.socialtv.message.entity.PrivateLetterItem;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.StartOtherFeedUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 * 私信的Adapter
 */
public class PrivateLetterAdapter extends MultiTypeAdapter {

    private final static String EMPTY_TAG = "empty";
    private final Activity activity;
    private final static int AVATAR = 0;
    private final static int NOTICE_ICON = 1;
    private final static int NICKNAME = 2;
    private final static int MESSAGE = 3;
    private final static int CREATE_TIME = 4;
    private final static int ITEM_ROOT = 5;
    private final static int BADGE_LAYOUT = 6;
    private final static int EMPTY_LAYOUT = 7;
    private final static int EMPTY_TEXT = 8;

    private String tag;
    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    @Inject
    public PrivateLetterAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void addItems(List<PrivateLetterItem> items, String tag) {
        addItems(0, items);
        this.tag = tag;
    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.private_letter_item;
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
                R.id.private_letter_item_avatar,
                R.id.private_letter_item_notice_icon,
                R.id.private_letter_item_nickname,
                R.id.private_letter_item_message,
                R.id.private_letter_item_create_time,
                R.id.private_letter_item_root,
                R.id.private_letter_item_badge_layout,
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
    protected void update(int position, final Object item, int type) {
        try {
            final PrivateLetterItem letterItem = (PrivateLetterItem) item;
            if (letterItem != null) {
                if (EMPTY_TAG.equals(tag)) {
                    setGone(ITEM_ROOT, true);
                    setGone(EMPTY_LAYOUT, false);
                    setText(EMPTY_TEXT, letterItem.getEmptyText());
                } else {
                    setGone(EMPTY_LAYOUT, true);
                    setGone(ITEM_ROOT, false);
                    User user = letterItem.getContactUser();
                    if (user != null) {
                        ImageLoader.getInstance().displayImage(letterItem.getContactUser().getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                        setText(NICKNAME, letterItem.getContactUser().getNickname());

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
                    if (letterItem.getStatus() == 1) {
                        setGone(NOTICE_ICON, true);
                    } else {
                        setGone(NOTICE_ICON, false);
                    }
                    setText(MESSAGE, letterItem.getPrivatePost().getPost());
                    setText(CREATE_TIME, letterItem.getPrivatePost().getCreateTime());
                    view(AVATAR).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StartOtherFeedUtil.startOtherFeed(activity, letterItem.getContactUser().getUserId());
                        }
                    });
                }
            }
        } catch (Throwable e) {

        }
    }
}
