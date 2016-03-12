package com.mzs.guaji.adapter.factory.personal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.factory.ViewHolder;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.LinkTopic;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.entity.feed.TopicAction;
import com.mzs.guaji.entity.feed.TopicActionFeed;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.TopicCommentActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.StringUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class TopicViewHolder implements ViewHolder {

	private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
	.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
	.setPrettyPrinting().setVersion(1.0).create();

    private LinearLayout mRootLayout;
	private ImageView mUserAvatarImage;
	private TextView mUserNameText;
	private TextView mGroupNameText;    
	private TextView mCreateTimeText;   
	private TextView mTitleText;  
	private TextView mDescText;       
	private ImageView mContentImage;     
	private TextView mPostsCountText;   
	private TextView mSupportsCountText;
	private RelativeLayout mShareLayout;     
	private RelativeLayout mPostsLayout;     
	private RelativeLayout mSupportsLayout;   
	private FrameLayout mFrameLayout;
	private ImageView mLikeImage;
	private View view;
    private GuaJiAPI mApi;
    private Context context;
    private Dialog mDialog;
    private DefaultThirdPartyShareActivity mShareActivity;
    public TopicViewHolder(Context context) {
        this.context = context;
    }
	
	@Override
	public void build(View v) {
        mApi = GuaJiAPI.newInstance(context);
		view = v;
        mRootLayout = (LinearLayout) v.findViewById(R.id.personal_topic_list_item_root_layout);
		mUserAvatarImage = (ImageView) v.findViewById(R.id.personal_topic_list_item_user_avatar);
		mUserNameText = (TextView) v.findViewById(R.id.personal_topic_list_item_usre_name);
		mGroupNameText = (TextView) v.findViewById(R.id.personal_topic_list_item_group_name);
		mCreateTimeText = (TextView) v.findViewById(R.id.personal_topic_list_item_create_time);
		mTitleText = (TextView) v.findViewById(R.id.personal_topic_list_item_title);
		mDescText = (TextView) v.findViewById(R.id.personal_topic_list_item_desc);
		mContentImage = (ImageView) v.findViewById(R.id.personal_topic_list_item_content_img);
		mPostsCountText = (TextView) v.findViewById(R.id.personal_topic_list_item_post_count);
		mSupportsCountText = (TextView) v.findViewById(R.id.personal_topic_list_item_supports_count);
		mShareLayout = (RelativeLayout) v.findViewById(R.id.personal_topic_list_item_share);
		mPostsLayout = (RelativeLayout) v.findViewById(R.id.personal_topic_list_item_posts);
		mSupportsLayout = (RelativeLayout) v.findViewById(R.id.personal_topic_list_item_supports);
		mFrameLayout = (FrameLayout) v.findViewById(R.id.personal_topic_list_item_content_img_layout);
		mLikeImage = (ImageView) v.findViewById(R.id.personal_topic_list_item_like_icon);
	}

	@Override
	public View renderView(final Context context, JsonElement obj) {
		TopicActionFeed mTopicActionFeed = mGson.fromJson(obj, TopicActionFeed.class);
		ImageLoader mImageLoader = ImageLoader.getInstance();
        if(mTopicActionFeed.getTarget() != null) {
            final TopicAction mTopicAction = mTopicActionFeed.getTarget();
            if(mTopicAction.getUserAvatar() != null && !"".equals(mTopicAction.getUserAvatar())) {
                mImageLoader.displayImage(mTopicAction.getUserAvatar(), mUserAvatarImage, ImageUtils.imageLoader(context, 4));
            }
            final Topic mTopic = mTopicAction.getTopic();
            mShareLayout.setTag(mTopic);
            mShareLayout.setOnClickListener(mShareClickListener);
            mPostsLayout.setTag(mTopic);
            mPostsLayout.setOnClickListener(mPostsClickListener);
            if(mTopic != null) {
                if(mTopic.getIsLiked() == 0) {
                    mLikeImage.setImageResource(R.drawable.icon_liketopic_tj);
                }else {
                    mLikeImage.setImageResource(R.drawable.icon_liketopic_active_tj);
                }
                mSupportsLayout.setTag(mTopic);
                mSupportsLayout.setOnClickListener(mSuppertsClickListener);
                mUserNameText.setText(mTopicAction.getUserNickname());
                mGroupNameText.setText(mTopicActionFeed.getAction());
                mCreateTimeText.setText(mTopicAction.getCreateTime());
                mTitleText.setText(mTopic.getTitle());
                mDescText.setText(mTopic.getDesc()+"");
                if(mTopic.getImg() != null && !"".equals(mTopic.getImg())) {
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mImageLoader.displayImage(mTopic.getImg(), mContentImage, ImageUtils.imageLoader(context, 0));
                    mContentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mIntent = new Intent(context, ImageDetailsActivity.class);
                            mIntent.putExtra("imageUrl", mTopic.getImg());
                            context.startActivity(mIntent);
                        }
                    });
                }else {
                    mFrameLayout.setVisibility(View.GONE);
                }
                mPostsCountText.setText(String.format(context.getResources().getString(R.string.comment_count), mTopic.getPostsCnt()));
                mSupportsCountText.setText(String.format(context.getResources().getString(R.string.like_count), mTopic.getSupportsCnt()));
                mRootLayout.setTag(mTopic);
                mRootLayout.setOnClickListener(mRootClickListener);
            }
        }
		return view;
	}

    @Override
    public String getType() {
        return "TOPIC_ACTION";
    }

    View.OnClickListener mRootClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
            mIntent.putExtra("topicId", mTopic.getId());
            context.startActivity(mIntent);
        }
    };

    /**
     * 分享点击事件
     */
    View.OnClickListener mShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(context, "topic_share");
            if (!LoginUtil.isLogin(context)) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            } else {
                Activity mActivity = (Activity) context;
                if (mActivity instanceof DefaultThirdPartyShareActivity) {
                    mShareActivity = (DefaultThirdPartyShareActivity) mActivity;
                    mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                    mDialog.setContentView(R.layout.share_pop);
                    Button mCancelLayout = (Button) mDialog.findViewById(R.id.share_pop_cancel);
                    LinearLayout mSinaLayout = (LinearLayout) mDialog.findViewById(R.id.share_sina_layout);
                    mSinaLayout.setTag(v.getTag());
                    mSinaLayout.setOnClickListener(mSinaShareClickListener);
                    LinearLayout mTencentLayout = (LinearLayout) mDialog.findViewById(R.id.share_tencent_layout);
                    mTencentLayout.setTag(v.getTag());
                    mTencentLayout.setOnClickListener(mTencentShareClickListener);
                    LinearLayout mWeiXinLayout = (LinearLayout) mDialog.findViewById(R.id.share_weixin_layout);
                    setWeiXinClickListener(mWeiXinLayout, (Topic) v.getTag());
                    if (!mDialog.isShowing()) {
                        mDialog.show();
                    }
                    mCancelLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    };

    public void setWeiXinClickListener(LinearLayout mWeiXinLayout, final Topic mTopic) {
        View.OnClickListener mWeiXinClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                String shareText = "#天天圈#我在"+mTopic.getGroupName()+"分享了一个话题:"+mTopic.getTitle();
                mShareActivity.shareWeiXinText(StringUtil.getShareText(shareText, mTopic.getDesc()));
//                if(mTopic.getImg() == null || "".equals(mTopic.getImg())) {
//                    shareWeiXinText(StringUtil.getShareText(shareText, mTopic.getDesc()));
//                }else {
//                    shareWeiXinPic(mHeaderContentImage, StringUtil.getShareText(shareText, mTopic.getDesc()));
//                }
            }
        };
        mWeiXinLayout.setOnClickListener(mWeiXinClickListener);
    }

    /**
     * 新浪微博分享
     */
    View.OnClickListener mSinaShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            String shareText = "#天天圈#我在"+mTopic.getGroupName()+"分享了一个话题:"+mTopic.getTitle();
            if(mTopic.getImg() == null || "".equals(mTopic.getImg())) {
                mShareActivity.sinaShareText(StringUtil.getShareText(shareText, mTopic.getDesc()));
            }else {
                mShareActivity.sinaSharePic(mContentImage, StringUtil.getShareText(shareText, mTopic.getDesc()));
            }
        }
    };

    /**
     * 腾讯微博分享
     */
    View.OnClickListener mTencentShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            String shareText = "#天天圈#我在"+mTopic.getGroupName()+"分享了一个话题:"+mTopic.getTitle();
            if(mTopic.getImg() == null || "".equals(mTopic.getImg())) {
                mShareActivity.tencentShareText(StringUtil.getShareText(shareText, mTopic.getDesc()));
            }else {
                mShareActivity.tencentSharePic(mContentImage, StringUtil.getShareText(shareText, mTopic.getDesc()));
            }
        }
    };

    /**
     * 评论点击事件
     */
    View.OnClickListener mPostsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            Intent mIntent = new Intent(context, TopicCommentActivity.class);
            mIntent.putExtra("from", true);
            mIntent.putExtra("id", mTopic.getId());
            context.startActivity(mIntent);
        }
    };

    /**
     * 喜欢点击事件
     */
    View.OnClickListener mSuppertsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LoginUtil.isLogin(context)) {
                Topic mTopic = (Topic) v.getTag();
                if (mTopic.getIsLiked() == 0) {
                    execLikeRequest(mTopic.getId(), mTopic);
                } else {
                    execUnLikeRequest(mTopic.getId(), mTopic);
                }
            } else {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        }
    };

    /**
     * 执行喜欢
     */
    private void execLikeRequest(long id, final Topic mTopic) {
        mApi.requestGetData(getLikeRequest(id), LinkTopic.class, new Response.Listener<LinkTopic>() {
            @Override
            public void onResponse(LinkTopic response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        mTopic.setIsLiked(1);
                        mLikeImage.setImageResource(R.drawable.icon_liketopic_active_tj);
                        mSupportsCountText.setText(String.format(context.getResources().getString(R.string.like_count), response.getSupportsCnt()));
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, null);
    }

    /**
     * 执行取消喜欢
     */
    private void execUnLikeRequest(long id, final Topic mTopic) {
        mApi.requestGetData(getUnLikeRequest(id), LinkTopic.class, new Response.Listener<LinkTopic>() {
            @Override
            public void onResponse(LinkTopic response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        mTopic.setIsLiked(0);
                        mLikeImage.setImageResource(R.drawable.icon_liketopic_tj);
                        mSupportsCountText.setText(String.format(context.getResources().getString(R.string.like_count), response.getSupportsCnt()));
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, null);
    }

    /**
     * 喜欢话题
     * @param id
     * @return
     */
    private String getLikeRequest(long id) {
        return "http://social.api.ttq2014.com/" + "topic/like.json" + "?id=" + id;
    }

    /**
     * 取消喜欢话题
     * @param id
     * @return
     */
    private String getUnLikeRequest(long id) {
        return "http://social.api.ttq2014.com/" + "topic/unlike.json" + "?id=" + id;
    }
}
