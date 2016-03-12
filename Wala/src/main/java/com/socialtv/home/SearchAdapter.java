package com.socialtv.home;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.Utils;
import com.socialtv.feed.OthersFeedActivity;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.util.List;

/**
 * Created by wlanjie on 14/10/23.
 * 搜索Adapter
 */
public class SearchAdapter extends MultiTypeAdapter {

    private final static String TAG = "recommend";
    private final static int RECOMMEND = 0;
    private final static int SEARCH = 1;

    private final static int TEXT = 0;

    private final static int AVATAR_LAYOUT = 0;
    private final static int AVATAR = 1;
    private final static int NICKNAME = 2;
    private final static int MESSAGE = 3;
    private final static int GENDER = 4;
    private final static int BADGE_LAYOUT = 5;

    private final Activity activity;

    @Inject
    public SearchAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == RECOMMEND) {
            return R.layout.text;
        } else {
            return R.layout.search_item;
        }
    }

    public void addItem(List<User> users, String tag) {
        for (int i = 0; i < users.size(); i++) {
            if (i == 0 && TAG.equals(tag)) {
                addItem(RECOMMEND, users.get(i));
            }
            addItem(SEARCH, users.get(i));
        }
    }

    @Override
    protected int[] getChildViewIds(int type) {
        if (type == RECOMMEND) {
            return new int[] {
                    R.id.text
            };
        } else {
            return new int[]{
                    R.id.search_item_avatar_layout,
                    R.id.search_item_avatar,
                    R.id.search_item_nickname,
                    R.id.search_item_message,
                    R.id.search_item_gender,
                    R.id.search_item_badge_layout
            };
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected void update(int position, Object object, int type) {
        if (type == RECOMMEND) {
            setText(TEXT, "也许你会对这些人感兴趣");
        } else {
            if (object != null) {
                final User item = (User) object;
                ImageLoader.getInstance().displayImage(item.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                setText(NICKNAME, item.getNickname());
                if (item.getBadges() != null && !item.getBadges().isEmpty()) {
                    setGone(BADGE_LAYOUT, false);
                    ((LinearLayout) view(BADGE_LAYOUT)).removeAllViews();
                    for (UserBadge badge : item.getBadges()) {
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
                view(AVATAR_LAYOUT).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intents(activity, OthersFeedActivity.class).add(IConstant.USER_ID, item.getUserId()).toIntent());
                    }
                });
                setText(MESSAGE, item.getSignature());
                if (TextUtils.isEmpty(item.getGender())) {
                    setGone(GENDER, true);
                } else {
                    setGone(GENDER, false);
                    if ("f".equals(item.getGender())) {
                        imageView(GENDER).setImageResource(R.drawable.icon_lady_tomato);
                    } else {
                        imageView(GENDER).setImageResource(R.drawable.icon_man_tomato);
                    }
                }
            }
        }
    }
}
