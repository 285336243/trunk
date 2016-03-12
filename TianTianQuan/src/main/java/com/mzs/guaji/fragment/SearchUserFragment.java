package com.mzs.guaji.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.SearchUserAdapter;
import com.mzs.guaji.entity.User;
import com.mzs.guaji.entity.UserSearch;
import com.mzs.guaji.ui.SearchActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.SkipPersonalCenterUtil;

import java.util.HashMap;
import java.util.Map;

public class SearchUserFragment extends GuaJiFragment implements OnLastItemVisibleListener {

	private SearchUserAdapter mAdapter;
	private SearchActivity mActivity;
	private String userSearch = DOMAIN +"user/search.json";
	private Map<String, String> headers;
	private RelativeLayout mEmptyLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (SearchActivity) getActivity();
		headers = new HashMap<String, String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_user_list, container, false);
        mRootView = v;
        mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.search_user_listview);
        super.onCreateView(inflater, container, savedInstanceState);
		mEmptyLayout = (RelativeLayout) v.findViewById(R.id.search_user_empty);
//		mRefreshListView.mOnRefreshListener = null;
//		mRefreshListView.isShowHeaderLayout(false);
        mRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        mRefreshListView.getLoadingLayoutProxy().setPullLabel("");
        mRefreshListView.getLoadingLayoutProxy().setReleaseLabel("");
		mRefreshListView.setOnLastItemVisibleListener(this);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
		return v;
	}

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User mUser = mActivity.mUserSearch.getUsers().get((int) id);
            SkipPersonalCenterUtil.startPersonalCore(mActivity, mUser.getUserId(), mUser.getRenderTo());
        }
    };

    @Override
    protected void onInitialization() {
        super.onInitialization();
        if(mActivity != null) {
            setOnLaodingGone();
            if(mActivity.mUserSearch != null) {
                if(mActivity.mUserSearch.getUsers() != null && mActivity.mUserSearch.getUsers().size() > 0) {
                    mAdapter = new SearchUserAdapter(getActivity(), mActivity.mUserSearch.getUsers());
                    mRefreshListView.setAdapter(mAdapter);
                }else {
//                    mEmptyLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

	public void addUserList(UserSearch mUserSearch) {
		if(mUserSearch != null && mUserSearch.getUsers() != null && mUserSearch.getUsers().size() > 0) {
			mAdapter = new SearchUserAdapter(getActivity(), mUserSearch.getUsers());
			mRefreshListView.setAdapter(mAdapter);
			mEmptyLayout.setVisibility(View.GONE);
		}else {
			if(mAdapter != null) {
				mAdapter.clear();
			}
			mEmptyLayout.setVisibility(View.VISIBLE);
		}
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mActivity.searchRequested();
	}

	@Override
	public void onLastItemVisible() {
		String key = mActivity.mAutoEdit.getText().toString();
        if(isLastItemVisible) {
            if (!isFootShow) {
                View v = View.inflate(getActivity(), R.layout.list_foot_layout, null);
                mRefreshListView.getRefreshableView().addFooterView(v);
                isFootShow = true;
            }
            return;
        }
        page = page + 1;
		headers.put("key", key);
		headers.put("p", page+"");
		headers.put("cnt", count+"");
		mApi.requestPostData(userSearch, UserSearch.class, headers, new Response.Listener<UserSearch>() {
			@Override
			public void onResponse(UserSearch response) {
				if(mAdapter != null && response != null && response.getUsers() != null) {
					mAdapter.addUserItem(response.getUsers());
                    if (ListViewLastItemVisibleUtil.isLastItemVisible(page, count, response.getTotal())) {
//                        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        isLastItemVisible = true;
                    }
				}
			}
		}, this);
	}
}
