package com.mzs.guaji.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.PersonalFansAdapter;
import com.mzs.guaji.entity.FollowAndFands;
import com.mzs.guaji.entity.User;
import com.mzs.guaji.ui.FollowAndFansActivity;
import com.mzs.guaji.ui.OthersInformationActivity;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.util.ToastUtil;

import java.util.List;

/**
 * 查看粉丝
 */
public class PersonalFansFragment extends GuaJiFragment {

	private PersonalFansAdapter mAdapter;
	private LinearLayout mRootLayout;
	private FollowAndFansActivity mActivity;
    private boolean isSelf = true;
    private long userId = -1;
    private FansBroadcastReceiver mBroadcast;
    private FollowAndFands mSelfFands;
    private FollowAndFands mOthersFands;

    public static PersonalFansFragment newInstance(boolean isSelf, long userId) {
        PersonalFansFragment mFragment = new PersonalFansFragment();
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("isSelf", isSelf);
        mBundle.putLong("userId", userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        if(mBundle != null) {
            isSelf = mBundle.getBoolean("isSelf", true);
            userId = mBundle.getLong("userId", -1);
        }
        mBroadcast = new FansBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastActionUtil.REFRESHFANSACTION);
        getActivity().registerReceiver(mBroadcast, mFilter);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.personal_follow_layout, null);
        mRootView = v;
        mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.personal_attention_listview);
        super.onCreateView(inflater, container, savedInstanceState);

		mActivity = (FollowAndFansActivity) getActivity();
		mRootLayout = (LinearLayout) v.findViewById(R.id.follow_root_layout);
//		mRefreshListView.mOnRefreshListener = null;
//		mRefreshListView.isShowHeaderLayout(false);
        mRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        mRefreshListView.getLoadingLayoutProxy().setPullLabel("");
        mRefreshListView.getLoadingLayoutProxy().setReleaseLabel("");
        mRefreshListView.setOnLastItemVisibleListener(this);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
		return v;
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();//根据isSelf判断进入查看谁的粉丝,如果isSelf为false,根据userId查看别人的粉丝,否则查看自己的粉丝
        if(isSelf) {
            mApi.requestGetData(getFansRequest(page, count), FollowAndFands.class, new Response.Listener<FollowAndFands>() {
                @Override
                public void onResponse(FollowAndFands response) {
                    setOnLaodingGone();
                    mSelfFands = response;
                    if(response != null && response.getUsers() != null) {
                        mActivity.mFansButton.setText(String.format(getResources().getString(R.string.fans_count), response.getTotal()));
                        mAdapter = new PersonalFansAdapter(getActivity(), response.getUsers(), mRootLayout);
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.getRefreshableView().setTag(response.getUsers());
                        mRefreshListView.setOnItemClickListener(mItemClickListener);
                    }else {
                        mActivity.mFansButton.setText(String.format(getResources().getString(R.string.fans_count), 0));
                        mRefreshListView.setAdapter(new EmptyAdapter(getActivity(), R.string.fans_empty));
                    }
                }
            }, this);
        }else {
            mApi.requestGetData(getOthersFansRequest(userId, page, count), FollowAndFands.class, new Response.Listener<FollowAndFands>() {
                @Override
                public void onResponse(FollowAndFands response) {
                    setOnLaodingGone();
                    mOthersFands = response;
                    if(response != null && response.getUsers() != null) {
                        mActivity.mFansButton.setText(String.format(getResources().getString(R.string.fans_count), response.getTotal()));
                        mAdapter = new PersonalFansAdapter(getActivity(), response.getUsers(), mRootLayout);
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.getRefreshableView().setTag(response.getUsers());
                        mRefreshListView.setOnItemClickListener(mItemClickListener);
                    }else {
                        mActivity.mFansButton.setText(String.format(getResources().getString(R.string.fans_count), 0));
                        mRefreshListView.setAdapter(new EmptyAdapter(getActivity(), R.string.fans_empty));
                    }
                }
            },this);
        }

    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(isSelf) {
            if(mSelfFands != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mSelfFands.getTotal())) {
                    if(mSelfFands.getTotal() > count) {
                        if (!isFootShow) {
                            View v = View.inflate(getActivity(), R.layout.list_foot_layout, null);
                            mRefreshListView.getRefreshableView().addFooterView(v);
                            isFootShow = true;
                        }
                    }
                    return;
                }
            }
            page = page + 1;
            mApi.requestGetData(getFansRequest(page, count), FollowAndFands.class, new Response.Listener<FollowAndFands>() {
                @Override
                public void onResponse(FollowAndFands response) {
                    if(response != null && response.getUsers() != null && mAdapter != null) {
                        mAdapter.addUser(response.getUsers());
                    }
                }
            }, this);
        }else {
            if(mOthersFands != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mOthersFands.getTotal())) {
                    if(mOthersFands.getTotal() > count) {
                        if (!isFootShow) {
                            View v = View.inflate(getActivity(), R.layout.list_foot_layout, null);
                            mRefreshListView.getRefreshableView().addFooterView(v);
                            isFootShow = true;
                        }
                    }
                    return;
                }
            }
            page = page + 1;
            mApi.requestGetData(getOthersFansRequest(userId, page, count), FollowAndFands.class, new Response.Listener<FollowAndFands>() {
                @Override
                public void onResponse(FollowAndFands response) {
                    if(response != null && response.getUsers() != null && mAdapter != null) {
                        mAdapter.addUser(response.getUsers());
                    }
                }
            }, this);
        }
    }

    /**
     * 粉丝list item点击事件
     */
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            List<User> mUsers = (List<User>) adapterView.getTag();
            if(LoginUtil.getUserId(getActivity()) != mUsers.get((int) id).getUserId()) {
                User user = mUsers.get((int) id);
                SkipPersonalCenterUtil.startPersonalCore(getActivity(), user.getUserId(), user.getRenderTo());
            }else {
                ToastUtil.showToast(getActivity(), "这里不能查看自己的资料");
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBroadcast);
    }


    /**
     * 查看自己的粉丝
     * @param page
     * @param count
     * @return
     */
	private String getFansRequest(long page , long count) {
		return DOMAIN + "user/self_fans.json" + "?p=" + page + "&cnt=" + count;
	}

    /**
     * 查看别人的粉丝
     * @param userId
     * @param page
     * @param count
     * @return
     */
    private String getOthersFansRequest(long userId, long page, long count) {
        return DOMAIN + "user/read_fans.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }
	
    public class FansBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction().equals(BroadcastActionUtil.REFRESHFANSACTION)) {
                mApi.requestGetData(getFansRequest(1, count), FollowAndFands.class, new Response.Listener<FollowAndFands>() {
                    @Override
                    public void onResponse(FollowAndFands response) {
                        if(response != null && response.getUsers() != null) {
                            mAdapter = new PersonalFansAdapter(getActivity(), response.getUsers(), mRootLayout);
                            mRefreshListView.setAdapter(mAdapter);
                            mActivity.mFollowButton.setText(String.format(getResources().getString(R.string.follow_count), response.getTotal()));
                        }else {
                            mActivity.mFollowButton.setText(String.format(getResources().getString(R.string.follow_count), 0));
                            mRefreshListView.setAdapter(new EmptyAdapter(getActivity(), R.string.follow_empty));
                        }
                    }

                }, PersonalFansFragment.this);
            }
        }

    }
}
