package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.PersonalDynamicConditionAdapter;
import com.mzs.guaji.adapter.PersonalGralleryGridAdapter;
import com.mzs.guaji.adapter.SearchCircleAdapter;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.entity.OthersPersonCenterInfo;
import com.mzs.guaji.entity.SelfDynamicCondition;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.topic.entity.Feed;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 13-12-18.
 * 别人的资料
 */
public class OthersInformationActivity extends GuaJiActivity {
    private static final int DELETE_PHOTO_RESULT = 1;
    private Context context = OthersInformationActivity.this ;
    private DisplayImageOptions options;
    private long userId;
    private TextView mDynamicConditionText;
    private TextView mCircleText;
    private int selectorColor;
    private int defaultColor;
    private RelativeLayout mDynamicConditionLayout;
    private RelativeLayout mCircleLayout;
    private PersonalDynamicConditionAdapter mAdapter;
    private SearchCircleAdapter mCircleAdapter;
    private PersonalGralleryGridAdapter mGralleryGridAdapter;
    private HorizontalListView mHorizontalListView;
    private LinearLayout mGralleryEmptyLayout;
    private ImageView mBackGroundImage;
    private ImageView mUserAvatarImage;
    private TextView mNickNameText;
    private ImageView mGenderImage;
    private TextView mUserIntegralText;
    private TextView mAutographText;
    private RelativeLayout mSubTopicLayout;
    private TextView mSubTopicCountText;
    private RelativeLayout mJoinCircleLayout;
    private TextView mAddCircleCountText;
    private RelativeLayout mAttentionLayout;
    private TextView mAttentionCountText;
    private String tag = "DynamicCondition";
    private SelfDynamicCondition mDynamicCondition;
    private CircleSearch mCircleSearch;
    private TextView mTitleText;
    private FrameLayout mGralleryLayout;
    private LinearLayout mAddFollowLayout;
    private LinearLayout mSendMessageLayout;
    private LinearLayout mMoreLayout;
    private LinearLayout mRootLayout;
    private ImageView mAddFollowImage;
    private TextView mAddFollowText;
    private boolean isFollow;
    private PopupWindow mReportPopupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.others_information_layout);
        userId = getIntent().getLongExtra("userId", -1);
        options = ImageUtils.imageLoader(context, 4);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.others_information_listview);
        mRootView = this;
        super.onCreate(savedInstanceState);

        selectorColor = context.getResources().getColor(R.color.search_tab_selector_color);
        defaultColor = context.getResources().getColor(R.color.search_tab_color);
        mRootLayout = (LinearLayout) findViewById(R.id.others_information_root_layout);
        LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.others_information_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mTitleText = (TextView) findViewById(R.id.others_information_title);
        final View mHeaderView = View.inflate(context, R.layout.others_information_header_layout, null);
        mRefreshListView.getRefreshableView().addHeaderView(mHeaderView);
        findListHeaderView(mHeaderView);
        mAdapter = new PersonalDynamicConditionAdapter(this);

    }

    @Override
    protected void onInitialization() {
        super.onInitialization();mApi.requestGetData(getOthersInformationRequest(userId), OthersPersonCenterInfo.class, new Response.Listener<OthersPersonCenterInfo>() {

            @Override
            public void onResponse(OthersPersonCenterInfo response) {
                setOnLaodingGone();
                if (response != null) {
                    setListHeaderContent(response);
                    setListDynamicConditionContent();
                    setCircleContent();
                }
            }
        }, this);

    }

    /**
     * listview 头部控件初始化
     * @param mHeaderView
     */
    private void findListHeaderView(View mHeaderView) {
        mBackGroundImage = (ImageView) mHeaderView.findViewById(R.id.others_information_background);
        mUserAvatarImage = (ImageView) mHeaderView.findViewById(R.id.others_information_avatar_image);
        mNickNameText = (TextView) mHeaderView.findViewById(R.id.others_information_user_nickname);
        mGenderImage = (ImageView) mHeaderView.findViewById(R.id.others_information_gender_image);
        mUserIntegralText = (TextView) mHeaderView.findViewById(R.id.others_information_user_integral);
        mAutographText = (TextView) mHeaderView.findViewById(R.id.others_information_user_autograph);
        mSubTopicLayout = (RelativeLayout) mHeaderView.findViewById(R.id.others_information_sub_topic_layout);
        mSubTopicCountText = (TextView) mHeaderView.findViewById(R.id.others_information_sub_topic_count);
        mJoinCircleLayout = (RelativeLayout) mHeaderView.findViewById(R.id.others_information_join_circle_layout);
        mAddCircleCountText = (TextView) mHeaderView.findViewById(R.id.others_information_circle_count);
        mAttentionLayout = (RelativeLayout) mHeaderView.findViewById(R.id.others_information_attention_layout);
        mAttentionCountText = (TextView) mHeaderView.findViewById(R.id.others_information_attention_count);
        mHorizontalListView = (HorizontalListView) mHeaderView.findViewById(R.id.others_information_grallery_list);
        mGralleryEmptyLayout = (LinearLayout) mHeaderView.findViewById(R.id.others_information_grallery_empty_layout);
        mGralleryLayout = (FrameLayout) findViewById(R.id.others_information_grallery_layout);
        mDynamicConditionLayout = (RelativeLayout) mHeaderView.findViewById(R.id.others_information_dynamic_condition_layout);
        mDynamicConditionText = (TextView) mHeaderView.findViewById(R.id.others_information_dynamic_condition_text);
        mCircleLayout = (RelativeLayout) mHeaderView.findViewById(R.id.others_information_circle_layout);
        mCircleText = (TextView) mHeaderView.findViewById(R.id.others_information_circle_text);
        mAddFollowLayout = (LinearLayout) mHeaderView.findViewById(R.id.others_information_add_follow);
        mAddFollowImage = (ImageView) mHeaderView.findViewById(R.id.others_information_add_follow_image);
        mAddFollowText = (TextView) mHeaderView.findViewById(R.id.others_information_add_follow_text);
        mSendMessageLayout = (LinearLayout) mHeaderView.findViewById(R.id.others_information_send_message);
        mMoreLayout = (LinearLayout) mHeaderView.findViewById(R.id.others_information_more);

    }

    /**
     * 设置listview header内容
     * @param
     */
    private void setListHeaderContent(final OthersPersonCenterInfo mCenterInfo) {
        mTitleText.setText(mCenterInfo.getNickname());
        mAddFollowLayout.setOnClickListener(mAddFollowClickListener);
        if(mCenterInfo.getIsFollowed() != 0) {
            mAddFollowImage.setBackgroundResource(R.drawable.icon_unfollow_tj);
            mAddFollowText.setText(R.string.others_un_follow);
            isFollow = true;
        }else {
            mAddFollowImage.setBackgroundResource(R.drawable.icon_addfollow_tj);
            mAddFollowText.setText(R.string.others_add_follow);
            isFollow = false;
        }

        mSendMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(context)) {
                    Intent mIntent = new Intent(context, PrivateLetterDetailActivity.class);
                    mIntent.putExtra(Intents.INTENT_EXTRA_PERSON_NICKNAME, mCenterInfo.getNickname());
                    mIntent.putExtra(Intents.INTENT_EXTRA_PERSON_USERID, mCenterInfo.getUserId());
                    startActivity(mIntent);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        mMoreLayout.setOnClickListener(mMoreClickListener);
        mSubTopicLayout.setOnClickListener(mSubTopicListener);
        mJoinCircleLayout.setOnClickListener(mAddCircleListener);
        mAttentionLayout.setOnClickListener(mAttentionListener);
        mGralleryEmptyLayout.setOnClickListener(mGralleryEmptyClickListener);
        mDynamicConditionLayout.setOnClickListener(mDynamicConditionListener);
        mCircleLayout.setOnClickListener(mCircleListener);
        if(mCenterInfo.getBgImg() != null || !"".equals(mCenterInfo.getBgImg())) {
            mImageLoader.displayImage(mCenterInfo.getBgImg(), mBackGroundImage, options);
        }

        if(mCenterInfo.getAvatar() != null && !"".equals(mCenterInfo.getAvatar())) {
            mImageLoader.displayImage(mCenterInfo.getAvatar(), mUserAvatarImage, options);
//            LoginUtil.saveUserAvatar(context, mCenterInfo.getAvatar());
        }
        mNickNameText.setText(mCenterInfo.getNickname());
        if("m".equals(mCenterInfo.getGender())) {
            mGenderImage.setImageResource(R.drawable.icon_man_tj);
        }else {
            mGenderImage.setImageResource(R.drawable.icon_lady_tj);
        }
        mUserIntegralText.setText(mCenterInfo.getScore()+"");
        if(mCenterInfo.getSignature() != null && !"".equals(mCenterInfo.getSignature())) {
            mAutographText.setText(mCenterInfo.getSignature());
        }
        mSubTopicCountText.setText(mCenterInfo.getTopicsCnt()+"");
        mAddCircleCountText.setText(mCenterInfo.getGroupsCnt()+"");
        mAttentionCountText.setText(mCenterInfo.getFollowsCnt()+"");
        if(mCenterInfo.getPics() != null) {
            if(mGralleryGridAdapter == null) {
                mGralleryGridAdapter = new PersonalGralleryGridAdapter(context, mCenterInfo.getPics());
                mHorizontalListView.setAdapter(mGralleryGridAdapter);
            }else {
                mGralleryGridAdapter.addPic(mCenterInfo.getPics());
            }
            mHorizontalListView.setOnItemClickListener(mItemClickListener);
        }else {
            mGralleryLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        mApi.requestGetData(getOthersInformationRequest(userId), OthersPersonCenterInfo.class, new Response.Listener<OthersPersonCenterInfo>() {

            @Override
            public void onResponse(OthersPersonCenterInfo response) {
                mRefreshListView.onRefreshComplete();
                if(response != null) {
                    setListHeaderContent(response);
                }
            }
        }, this);

        page = 1;
        if("DynamicCondition".equals(tag)) {
            mApi.requestGetData(getOthersDynamicConditionRequest(userId, page, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
                @Override
                public void onResponse(SelfDynamicCondition response) {
                    if(response != null && response.getFeeds() != null && mAdapter != null) {
                        mAdapter.addFeeds(response.getFeeds());
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_dynamic_condition));
                    }
                }
            }, this);
        }else {
            mApi.requestGetData(getOthersJoinCircle(userId, page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null && response.getGroups() != null && mCircleAdapter != null) {
                        mCircleAdapter.clear();
                        mCircleAdapter.addGroupItem(response.getGroups());
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_circle));
                    }
                }
            }, this);
        }
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        page = page + 1;
        if("DynamicCondition".equals(tag)) {
            if(mDynamicCondition != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mDynamicCondition.getTotal())) {
                    setRefreshListViewMode();
                    ToastUtil.showToast(context, R.string.toast_last_page_tip);
                    return;
                }
            }
            mApi.requestGetData(getOthersDynamicConditionRequest(userId, page, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
                @Override
                public void onResponse(SelfDynamicCondition response) {
                    if(response != null) {
                        if(response.getResponseCode() == 0) {
                            if(mAdapter != null && response.getFeeds() != null) {
                                for (Feed feed : response.getFeeds()) {
                                    mAdapter.addItem(feed);
                                }
                            }
                        }
                    }
                }
            }, this);
        }else {
            if(mCircleSearch != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mCircleSearch.getTotal())) {
                    setRefreshListViewMode();
                    ToastUtil.showToast(context, R.string.toast_last_page_tip);
                    return;
                }
            }
            mApi.requestGetData(getOthersJoinCircle(userId, page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null) {
                        if(response.getResponseCode() == 0) {
                            if(mAdapter != null && response.getGroups() != null) {
                                mCircleAdapter.addGroupItem(response.getGroups());
                            }
                        }
                    }
                }
            }, this);
        }
    }

    View.OnClickListener mGralleryEmptyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, PhotoGralleryListActivity.class);
            startActivityForResult(mIntent, DELETE_PHOTO_RESULT);
        }
    };

    /**
     * 加关注点击事件
     */
    View.OnClickListener mAddFollowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!LoginUtil.isLogin(context)) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            } else {
                mAddFollowLayout.setEnabled(false);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("userId", userId + "");
                if (isFollow) {
                    TipsUtil.showPopupWindow(context, mRootLayout, R.string.delete_follow);
                    mApi.requestPostData(getUnFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
                        @Override
                        public void onResponse(DefaultReponse response) {
                            mAddFollowLayout.setEnabled(true);
                            TipsUtil.dismissPopupWindow();
                            if (response != null && response.getResponseCode() == 0) {
                                isFollow = false;
                                ToastUtil.showToast(context, R.string.delete_follow_succeed);
                                mAddFollowText.setText(R.string.others_add_follow);
                                mAddFollowImage.setBackgroundResource(R.drawable.icon_addfollow_tj);
                            } else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }, OthersInformationActivity.this);
                } else {
                    TipsUtil.showPopupWindow(context, mRootLayout, R.string.add_follow);
                    mApi.requestPostData(getFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
                        @Override
                        public void onResponse(DefaultReponse response) {
                            mAddFollowLayout.setEnabled(true);
                            TipsUtil.dismissPopupWindow();
                            if (response != null && response.getResponseCode() == 0) {
                                isFollow = true;
                                ToastUtil.showToast(context, R.string.add_follow_succeed);
                                mAddFollowText.setText(R.string.others_un_follow);
                                mAddFollowImage.setBackgroundResource(R.drawable.icon_unfollow_tj);
                            } else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }, OthersInformationActivity.this);
                }
            }
        }
    };

    /**
     * 更多点击事件
     */
    View.OnClickListener mMoreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View v = View.inflate(context, R.layout.others_report_pop, null);
            mReportPopupWindow = new PopupWindow(v, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            Button mReportButton = (Button) v.findViewById(R.id.others_report_button);
            mReportButton.setOnClickListener(mReportClickListener);
            Button mCancelButton = (Button) v.findViewById(R.id.others_report_cancel);
            mCancelButton.setOnClickListener(mReportCancelClickListener);
            if(!mReportPopupWindow.isShowing()) {
                mReportPopupWindow.showAtLocation(mRootLayout, Gravity.BOTTOM, 0, 0);
            }
        }
    };

    /**
     * 关闭举报popupwindow
     */
    private void dismissReportWindow() {
        if(mReportPopupWindow != null && mReportPopupWindow.isShowing()) {
            mReportPopupWindow.dismiss();
        }
    }

    /**
     * popupwindow 取消按钮点击事件
     */
    View.OnClickListener mReportCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismissReportWindow();
        }
    };

    /**
     * popupwindow 举报按钮点击事件
     */
    View.OnClickListener mReportClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final  View view) {
            view.setEnabled(false);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("id", userId+"");
            headers.put("type", "USER");
            headers.put("comment", "");
            mApi.requestPostData(getReportRequest(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>(){
                @Override
                public void onResponse(DefaultReponse response) {
                    view.setEnabled(true);
                    if(response != null) {
                        dismissReportWindow();
                        if(response.getResponseCode() == 0) {
                            ToastUtil.showToast(context, "举报成功");
                        }else {
                            ToastUtil.showToast(context, response.getResponseMessage());
                        }
                    }
                }
            }, OthersInformationActivity.this);
        }
    };

    /**
     * 相册item点击事件
     */
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent mIntent = new Intent(context, PhotoGralleryListActivity.class);
            mIntent.putExtra("isSelf", false);
            mIntent.putExtra("userId", userId);
            startActivity(mIntent);
        }
    };

    /**
     * 发布话题的点击事件
     */
    View.OnClickListener mSubTopicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, PersonalTopicActivity.class);
            mIntent.putExtra("userId", userId);
            mIntent.putExtra("isSelf", false);
            startActivity(mIntent);
        }
    };

    /**
     * 加入圈子的点击事件
     */
    View.OnClickListener mAddCircleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, PersonalCircleActivity.class);
            mIntent.putExtra("userId", userId);
            mIntent.putExtra("isSelf", false);
            startActivity(mIntent);
        }
    };

    /**
     * 关注点击事件
     */
    View.OnClickListener mAttentionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, FollowAndFansActivity.class);
            mIntent.putExtra("userId", userId);
            mIntent.putExtra("isSelf", false);
            startActivity(mIntent);
        }
    };

    /**
     * 动态点击事件
     */
    View.OnClickListener mDynamicConditionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tag = "DynamicCondition";
            mDynamicConditionLayout.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
            mCircleLayout.setBackgroundResource(R.drawable.bdg_tab_center_tj);
            mDynamicConditionText.setTextColor(selectorColor);
            mCircleText.setTextColor(defaultColor);
            if(mAdapter != null) {
//                SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
//                mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                mRefreshListView.setAdapter(mAdapter);
            }else {
                setRefreshListViewMode();
                mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_dynamic_condition));
            }
        }
    };

    /**
     * 圈子点击事件
     */
    View.OnClickListener mCircleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tag = "Circle";
            mDynamicConditionLayout.setBackgroundResource(R.drawable.bdg_tab_center_tj);
            mCircleLayout.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
            mDynamicConditionText.setTextColor(defaultColor);
            mCircleText.setTextColor(selectorColor);
            if(mCircleAdapter != null) {
//                SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mCircleAdapter, 150);
//                mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                mRefreshListView.setAdapter(mCircleAdapter);
            }else {
                setRefreshListViewMode();
                mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.join_circle_empty));
            }
        }
    };

    /**
     * 设置动态内容
     */
    private void setListDynamicConditionContent() {

        mApi.requestGetData(getOthersDynamicConditionRequest(userId, page, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
            @Override
            public void onResponse(SelfDynamicCondition response) {
                if(response != null && response.getFeeds() != null) {
                    mDynamicCondition = response;
                    for (Feed feed : response.getFeeds()) {
                        mAdapter.addItem(feed);
                    }
                    SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                    mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                    mRefreshListView.setAdapter(mAnimationAdapter);
                }else {
                    setRefreshListViewMode();
                    mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_dynamic_condition));
                }
            }
        }, OthersInformationActivity.this);
    }

    /**
     * 设置圈子内容
     */
    private void setCircleContent() {
        mApi.requestGetData(getOthersJoinCircle(userId, page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
            @Override
            public void onResponse(final CircleSearch response) {
                if(response != null && response.getGroups() != null) {
                    mCircleSearch = response;
                    mCircleAdapter = new SearchCircleAdapter(context, response.getGroups());
                    SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mCircleAdapter, 150);
                    mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
//                    mRefreshListView.setAdapter(mAnimationAdapter);
                    mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Group mGroup = response.getGroups().get((int) id);
                            if("OFFICIAL".equals(mGroup.getType())) {
                                Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
                                mIntent.putExtra("img", mGroup.getImg());
                                mIntent.putExtra("id", mGroup.getId());
                                mIntent.putExtra("name", mGroup.getName());
                                context.startActivity(mIntent);
                            }else {
                                Intent privateIntent = new Intent(context, PrivateCircleActivity.class);
                                privateIntent.putExtra("id", mGroup.getId());
                                context.startActivity(privateIntent);
                            }
                        }
                    });
                }else {
                    setRefreshListViewMode();
                    mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.join_circle_empty));
                }
            }
        }, OthersInformationActivity.this);
    }

    /**
     * 查看别人个人资料的URL
     * @param userId
     */
    private String getOthersInformationRequest(long userId) {
        return DOMAIN + "user/read.json" + "?userId=" + userId;
    }

    /**
     * 查看别人的动态内容URL
     * @param userId
     * @param page
     * @param count
     * @return
     */
    private String getOthersDynamicConditionRequest(long userId, long page, long count) {
        return DOMAIN + "feed/read.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }

    /**
     * 查看别人加入的圈子URL
     * @param userId
     * @param page
     * @param count
     * @return
     */
    private String getOthersJoinCircle(long userId, long page, long count) {
        return DOMAIN + "user/read_create_group.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }

    /**
     * 当没有数据时,设置Listview的mode为不可上拉
     */
    private void setRefreshListViewMode() {
        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    /**
     * 添加关注
     * @return
     */
    private String getFollowRequestUrl() {
        return DOMAIN + "user/follow.json";
    }

    /**
     * 取消关注
     * @return
     */
    private String getUnFollowRequestUrl() {
        return DOMAIN + "user/unfollow.json";
    }

    /**
     * 举报URL
     * @return
     */
    private String getReportRequest() {
        return DOMAIN + "system/expose.json";
    }
}
