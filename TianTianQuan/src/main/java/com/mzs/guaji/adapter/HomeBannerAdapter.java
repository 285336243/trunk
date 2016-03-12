package com.mzs.guaji.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.entity.ActivityTopicHome;
import com.mzs.guaji.entity.Banners;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.entity.GameList;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.LoveExponentActivity;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.ui.TopicHomeActivity;
import com.mzs.guaji.ui.WebViewActivity;
import com.mzs.guaji.util.LoginUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-22.
 */
public class HomeBannerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private final LayoutInflater mInflater;
    private List<Banners> mBanners;
    private static final Gson mGson  = GsonUtils.createGson();
    private DisplayImageOptions options;
    public HomeBannerAdapter(Context context, List<Banners> mBanners) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mBanners = mBanners;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }

    public void addBanners(List<Banners> mBanners) {
        this.mBanners.clear();
        for (Banners mBanner : mBanners) {
            this.mBanners.add(mBanner);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        HomeBannersViewHolder mHolder;
        if (convertView != null) {
            mHolder = (HomeBannersViewHolder) convertView.getTag();
        }else {
            convertView = mInflater.inflate(R.layout.home_pager_item, container, false);
            mHolder = new HomeBannersViewHolder(convertView);
            convertView.setTag(mHolder);
        }
        Banners mBanner = mBanners.get(position);
        setImage(convertView, mBanner, mHolder);
        return convertView;
    }

    @Override
    public int getCount() {
        return mBanners.size();
    }

    private void setImage(View v, Banners mBanner, final HomeBannersViewHolder mHolder) {
        if(mBanner != null) {
            if(mBanner.getImg() != null && !"".equals(mBanner.getImg())) {
                ImageLoader.getInstance().displayImage(mBanner.getImg(), mHolder.mImageView, options);
            }
            setBannerClickListener(v, mBanner);
        }
    }

    private void setBannerClickListener(View v, final Banners mBanner) {
        View.OnClickListener mBannerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "home_banner");
                if("ACTIVITY".equals(mBanner.getType())) {
                    startActivity(mBanner);
                } else if ("TOPIC".equals(mBanner.getType())) {
                    startTopic(mBanner);
                } else if ("GROUP".equals(mBanner.getType())) {
                    startGroup(mBanner);
                } else if ("ENTRY_FORM".equals(mBanner.getType())) {
                    startEntryForm(mBanner);
                } else if ("GAME".equals(mBanner.getType())) {
                    startGame(mBanner);
                }
            }
        };
        v.setOnClickListener(mBannerClickListener);
    }

    private void startGame(Banners mBanner) {
        if (LoginUtil.isLogin(context)) {
            GameList gameList = mGson.fromJson(mBanner.getTarget(), GameList.class);
            if (gameList != null) {
                if ("AFFECTION_INDEX".equals(gameList.getType())) {
                    MobclickAgent.onEvent(context, "game_lovetest");
                    Intent mIntent = new Intent(context, LoveExponentActivity.class);
                    context.startActivity(mIntent);
                } else if ("WEB_VIEW".equals(gameList.getType())) {
                    startWebViewActivity(gameList);
                }
             }
        } else {
            Intent mIntent = new Intent(context, LoginActivity.class);
            context.startActivity(mIntent);
        }
    }

    private void startWebViewActivity(GameList gameList) {
        if (LoginUtil.isLogin(context)) {
            Map<String, String> maps= GsonUtils.createGson().fromJson(gameList.getParam(), new TypeToken<Map<String, String>>(){}.getType());
            if (maps != null) {
                String link = maps.get("link");
                String noTitle = maps.get("noTitle");
                String title = maps.get("title");
                String backKey = maps.get("backKey");
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", link);
                intent.putExtra("noTitle", noTitle);
                intent.putExtra("title", title);
                intent.putExtra("backKey", backKey);
                context.startActivity(intent);
            }
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void startActivity(Banners mBanner) {
        Activity mActivity = mGson.fromJson(mBanner.getTarget(), Activity.class);
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

    private void startTopic(Banners mBanner) {
        Topic mTopic = mGson.fromJson(mBanner.getTarget(), Topic.class);
        if(mTopic != null) {
            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
            mIntent.putExtra("topicId", mTopic.getId());
            context.startActivity(mIntent);
        }
    }

    private void startGroup(Banners mBanner) {
        Group mGroup = mGson.fromJson(mBanner.getTarget(), Group.class);
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

    private void startEntryForm(Banners mBanner) {
        EntryForm mEntryForm = mGson.fromJson(mBanner.getTarget(), EntryForm.class);
        if(mEntryForm != null && "GSTX_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent applyIntent = new Intent(context, GSTXApplyActivity.class);
            applyIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(applyIntent);
        }else if(mEntryForm != null && "AQBWZ_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, AQBWApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            mIntent.putExtra("clause", mEntryForm.getClause());
            context.startActivity(mIntent);
        }else if(mEntryForm != null && "FNMS_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, FNMSApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(mIntent);
        }
    }

    private class HomeBannersViewHolder {
        public final ImageView mImageView;
        public HomeBannersViewHolder(View v) {
            mImageView = (ImageView) v.findViewById(R.id.home_pager_item_image);
        }
    }
}
