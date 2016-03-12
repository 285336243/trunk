package com.mzs.guaji.adapter.factory.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
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
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.feed.TopicPost;
import com.mzs.guaji.entity.feed.TopicPostFeed;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.TopicReplyActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopicPostHolder implements ViewHolder {
	private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
			.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
			.setPrettyPrinting().setVersion(1.0).create();
			
	private ImageView mUserAvatarImage;
	private TextView mUserNameText;
	private TextView mGroupNameText;    
	private TextView mCreateTimeText;   
	private TextView mTitleText;  
	private TextView mDescText; 
	private TextView mSupportsText;
    private RelativeLayout mReplyLayout;
    private RelativeLayout mSupportLayout;
    private LinearLayout mRootLayout;
	private View view;
	private Context context;
    private GuaJiAPI mApi;
    public static final String DOMAIN = "http://social.api.ttq2014.com/";
    public TopicPostHolder(Context context) {
        this.context = context;
        mApi = GuaJiAPI.newInstance(context);
    }


	@Override
	public void build(View v) {
		view = v;
		mUserAvatarImage = (ImageView) v.findViewById(R.id.personal_topic_post_list_item_user_avatar);
		mUserNameText = (TextView) v.findViewById(R.id.personal_topic_post_list_item_usre_name);
		mGroupNameText = (TextView) v.findViewById(R.id.personal_topic_post_list_item_group_name);
		mCreateTimeText = (TextView) v.findViewById(R.id.personal_topic_post_list_item_create_time);
		mTitleText = (TextView) v.findViewById(R.id.personal_topic_post_list_item_title);
		mDescText = (TextView) v.findViewById(R.id.personal_topic_post_list_item_desc);
		mSupportsText = (TextView) v.findViewById(R.id.personal_topic_post_post_supports_count);
        mReplyLayout = (RelativeLayout) v.findViewById(R.id.personal_topic_list_item_reply);
        mSupportLayout = (RelativeLayout) v.findViewById(R.id.personal_topic_list_item_supports);
        mRootLayout = (LinearLayout) v.findViewById(R.id.personal_topic_post_list_item_root_layout);
	}

	@Override
	public View renderView(Context context, JsonElement param) {
		TopicPostFeed mTopicPostFeed = mGson.fromJson(param, TopicPostFeed.class);
		ImageLoader mImageLoader = ImageLoader.getInstance();
		TopicPost mTopicPost = mTopicPostFeed.getTarget();
		if(mTopicPost.getUserAvatar() != null && !"".equals(mTopicPost.getUserAvatar())) {
			mImageLoader.displayImage(mTopicPost.getUserAvatar(), mUserAvatarImage, ImageUtils.imageLoader(context, 4));
		}
		mUserNameText.setText(mTopicPost.getUserNickname());
		mGroupNameText.setText(mTopicPostFeed.getAction());
		mCreateTimeText.setText(mTopicPost.getCreateTime());
		mTitleText.setText(mTopicPost.getTopic().getTitle());
		mDescText.setText(mTopicPost.getMessage());
		mSupportsText.setText(mTopicPost.getSupportsCnt()+"");
        mReplyLayout.setTag(mTopicPost);
        mReplyLayout.setOnClickListener(mReplyClickListener);
        mSupportLayout.setTag(mTopicPost);
        mSupportLayout.setOnClickListener(mSupportClickListener);
        mRootLayout.setTag(mTopicPost);
        mRootLayout.setOnClickListener(mRootClickListener);
		return view;
	}

    @Override
    public String getType() {
        return "TOPIC_POST";
    }

    View.OnClickListener mRootClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TopicPost mTopicPost = (TopicPost) v.getTag();
            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
            mIntent.putExtra("topicId", mTopicPost.getTopic().getId());
            context.startActivity(mIntent);
        }
    };

    /**
     * 回复点击事件
     */
    View.OnClickListener mReplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(LoginUtil.isLogin(context)) {
                TopicPost mPost = (TopicPost) v.getTag();
                Intent mIntent = new Intent(context, TopicReplyActivity.class);
                mIntent.putExtra("nickname", mPost.getUserNickname());
                mIntent.putExtra("commentId", mPost.getId());
                mIntent.putExtra("topicId", mPost.getTopic().getId());
                mIntent.putExtra("from", true);
                context.startActivity(mIntent);
            }else {
                Intent mIntent = new Intent(context, LoginActivity.class);
                context.startActivity(mIntent);
            }
        }
    };

    /**
     * 顶点击事件
     */
    View.OnClickListener mSupportClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final TopicPost mPost = (TopicPost) v.getTag();
            if(LoginUtil.isLogin(context)) {
                mApi.requestGetData(getSupportsRequest(mPost.getId()), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
                        @Override
                        public void onResponse(DefaultReponse response) {
                            if(response != null) {
                                if(response.getResponseCode() == 0) {
                                    mPost.setIsSupported(1);
                                }else {
                                    ToastUtil.showToast(context, response.getResponseMessage());
                                }
                            }
                        }
                    }, null);
            }else {
                Intent mIntent = new Intent(context, LoginActivity.class);
                context.startActivity(mIntent);
            }
        }

    };

    /**
     * 话题赞URL
     * @return
     */
    private String getSupportsRequest(long id) {
        return DOMAIN + "topic/post_support.json" + "?id=" + id;
    }
}
