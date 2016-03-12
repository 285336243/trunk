package com.mzs.guaji.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.entity.ActivityTopicHome;
import com.mzs.guaji.entity.CategoryGroups;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.entity.User;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.ui.TopicHomeActivity;
import com.mzs.guaji.ui.WebViewActivity;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.view.HorizontalListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by wlanjie on 14-1-15.
 */
public class HomeAdapter extends BaseAdapter {

    private Context context;
    private int width;
    private List<CategoryGroups> mCategoryGroups;
    private final Gson mGson;

    public HomeAdapter(Context context, List<CategoryGroups> mCategoryGroups, int width) {
        this.context = context;
        this.mCategoryGroups = mCategoryGroups;
        this.width = width;
        mGson = GsonUtils.createGson();
    }

    public void addCategoryGroups(List<CategoryGroups> mCategoryGroups) {
        this.mCategoryGroups.clear();
        for (CategoryGroups mGroups : mCategoryGroups) {
            this.mCategoryGroups.add(mGroups);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCategoryGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoryGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        CategoryGroups groups = mCategoryGroups.get(position);
        if (groups != null) {
            if ("CELEBRITY".equals(groups.getType())) {
                return 0;
            } else if ("GROUP".equals(groups.getType())) {
                return 1;
            } else if ("ACTIVITY".equals(groups.getType())) {
                return 2;
            } else {
                throw  new IllegalArgumentException();
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        HomeViewHolder mHolder;
        int type = getItemViewType(position);
        if(v == null) {
            mHolder = new HomeViewHolder();
            v = View.inflate(context, R.layout.home_list_item, null);
            mHolder.mHorizontalListView = new HorizontalListView(context, null);
            mHolder.mTitleText = (TextView) v.findViewById(R.id.home_list_item_title);
            mHolder.mHorizontalListView.setDividerWidth(12);
            mHolder.mHorizontalListView.setPadding(12, 0, 12, 0);
            ((LinearLayout) v).addView(mHolder.mHorizontalListView);
            v.setTag(mHolder);
        }else {
            mHolder = (HomeViewHolder) v.getTag();
        }

        CategoryGroups mGroups = mCategoryGroups.get(position);
        if(mGroups != null) {
            if (type == 0) {
                mHolder.mHorizontalListView.setLayoutParams(new LinearLayout.LayoutParams(width, (width - 36) / 3 + dip2px(context, 30)));
                List<User> stars = GsonUtils.createGson(true).fromJson(mGroups.getResult(), new TypeToken<List<User>>() {}.getType());
                if (stars != null) {
                    mHolder.mHorizontalListView.setAdapter(new HomeStarHorizontalAdapter(context, stars, width));
                    setListStarOnItemClickListener(stars, mHolder);
                }
            } else if (type == 1) {
                mHolder.mHorizontalListView.setLayoutParams(new LinearLayout.LayoutParams(width, width / 2 * 3 / 4 + dip2px(context, 30)));
                List<Group> groups = GsonUtils.createGson(true).fromJson(mGroups.getResult(), new TypeToken<List<Group>>() {}.getType());
                if (groups != null) {
                    mHolder.mHorizontalListView.setAdapter(new HomeHorizontalAdapter(context, groups, width));
                    setListOnItemClickListener(groups, mHolder);
                }
            } else if (type == 2) {
                mHolder.mHorizontalListView.setLayoutParams(new LinearLayout.LayoutParams(width, width / 2 * 3 / 4 + dip2px(context, 30)));
                List<Activity> activities = GsonUtils.createGson(true).fromJson(mGroups.getResult(), new TypeToken<List<Activity>>() {}.getType());
                if (activities != null) {
                    mHolder.mHorizontalListView.setAdapter(new HomeActivityHorizontalAdapter(context, activities, width));
                    setActivityOnItemClickListener(activities, mHolder);
                }
            }
        }
        mHolder.mTitleText.setText(mGroups.getCategoryTitle());

        return v;
    }

    private class HomeViewHolder {
        public TextView mTitleText;
        public HorizontalListView mHorizontalListView;
    }

    private void setActivityOnItemClickListener(final List<Activity> activities, HomeViewHolder mHolder) {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = activities.get(position);
                startActivity(activity);
            }
        };
        mHolder.mHorizontalListView.setOnItemClickListener(onItemClickListener);
    }

    private void startActivity(Activity mActivity) {
        if (mActivity != null) {
            if("BROWSER".equals(mActivity.getType())) {
                JsonElement mElement = mActivity.getParam();
                if(mElement != null) {
                    SkipBrowser mBrowser = mGson.fromJson(mElement, SkipBrowser.class);
                    if(mBrowser != null) {
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowser.getLink()));
                        context.startActivity(mIntent);
                    }
                }
            }else if("WEB_VIEW".equals(mActivity.getType())) {
                JsonElement mElement = mActivity.getParam();
                if(mElement != null) {
                    SkipWebView mWebView = mGson.fromJson(mElement, SkipWebView.class);
                    if(mWebView != null) {
                        Intent mIntent = new Intent(context, WebViewActivity.class);
                        mIntent.putExtra("url", mWebView.getLink());
                        context.startActivity(mIntent);
                    }
                }
            } else if ("TOPIC_HOME".equals(mActivity.getType())) {
                JsonElement element = mActivity.getParam();
                if (element != null) {
                    ActivityTopicHome topicHome = mGson.fromJson(element, ActivityTopicHome.class);
                    if (topicHome != null) {
                        Intent intent = new Intent(context, TopicHomeActivity.class);
                        intent.putExtra("topic_home_image", topicHome.getImg());
                        intent.putExtra("activityId", mActivity.getId());
                        context.startActivity(intent);
                    }
                }
            }
        }
    }

    private void setListStarOnItemClickListener(final List<User> stars, HomeViewHolder mHolder) {
        AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(context, "home_star");
                User star = stars.get(position);
                SkipPersonalCenterUtil.startPersonalCore(context, star.getUserId(), star.getRenderTo());
            }
        };
        mHolder.mHorizontalListView.setOnItemClickListener(mOnItemClickListener);
    }

    private void setListOnItemClickListener(final List<Group> mGroups, HomeViewHolder mHolder) {
        AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(context, "home_group");
                Group mGroup = mGroups.get(position);
                if(mGroup != null) {
                    if("OFFICIAL".equals(mGroup.getType())) {
                        Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
                        mIntent.putExtra("img", mGroup.getImg());
                        mIntent.putExtra("id", mGroup.getId());
                        mIntent.putExtra("name", mGroup.getName());
                        context.startActivity(mIntent);
                    }
                }
            }
        };
        mHolder.mHorizontalListView.setOnItemClickListener(mOnItemClickListener);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
