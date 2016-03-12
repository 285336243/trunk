package com.mzs.guaji.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.android.volley.Response;
import com.android.volley.SynchronizationHttpRequest;
import com.google.gson.Gson;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.PersonalDynamicConditionAdapter;
import com.mzs.guaji.adapter.PersonalGralleryGridAdapter;
import com.mzs.guaji.adapter.SearchCircleAdapter;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.entity.Badges;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.entity.PersonCenterInfo;
import com.mzs.guaji.entity.SelfDynamicCondition;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.entity.Feed;
import com.mzs.guaji.topic.entity.TopicAction;
import com.mzs.guaji.topic.entity.TopicPost;
import com.mzs.guaji.ui.FollowAndFansActivity;
import com.mzs.guaji.ui.MessageActivity;
import com.mzs.guaji.ui.PersonalCircleActivity;
import com.mzs.guaji.ui.PersonalTopicActivity;
import com.mzs.guaji.ui.PhotoGralleryListActivity;
import com.mzs.guaji.ui.SettingActivity;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 我的信息页面
 * @author lenovo
 *
 */
public class  PersonalCenterFragment extends GuaJiFragment {

	private static final int DELETE_PHOTO_RESULT = 1;
	private Context context ;
	private DisplayImageOptions options;
	private long userId;
	private int selectorColor;
	private int defaultColor;
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
	private RelativeLayout mAddCircleLayout;
	private TextView mAddCircleCountText;
	private RelativeLayout mAttentionLayout;
	private TextView mAttentionCountText;
	private PersonCenterInfo mCenterInfo;
    private SelfDynamicCondition mDynamicCondition;
    private CircleSearch mCircleSearch;
    private RadioButton mDynamicConditionButton;
    private RadioButton mCircleButton;
    private RadioGroup mGroup;
    private View mFootView;
    private int dynamicConditionPage = 1;
    private int circlePage = 1;
    private ImageView badgeView;
    private final Gson gson = GsonUtils.createGson();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		options = ImageUtils.imageLoader(context, 4);
		selectorColor = context.getResources().getColor(R.color.search_tab_selector_color);
		defaultColor = context.getResources().getColor(R.color.search_tab_color);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.personal_center_layout, null);
        mRootView = v;
        mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.personal_center_listview);
        super.onCreateView(inflater, container, savedInstanceState);

        mFootView = View.inflate(context, R.layout.list_foot_layout, null);
		LinearLayout mSettingLayout = (LinearLayout) v.findViewById(R.id.personal_settings);
		mSettingLayout.setOnClickListener(mSettingClickListener);
        TextView mMessageText = (TextView) v.findViewById(R.id.personal_message_text);
        mMessageText.setOnClickListener(mMessageClickListener);
        badgeView = (ImageView) v.findViewById(R.id.personal_message_badge);
		final View mHeaderView = View.inflate(context, R.layout.personal_center_header_layout, null);
		mRefreshListView.getRefreshableView().addHeaderView(mHeaderView);
        mRefreshListView.setOnItemClickListener(itemClickListener);

        mAdapter = new PersonalDynamicConditionAdapter(getActivity());
        findListHeaderView(mHeaderView);
		return v;
	}

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Feed feed = (Feed) parent.getItemAtPosition(position);
            Intent intent = null;
            if ("TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).toIntent();
            } else if ("ACTIVITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
            } else if ("CELEBRITY_TOPIC_ACTION".equals(feed.getTargetType())) {
                TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
            } else if ("TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).toIntent();
            } else if ("ACTIVITY_TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
            } else if ("CELEBRITY_TOPIC_POST".equals(feed.getTargetType())) {
                TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
            }

            if (intent != null)
                startActivity(intent);
        }
    };
	
	private void findListHeaderView(View mHeaderView) {
        mGroup = (RadioGroup) mHeaderView.findViewById(R.id.personal_header_radiogroup);
        mDynamicConditionButton = (RadioButton) mHeaderView.findViewById(R.id.personal_dynamic_condition_btn);
        mCircleButton = (RadioButton) mHeaderView.findViewById(R.id.personal_circle_btn);
		mBackGroundImage = (ImageView) mHeaderView.findViewById(R.id.personal_background);
		mUserAvatarImage = (ImageView) mHeaderView.findViewById(R.id.personal_user_avatar_image);
		mNickNameText = (TextView) mHeaderView.findViewById(R.id.personal_user_nickname);
		mGenderImage = (ImageView) mHeaderView.findViewById(R.id.personal_gender_image);
		mUserIntegralText = (TextView) mHeaderView.findViewById(R.id.personal_user_integral);
		mAutographText = (TextView) mHeaderView.findViewById(R.id.personal_user_autograph);
		mSubTopicLayout = (RelativeLayout) mHeaderView.findViewById(R.id.personal_sub_topic_layout);
		mSubTopicCountText = (TextView) mHeaderView.findViewById(R.id.personal_sub_topic_count);
		mAddCircleLayout = (RelativeLayout) mHeaderView.findViewById(R.id.personal_add_circle_layout);
		mAddCircleCountText = (TextView) mHeaderView.findViewById(R.id.personal_add_circle_count);
		mAttentionLayout = (RelativeLayout) mHeaderView.findViewById(R.id.personal_attention_layout);
		mAttentionCountText = (TextView) mHeaderView.findViewById(R.id.personal_attention_count);
		mHorizontalListView = (HorizontalListView) mHeaderView.findViewById(R.id.personal_grallery_list);
		mGralleryEmptyLayout = (LinearLayout) mHeaderView.findViewById(R.id.personal_grallery_empty_layout);
	}
	
	/**
	 * 设置listview header内容
	 * @param
	 */
	private void setListHeaderContent(PersonCenterInfo mCenterInfo) {
        mGroup.setOnCheckedChangeListener(mCheckedChangeListener);
		mSubTopicLayout.setOnClickListener(mSubTopicListener);
		mAddCircleLayout.setOnClickListener(mAddCircleListener);
		mAttentionLayout.setOnClickListener(mAttentionListener);
		mGralleryEmptyLayout.setOnClickListener(mGralleryEmptyClickListener);
		if(mCenterInfo.getBgImg() != null || !"".equals(mCenterInfo.getBgImg())) {
			mImageLoader.displayImage(mCenterInfo.getBgImg(), mBackGroundImage, options);
		}
		
		if(mCenterInfo.getAvatar() != null && !"".equals(mCenterInfo.getAvatar())) {
			mImageLoader.displayImage(mCenterInfo.getAvatar(), mUserAvatarImage, options);
			LoginUtil.saveUserAvatar(context, mCenterInfo.getAvatar());
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
            mHorizontalListView.setVisibility(View.VISIBLE);
            mGralleryEmptyLayout.setVisibility(View.GONE);
			if(mGralleryGridAdapter == null) {
				mGralleryGridAdapter = new PersonalGralleryGridAdapter(context, mCenterInfo.getPics());
				mHorizontalListView.setAdapter(mGralleryGridAdapter);
			}else {
				mGralleryGridAdapter.addPic(mCenterInfo.getPics());
			}
			mHorizontalListView.setOnItemClickListener(mItemClickListener);
		}else {
			mHorizontalListView.setVisibility(View.GONE);
			mGralleryEmptyLayout.setVisibility(View.VISIBLE);
		}
	}

    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mRefreshListView.getRefreshableView().removeFooterView(mFootView);
            isFootShow = false;
            switch (checkedId) {
                case R.id.personal_dynamic_condition_btn:
                    mDynamicConditionButton.setTextColor(selectorColor);
                    mDynamicConditionButton.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
                    mCircleButton.setTextColor(defaultColor);
                    mCircleButton.setBackgroundResource(R.drawable.bdg_tab_center_tj);
                    setListDynamicConditionContent();
                    break;
                case R.id.personal_circle_btn:
                    setCircleContent();
                    break;
            }
        }
    };

    /**
     * 设置动态内容
     */
    private void setListDynamicConditionContent() {
        dynamicConditionPage = 1;
        mApi.requestGetData(getRequestSelfUrl(dynamicConditionPage, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
            @Override
            public void onResponse(SelfDynamicCondition response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getFeeds() != null && response.getFeeds().size() >0) {
                            mDynamicCondition = response;
                            mAdapter.addFeeds(response.getFeeds());
                            SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                            mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                            mRefreshListView.setAdapter(mAnimationAdapter);
                        }else {
                            mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_dynamic_condition));
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, PersonalCenterFragment.this);
    }

    /**
     * 设置圈子内容
     */
    private void setCircleContent() {
        mDynamicConditionButton.setTextColor(defaultColor);
        mDynamicConditionButton.setBackgroundResource(R.drawable.bdg_tab_center_tj);
        mCircleButton.setTextColor(selectorColor);
        mCircleButton.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
        circlePage = 1;
        mApi.requestGetData(getRequestCircleUrl(circlePage, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
            @Override
            public void onResponse(CircleSearch response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getGroups() != null) {
                            mCircleSearch = response;
                            mCircleAdapter = new SearchCircleAdapter(context, response.getGroups());
                            SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mCircleAdapter, 150);
                            mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        }else {
                            mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_circle));
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, PersonalCenterFragment.this);
    }

    private void showScoreDialog(String message) {
        final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.first_login_dialog);
        TextView mTextView = (TextView) mDialog.findViewById(R.id.first_login_text);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.first_login_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        mTextView.setText(message);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    protected void onInitialization() {
        super.onInitialization();
//        setListDynamicConditionContent();
        mApi.requestGetData(getRequestUrl(userId), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {

            @Override
            public void onResponse(final PersonCenterInfo response) {
                setOnLaodingGone();
                if (response != null) {
                    mCenterInfo = response;
                    setListHeaderContent(response);
                    setListDynamicConditionContent();
                    final SharedPreferences preferences = getActivity().getSharedPreferences(IConstant.GUIDE, Context.MODE_PRIVATE);
                    final int guide = preferences.getInt(IConstant.PERSION_CENTGER_GUIDE, 0);
                    if (guide == 0) {
                        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.setContentView(R.layout.persion_center_guice);
                        View v = dialog.findViewById(R.id.personal_center_guide_root);
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                final SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt(IConstant.PERSION_CENTGER_GUIDE, 1).commit();

                                if(response.getGivenScore() != null) {
                                    showScoreDialog(response.getGivenScore().getMessage());
                                }
                            }
                        });
                        if (!dialog.isShowing())
                            dialog.show();
                    } else {
                        if(response.getGivenScore() != null) {
                            showScoreDialog(response.getGivenScore().getMessage());
                        }
                    }
                }
            }
        }, PersonalCenterFragment.this);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        mApi.requestGetData(getRequestUrl(userId), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {

            @Override
            public void onResponse(PersonCenterInfo response) {
                mRefreshListView.onRefreshComplete();
                if(response != null) {
                    setListHeaderContent(response);
                }
            }
        }, PersonalCenterFragment.this);

        if(mDynamicConditionButton.isChecked()) {
            dynamicConditionPage = 1;
            mAdapter.clear();
            mApi.requestGetData(getRequestSelfUrl(dynamicConditionPage, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
                @Override
                public void onResponse(SelfDynamicCondition response) {
                    if(response != null && response.getFeeds() != null && mAdapter != null) {
                        mAdapter.addFeeds(response.getFeeds());
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_dynamic_condition));
                    }
                }
            }, PersonalCenterFragment.this);
        }else {
            circlePage = 1;
            mApi.requestGetData(getRequestCircleUrl(circlePage, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null && response.getGroups() != null && mCircleAdapter != null) {
                        mCircleAdapter.clear();
                        mCircleAdapter.addGroupItem(response.getGroups());
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.empty_circle));
                    }
                }
            }, PersonalCenterFragment.this);
        }
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(mDynamicConditionButton.isChecked()) {
            if(mDynamicCondition != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(dynamicConditionPage, count, mDynamicCondition.getTotal())) {
                    if(mDynamicCondition.getTotal() > count) {
                        if (!isFootShow) {
                            mRefreshListView.getRefreshableView().addFooterView(mFootView);
                            isFootShow = true;
                        }
                    }
                    return;
                }
            }
            dynamicConditionPage = dynamicConditionPage + 1;
            mApi.requestGetData(getRequestSelfUrl(dynamicConditionPage, count), SelfDynamicCondition.class, new Response.Listener<SelfDynamicCondition>() {
                @Override
                public void onResponse(SelfDynamicCondition response) {
                    if(response != null && response.getFeeds() != null && mAdapter != null) {
                        for (Feed feed : response.getFeeds()) {
                            mAdapter.addItem(feed);
                        }
                    }
                }
            }, PersonalCenterFragment.this);
        }else {
            if(mCircleSearch != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(circlePage, count, mCircleSearch.getTotal())) {
                    if(mCircleSearch.getTotal() > count) {
                        if (!isFootShow) {
                            mRefreshListView.getRefreshableView().addFooterView(mFootView);
                            isFootShow = true;
                        }
                    }
                    return;
                }
            }
            circlePage = circlePage + 1;
            mApi.requestGetData(getRequestCircleUrl(page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null && response.getGroups() != null && mCircleAdapter != null) {
                        mCircleAdapter.addGroupItem(response.getGroups());
                    }
                }
            }, PersonalCenterFragment.this);
        }
    }

    @Override
	public void onResume() {
		super.onResume();
        userId = LoginUtil.getUserId(context);
        new AsyncTask<Void, Void, Badges>(){
            @Override
            protected Badges doInBackground(Void... params) {
                SynchronizationHttpRequest<Badges> request = RequestUtils.getInstance().createGet(context, getBadgesCount(), null);
                request.setClazz(Badges.class);
                return request.getResponse();
            }

            @Override
            protected void onPostExecute(Badges badges) {
                super.onPostExecute(badges);
                if (badges != null) {
                    Map<String, Integer> badgesCounts = badges.getBages();
                    if (badgesCounts != null) {
                        long msgCount = badgesCounts.get("msg");
                        long letterCount = badgesCounts.get("ppl");
                        if (msgCount>0 || letterCount > 0) {
                            badgeView.setVisibility(View.VISIBLE);
                        } else {
                            badgeView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }.execute();
//        long msgCount = mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, "msgCount");
//        long letterCount = mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, "letterCount");
//        if (msgCount > 0 || letterCount > 0) {
//            badgeView.setVisibility(View.VISIBLE);
//        } else {
//            badgeView.setVisibility(View.GONE);
//        }
        mApi.requestGetData(getRequestUrl(userId), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {

            @Override
            public void onResponse(PersonCenterInfo response) {
                setOnLaodingGone();
                if (response != null) {
                    mCenterInfo = response;
                    setListHeaderContent(response);
                }
            }
        }, PersonalCenterFragment.this);
	}

    View.OnClickListener mGralleryEmptyClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(context, PhotoGralleryListActivity.class);
			startActivityForResult(mIntent, DELETE_PHOTO_RESULT);
		}
	};
	
	/**
	 * 设置点击事件
	 */
	View.OnClickListener mSettingClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            MobclickAgent.onEvent(context, "user_setup");
			Intent mIntent = new Intent(context, SettingActivity.class);
			mIntent.putExtra("userId", userId);
			if(mCenterInfo != null) {
				mIntent.putExtra("mobile", mCenterInfo.getMobile());
			}
			startActivity(mIntent);
		}
	};

    View.OnClickListener mMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(context, "user_message");
            Intent mIntent = new Intent(context, MessageActivity.class);
            startActivity(mIntent);
        }
    };
	
	/**
	 * 相册item点击事件
	 */
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
            MobclickAgent.onEvent(context, "user_photo");
			Intent mIntent = new Intent(context, PhotoGralleryListActivity.class);
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
			startActivity(mIntent);
		}
	};

	/**
	 * 请求个人资料url
	 * @param id 用户id
	 * @return
	 */
	private String getRequestUrl(long id) {
		return DOMAIN + "user/self.json" + "?id=" + id;
	}
	
	/**
	 * 查看自己动态url
	 * @return
	 */
	private String getRequestSelfUrl(long page, long count) {
		return DOMAIN + "feed/self_v1.json" + "?p=" + page + "&cnt=" + count;
	}
	
	private String getRequestCircleUrl(long page, long count) {
		return DOMAIN + "user/self_create_group.json" + "?p=" + page + "&cnt=" + count;
	}

    private String getBadgesCount() {
        return "system/badges.json";
    }

}
