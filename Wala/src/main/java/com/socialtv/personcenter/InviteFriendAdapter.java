package com.socialtv.personcenter;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.FollowResponse;
import com.socialtv.personcenter.entity.Mobile;
import com.socialtv.personcenter.entity.SearchResult;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.StartOtherFeedUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wlanjie on 14-8-14.
 * 邀请好友的Adapter
 */
public class InviteFriendAdapter extends MultiTypeAdapter {

    private final Activity activity;
    private final static int TEXT = 0;
    private final static int ATTENTION = 1;
    private final static int FRIEND = 2;

    private final static int AVATAR_LAYOUT = 0;
    private final static int ATTENTION_AVATAR = 1;
    private final static int ATTENTION_NICKNAME = 2;
    private final static int ATTENTION_PHONE_NUMBER = 3;
    private final static int ATTENTION_IS_FOLLOW = 4;
    private final static int BADGE_LAYOUT = 5;

    private final static int FRIEND_AVATAR = 0;
    private final static int FRIEND_NICKNAME = 1;
    private final static int FRIEND_PHONE_NUMBER = 2;
    private final static int FRIEND_IS_FOLLOW = 3;
    private final static int FRIEND_ROOT = 4;

    private Map<String, List<String>> phoneAndNames;
    private final List<String> phoneNumbers;

    //搜索list
    private List<Mobile> mobiles;
    private List<String> phones;
    private Object mLock = new Object();

    // 这个list是为了标记邀请好友的checkbox是否选中
    private final Map<Integer, Boolean> checks;

    private final PersonalUrlService service;

    private int namePosition = 0;

    private ItemFilter mFilter;

    /**
     * Create adapter
     *
     * @param activity
     */
    @Inject
    public InviteFriendAdapter(Activity activity, PersonalUrlService service) {
        super(activity);
        this.activity = activity;
        this.service = service;
        phoneNumbers = new ArrayList<String>();
        checks = new HashMap<Integer, Boolean>();
    }

    public void setItem(List<Mobile> mobiles, Map<String, List<String>> phoneAndNames) {
        this.phoneAndNames = phoneAndNames;
        this.mobiles = mobiles;
        addItem(TEXT, mobiles);
        for (Mobile mobile : mobiles) {
            addItem(ATTENTION, mobile);
        }
    }

    public void setInviteItem(List<String> items, Map<String, List<String>> phoneAndNames) {
        this.phoneAndNames = phoneAndNames;
        this.phones = items;
        addItem(TEXT, phoneAndNames);
        for (String item : items) {
            addItem(FRIEND, item);
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
        if (type == TEXT) {
            return R.layout.text;
        } else if (type == ATTENTION) {
            return R.layout.invite_attention_item;
        } else if (type == FRIEND) {
            return R.layout.invite_friend_item;
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
        if (type == ATTENTION) {
            return new int[]{
                    R.id.invite_attention_avatar_layout,
                    R.id.invite_attention_avatar,
                    R.id.invite_attention_nickname,
                    R.id.invite_attention_phone_number,
                    R.id.invite_attention_is_follow,
                    R.id.invite_attention_badge_layout
            };
        } else if (type == FRIEND) {
            return new int[]{
                    R.id.invite_friend_item_avatar,
                    R.id.invite_friend_item_nickname,
                    R.id.invite_friend_item_phone_number,
                    R.id.invite_friend_item_is_follow,
                    R.id.invite_friend_item_root
            };
        } else if (type == TEXT) {
            return new int[]{
                    R.id.text
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
    protected void update(final int position, Object item, int type) {
        if (type == TEXT) {
            if (item instanceof List) {
                textView(TEXT).setGravity(Gravity.NO_GRAVITY);
                setText(TEXT, "待关注好友");
            } else if (item instanceof Map) {
                textView(TEXT).setGravity(Gravity.NO_GRAVITY);
                setText(TEXT, "可邀请好友");
            } else {
                //没有搜到好友时的提醒语句,判断的是item的类型,如果不是List或者Map就显示这一句话
                setText(TEXT, "没有找到通讯录好友");
                textView(TEXT).setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } else if (type == ATTENTION) {
            final Mobile mobile = (Mobile) item;
            if (mobile != null) {
                final User user = mobile.getUser();
                if (user != null) {
                    ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(ATTENTION_AVATAR), ImageUtils.avatarImageLoader());
                    setText(ATTENTION_NICKNAME, user.getNickname());
                    view(AVATAR_LAYOUT).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StartOtherFeedUtil.startOtherFeed(activity, user.getUserId());
                        }
                    });
                }
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

                view(ATTENTION_IS_FOLLOW).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followRequest(mobile);
                        ImageButton imageButton = (ImageButton) v;
                        if (mobile.getIsFollow() == 1) {
                            imageButton.setImageResource(R.drawable.icon_follow_tomato);
                            mobile.setIsFollow(0);
                        } else {
                            imageButton.setImageResource(R.drawable.icon_following_tomato);
                            mobile.setIsFollow(1);
                        }
                    }
                });
                setText(ATTENTION_PHONE_NUMBER, phoneAndNames.get(mobile.getMobile()) + " " + mobile.getMobile());
            }
        } else if (type == FRIEND) {
            final String mobile = (String) item;
            if (phoneAndNames != null) {
                List<String> names = phoneAndNames.get(mobile);
                if (names != null) {
                    if (names.size() > 0 && names.size() == 1) {
                        setText(FRIEND_NICKNAME, names.get(0));
                    } else {
                        setText(FRIEND_NICKNAME, names.get(namePosition));
                        if (namePosition == names.size() - 1) {
                            namePosition = 0;
                        } else {
                            ++namePosition;
                        }
                    }
                }
                setText(FRIEND_PHONE_NUMBER, mobile);
            }
            final CheckBox checkBox = view(FRIEND_IS_FOLLOW);
            if (checks.get(position) != null && checks.get(position)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            view(FRIEND_ROOT).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        phoneNumbers.add(mobile);
                        checks.put(position, false);
                        checkBox.setChecked(false);
                    } else {
                        checks.put(position, true);
                        phoneNumbers.remove(mobile);
                        checkBox.setChecked(true);
                    }
                }
            });
        }
    }

    private void followRequest(final Mobile mobile) {
        if (mobile.getIsFollow() == 1) {
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
                    return (FollowResponse) HttpUtils.doRequest(service.createUnFollowRequest(mobile.getUser().getUserId())).result;
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
                    return (FollowResponse) HttpUtils.doRequest(service.createFollowRequest(mobile.getUser().getUserId())).result;
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
        }
    }

    public List<String> getCheckPhoneNumbers() {
        return phoneNumbers;
    }

    public void clearChecks() {
        checks.clear();
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                SearchResult result = new SearchResult();
                result.setMobiles(mobiles);
                result.setPhones(phones);
                results.values = result;
                results.count = mobiles.size() + phones.size();
            } else {
                SearchResult result = new SearchResult();
                List<Mobile> constraintMobiles = new ArrayList<Mobile>();
                final String filterString = constraint.toString().trim().toLowerCase(Locale.getDefault());
                for (int i = 0; i < mobiles.size(); i++) {
                    Mobile mobile = mobiles.get(i);
                    if (mobile != null) {
                        User user = mobile.getUser();
                        if (user.getNickname().toLowerCase(Locale.getDefault()).contains(filterString)) {
                            constraintMobiles.add(mobile);
                        }
                    }
                }
                result.setMobiles(constraintMobiles);
                List<String> containsPhones = new ArrayList<String>();
//                for (int i = 0; i < phones.size(); i++) {
//                    String newValue = phones.get(i);
//                    List<String> names = phoneAndNames.get(newValue);
//                    if (newValue.toLowerCase().contains(filterString)) {
//                        containsPhones.add(newValue);
//                    }
//                }

                for (Map.Entry<String, List<String>> entry : phoneAndNames.entrySet()) {
                    String key = entry.getKey();
                    List<String> names = entry.getValue();
                    for (String s : names) {
                        if (s.contains(filterString)) {
                            containsPhones.add(key);
                        }
                    }
                }
                result.setPhones(containsPhones);

                int count = 0;
                if (result.getMobiles() != null) {
                    count += result.getMobiles().size();
                }
                if (result.getPhones() != null) {
                    count += result.getPhones().size();
                }
                results.count = count;
                results.values = result;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                SearchResult result = (SearchResult) results.values;
                if (result != null && results.count > 0) {
                    clear();
                    if (result.getMobiles() != null && !result.getMobiles().isEmpty()) {
                        addItem(TEXT, mobiles);
                        for (Mobile mobile : result.getMobiles()) {
                            addItem(ATTENTION, mobile);
                        }
                    }
                    if (result.getPhones() != null && !result.getPhones().isEmpty()) {
                        addItem(TEXT, phoneAndNames);
                        for (String item : result.getPhones()) {
                            addItem(FRIEND, item);
                        }
                    }
                } else {
                    //没有搜索到好友,显示没有搜到好友的提醒
                    clear();
                    addItem(TEXT, new Object());
                }
            }
        }
    }
}
