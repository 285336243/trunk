package com.mzs.guaji.topic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.topic.entity.Post;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.TopicReplyActivity;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 13-12-25.
 */
public class TopicDetailsAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private List<Post> mPosts = new ArrayList<Post>();
    private ImageLoader mImageLoader;
    public static final String DOMAIN = "http://social.api.ttq2014.com/";
    private GuaJiAPI mApi;
//    private PullToRefreshExpandableListView mRefreshListView;
    private LinearLayout mReportLayout;
    private LinearLayout mSupportLayout;
    private boolean isReport = false;
//    private RelativeLayout mAnimLayout;
//    private ImageView mAnimImage;
//    private TextView mAnimText;
    private TopicDetailsItemViewHolder mHolder;
    private long id;
//    private final String type;
    private ExpandableListView listView;
    private String type;
    private TopicDetailsService service;

    @Inject
    public TopicDetailsAdapter(Context context) {
        this.context = context;
//        this.id = id;
//        this.mRefreshListView = mRefreshListView;
//        this.mAnimLayout = mAnimLayout;
//        this.mAnimImage = mAnimImage;
//        this.mAnimText = mAnimText;
//        this.type = type;
        mImageLoader = ImageLoader.getInstance();
        mApi = GuaJiAPI.newInstance(context);

    }

    public void setExpandableListView(ExpandableListView listView) {
        this.listView = listView;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTopicDetailsService(TopicDetailsService service) {
        this.service = service;
    }

    public void clear() {
        this.mPosts.clear();
        notifyDataSetChanged();
    }

    public void addPosts(Collection<Post> mPosts) {
        for(Post mPost : mPosts) {
            this.mPosts.add(mPost);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mPosts.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mPosts.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        TopicDetailsItemViewHolder mHolder = null;
        if(v == null) {
            mHolder = new TopicDetailsItemViewHolder();
            v = View.inflate(context, R.layout.topic_details_item, null);
            mHolder.mAvatarImage = (ImageView) v.findViewById(R.id.topic_details_item_avatar);
            mHolder.mNicknameText = (TextView) v.findViewById(R.id.topic_details_nickname);
            mHolder.mContentText = (TextView) v.findViewById(R.id.topic_details_content);
            mHolder.mTimeText = (TextView) v.findViewById(R.id.topic_details_item_time);
            mHolder.mSupportsCountText = (TextView) v.findViewById(R.id.topic_details_item_support_count);
            mHolder.mReplyText = (TextView) v.findViewById(R.id.topic_details_item_reply_nickname);
            v.setTag(mHolder);
        }else {
            mHolder = (TopicDetailsItemViewHolder) v.getTag();
        }
        this.mHolder = mHolder;
        final Post mPost = mPosts.get(groupPosition);
        mPost.getPost();
        if(mPost != null) {
            mImageLoader.displayImage(mPost.getUserAvatar(), mHolder.mAvatarImage, ImageUtils.imageLoader(context, 4));
            mHolder.mAvatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkipPersonalCenterUtil.skipPersonalCenter(context, mPost.getUserId());
                }
            });
            mHolder.mNicknameText.setText(mPost.getUserNickname());
            mHolder.mContentText.setText(mPost.getMessage());
            mHolder.mTimeText.setText(mPost.getCreateTime());
            mHolder.mSupportsCountText.setText(mPost.getSupportsCnt()+"顶");
            if(mPost.getPost() != null) {
                mHolder.mReplyText.setVisibility(View.VISIBLE);
                mHolder.mReplyText.setText("回复 " + mPost.getPost().getUserNickname());
            }else{
                mHolder.mReplyText.setVisibility(View.GONE);
            }
        }
        return v;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, final View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.topic_details_item_reply, null);
        mReportLayout = (LinearLayout) v.findViewById(R.id.topic_details_reply_report);
        mReportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReport) {
                    ToastUtil.showToast(context, "您已经举报过该内容");
                    return;
                }
                new AsyncTask<Void, Void, DefaultReponse>(){
                    @Override
                    protected DefaultReponse doInBackground(Void... params) {
                        ResourcePager<DefaultReponse> pager = new ResourcePager<DefaultReponse>() {
                            @Override
                            protected Object getId(DefaultReponse resource) {
                                return null;
                            }

                            @Override
                            public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                Map<String, String> bodyMap = new HashMap<String, String>();
                                bodyMap.put("id", mPosts.get(groupPosition).getId() + "");
                                bodyMap.put("type", type);
                                bodyMap.put("comment", "");
                                return service.pageReport(bodyMap);
                            }
                        };
                        try {
                            pager.next();
                            return pager.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(DefaultReponse defaultReponse) {
                        super.onPostExecute(defaultReponse);
                        if (defaultReponse != null) {
                            if (defaultReponse.getResponseCode() == 0) {
                                isReport = true;
                                ToastUtil.showToast(context, "举报成功");
                            } else {
                                ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                            }
                        }
                    }
                }.execute();
            }
        });

        LinearLayout mCancelLayout = (LinearLayout) v.findViewById(R.id.topic_details_reply_cancel);
        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.collapseGroup(groupPosition);
            }
        });

        LinearLayout mReplyLayout = (LinearLayout) v.findViewById(R.id.topic_details_reply_layout);
        mReplyLayout.setTag(mPosts.get(groupPosition));
        mReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.collapseGroup(groupPosition);
                if (LoginUtil.isLogin(context)) {
                    Post post = mPosts.get(groupPosition);
                    Intent intent = new Intents(context, TopicReplyActivity.class)
                            .add(IConstant.TOPIC_TYPE, type)
                            .add(IConstant.TOPIC_NICKNAME, post.getUserNickname())
                            .add(IConstant.COMMENT_ID, post.getId()).toIntent();
                    context.startActivity(intent);
                } else {
                    context.startActivity(new Intents(context, LoginActivity.class).toIntent());
                }
            }
        });

        mSupportLayout = (LinearLayout) v.findViewById(R.id.topic_details_reply_support);
        mSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(context)) {
                    final Post post = mPosts.get(groupPosition);
                    if (post.getIsSupported() == 1) {
                        ToastUtil.showToast(context, "您已经赞过了");
                    } else {
                        new AsyncTask<Void, Void, DefaultReponse>(){
                            @Override
                            protected DefaultReponse doInBackground(Void... params) {
                                ResourcePager<DefaultReponse> pager = new ResourcePager<DefaultReponse>() {
                                    @Override
                                    protected Object getId(DefaultReponse resource) {
                                        return null;
                                    }

                                    @Override
                                    public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                        return service.pageSupport(post.getId(), type);
                                    }
                                };
                                try {
                                    pager.next();
                                    return pager.get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(DefaultReponse defaultReponse) {
                                super.onPostExecute(defaultReponse);
                                if (defaultReponse != null) {
                                    if (defaultReponse.getResponseCode() == 0) {
                                        post.setIsSupported(1);
                                        ToastUtil.showToast(context, "已赞");
                                    } else {
                                        ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                                    }
                                }
                            }
                        }.execute();
                    }
                } else {
                    context.startActivity(new Intents(context, LoginActivity.class).toIntent());
                }
                listView.collapseGroup(groupPosition);
            }
        });
        isReport = false;
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public static class TopicDetailsItemViewHolder {
        public ImageView mAvatarImage;
        public TextView mNicknameText;
        public TextView mContentText;
        public TextView mTimeText;
        public TextView mSupportsCountText;
        public TextView mReplyText;
    }

    private void setCopyLongClickListener(View v, final TopicDetailsItemViewHolder mHolder) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GiveUpEditingDialog.showCopyDialog(context, "复制", "", mHolder.mContentText.getText().toString());
                return true;
            }
        });
    }

//    /**
//     * 顶点击事件
//     */
//    View.OnClickListener mSupportClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            final int groupPosition = (Integer) v.getTag();
//            final Post mPost = mPosts.get(groupPosition);
//            if(LoginUtil.isLogin(context)) {
//                mAnimLayout.setVisibility(View.VISIBLE);
//                updateSupportCount(groupPosition);
//                if(mPost.getIsSupported() == 1) {
//                    mAnimImage.setVisibility(View.GONE);
//                    mAnimText.setVisibility(View.VISIBLE);
//                    Animation mAnimationLayout = AnimationUtils.loadAnimation(context, R.anim.support_end_anim);
//                    mAnimationLayout.setAnimationListener(new TopicAnimationListener(4));
//                    mAnimLayout.startAnimation(mAnimationLayout);
//                    return;
//                }
//                mAnimText.setVisibility(View.GONE);
//                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.support_background_anim);
//                mAnimation.setAnimationListener(new TopicAnimationListener(0));
//                mAnimLayout.startAnimation(mAnimation);
//                mApi.requestGetData(getSupportsRequest(mPost.getId()), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
//                    @Override
//                    public void onResponse(DefaultReponse response) {
//                        if(response != null) {
//                            if(response.getResponseCode() == 0) {
//                                mPost.setIsSupported(1);
//                            }else {
//                                ToastUtil.showToast(context, response.getResponseMessage());
//                            }
//                        }
//                    }
//                }, null);
//            }else {
//                Intent mIntent = new Intent(context, LoginActivity.class);
//                context.startActivity(mIntent);
//            }
//        }
//    };
//
//    private void updateSupportCount(final int groupPosition) {
//        mApi.requestGetData(getTopicDetailsContentRequest(id, 1, Integer.MAX_VALUE), TopicDetailsPosts.class, new Response.Listener<TopicDetailsPosts>() {
//            @Override
//            public void onResponse(TopicDetailsPosts response) {
//                mRefreshListView.onRefreshComplete();
//                if(response != null) {
//                    if(response.getResponseCode() == 0) {
//                        if(response.getPosts() != null) {
//                            Post mPost = response.getPosts().get(groupPosition);
//                            if(mPost != null) {
//                                mHolder.mSupportsCountText.setText(mPost.getSupportsCnt()+"顶");
//                            }
//                        }
//                    }else {
//                        ToastUtil.showToast(context, response.getResponseMessage());
//                    }
//                }
//            }
//        }, null);
//    }
//
//    private class TopicAnimationListener implements Animation.AnimationListener {
//        private int action;
//        public TopicAnimationListener(int action) {
//            this.action = action;
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            if(action == 0) {
//                mAnimImage.setVisibility(View.VISIBLE);
//                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.support_scale);
//                mAnimation.setAnimationListener(new TopicAnimationListener(1));
//                mAnimImage.startAnimation(mAnimation);
//            }else if (action == 1) {
//                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.support_scale_end_anim);
//                mAnimation.setAnimationListener(new TopicAnimationListener(2));
//                mAnimImage.startAnimation(mAnimation);
//            }else if (action == 2) {
//                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.support_end_anim);
//                mAnimation.setAnimationListener(new TopicAnimationListener(3));
//                mAnimImage.startAnimation(mAnimation);
//            }else if(action == 3) {
//                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.support_end_anim);
//                mAnimation.setAnimationListener(new TopicAnimationListener(4));
//                mAnimLayout.startAnimation(mAnimation);
//            }else if(action == 4) {
//                mAnimLayout.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//
//        }
//    }

//    /**
//     * 话题赞URL
//     * @return
//     */
//    private String getSupportsRequest(long id) {
//        return DOMAIN + "topic/post_support.json" + "?id=" + id + "&type=" + type;
//    }
}
