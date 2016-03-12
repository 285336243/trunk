package com.socialtv.personcenter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.Intents;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.feed.OthersFeedActivity;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.FollowResponse;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-7.
 * 关注的Adapter
 */
public class FollowAdapter extends MultiTypeAdapter {

    private final static String TAG = "recommend";
    private final static int RECOMMEND = 0;
    private final static int FOLLOW = 1;

    private final static int AVATAR = 0;
    private final static int NICKNAME = 1;
    private final static int MESSAGE = 2;
    private final static int GENDER = 3;
    private final static int IS_FOLLOW = 4;
    private final static int BADGE_LAYOUT = 5;

    private final PersonalUrlService service;

    private final Activity activity;

    private String tag;
    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    public FollowAdapter(final FollowActivity activity, PersonalUrlService service) {
        super(activity);
        this.activity = activity;
        this.service = service;
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == RECOMMEND) {
            return R.layout.text;
        } else {
            return R.layout.follow_item;
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
                    R.id.follow_item_avatar,
                    R.id.follow_item_nickname,
                    R.id.follow_item_message,
                    R.id.follow_item_gender,
                    R.id.follow_item_is_follow,
                    R.id.follow_item_badge_layout
            };
        }
    }


    public void setItems(final List<User> items, final String tag) {
        for (int i = 0; i < items.size(); i++) {
            if (i == 0 && TAG.equals(tag)) {
                addItem(RECOMMEND, items.get(i));
            }
            addItem(FOLLOW, items.get(i));
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected void update(int position, Object item, int type) {
        if (type == RECOMMEND) {
            setText(0, "也许你会对这些人感兴趣");
        } else if (type == FOLLOW) {
            if (item != null) {
                final User user = (User) item;
                ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                setText(NICKNAME, user.getNickname());
                setText(MESSAGE, user.getSignature());

                if (user.getBadges() != null && !user.getBadges().isEmpty()) {
                    setGone(BADGE_LAYOUT, false);
                    ((LinearLayout) view(BADGE_LAYOUT)).removeAllViews();
                    for (UserBadge badge : user.getBadges()) {
                        if (badge != null) {
                            ImageView imageView = new ImageView(activity);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, 16), Utils.dip2px(activity, 16));
                            params.gravity = Gravity.CENTER_VERTICAL;
                            imageView.setLayoutParams(params);
                            ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
                            ((LinearLayout) view(BADGE_LAYOUT)).addView(imageView);
                        }
                    }
                } else {
                    setGone(BADGE_LAYOUT, true);
                }

                if (TextUtils.isEmpty(user.getGender())) {
                    setGone(GENDER, true);
                } else {
                    setGone(GENDER, false);
                    if ("f".equals(user.getGender())) {
                        imageView(GENDER).setImageResource(R.drawable.icon_lady_tomato);
                    } else {
                        imageView(GENDER).setImageResource(R.drawable.icon_man_tomato);
                    }
                }
                if (user.getIsFollow() == 1) {
                    ((ImageButton) view(IS_FOLLOW)).setImageResource(R.drawable.icon_following_tomato);
                } else {
                    ((ImageButton) view(IS_FOLLOW)).setImageResource(R.drawable.icon_follow_tomato);
                }

                view(IS_FOLLOW).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followRequest(user, (ImageButton) v);
                    }
                });
                view(AVATAR).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!user.getUserId().equals(LoginUtil.getUserId(activity))) {
                            activity.startActivityForResult(new Intents(activity, OthersFeedActivity.class)
                                    .add(IConstant.USER_ID, user.getUserId()).toIntent(), 1);
                        }
                    }
                });
            }
        }
    }

    private void followRequest(final User user, ImageButton imageButton) {
        if (user.getIsFollow() == 1) {
            new AbstractRoboAsyncTask<FollowResponse>(activity){
                /**
                 * Execute task with an authenticated account
                 *
                 * @param data
                 * @return result
                 * @throws Exception
                 */
                @Override
                protected FollowResponse run(Object data) throws Exception {
                    return (FollowResponse) HttpUtils.doRequest(service.createUnFollowRequest(user.getUserId())).result;
                }

                @Override
                protected void onSuccess(FollowResponse followResponse) throws Exception {
                    super.onSuccess(followResponse);
                    if (followResponse != null) {
                        if (followResponse.getResponseCode() == 0) {
                            activity.setResult(Activity.RESULT_OK);
                        } else {
                            ToastUtils.show(activity, followResponse.getResponseMessage());
                        }
                    }
                }
            }.execute();
            ToastUtils.show(activity, "操作成功");
            imageButton.setImageResource(R.drawable.icon_follow_tomato);
            user.setIsFollow(0);
        } else {
            new AbstractRoboAsyncTask<FollowResponse>(activity){
                /**
                 * Execute task with an authenticated account
                 *
                 * @param data
                 * @return result
                 * @throws Exception
                 */
                @Override
                protected FollowResponse run(Object data) throws Exception {
                    return (FollowResponse) HttpUtils.doRequest(service.createFollowRequest(user.getUserId())).result;
                }

                @Override
                protected void onSuccess(FollowResponse followResponse) throws Exception {
                    super.onSuccess(followResponse);
                    if (followResponse != null) {
                        if (followResponse.getResponseCode() == 0) {
                            activity.setResult(Activity.RESULT_OK);
                        } else {
                            ToastUtils.show(activity, followResponse.getResponseMessage());
                        }
                    }
                }
            }.execute();
            ToastUtils.show(activity, "操作成功");
            imageButton.setImageResource(R.drawable.icon_following_tomato);
            user.setIsFollow(1);
        }
    }
}
