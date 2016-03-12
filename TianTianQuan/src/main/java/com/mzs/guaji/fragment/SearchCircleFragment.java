package com.mzs.guaji.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.SearchCircleAdapter;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.ui.SearchActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;

import java.util.HashMap;
import java.util.Map;

public class SearchCircleFragment extends GuaJiFragment {
	private SearchCircleAdapter mAdapter;
	private SearchActivity mActivity;
	private RelativeLayout mEmptyLayout;
	private Map<String, String> headers;
	private String circleSearch = DOMAIN + "group/search.json";

	public static SearchCircleFragment newInstance() {
		SearchCircleFragment mCircleFragment = new SearchCircleFragment();
		return mCircleFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		headers = new HashMap<String, String>();
		mActivity = (SearchActivity) getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_circle_list, container, false);
        mRootView = v;
        mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.search_circle_listview);
        super.onCreateView(inflater, container, savedInstanceState);
		mEmptyLayout = (RelativeLayout) v.findViewById(R.id.search_circle_empty);
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
        super.onInitialization();if(mActivity != null) {
            setOnLaodingGone();
            if(mActivity.mCircleSearch != null && mActivity.mCircleSearch.getGroups() != null && mActivity.mCircleSearch.getGroups().size() > 0) {
                mAdapter = new SearchCircleAdapter(getActivity(), mActivity.mCircleSearch.getGroups());
                mRefreshListView.setAdapter(mAdapter);
            }else {
//                mEmptyLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Group mGoroup = mActivity.mCircleSearch.getGroups().get((int) id);
            Intent mIntent = new Intent(mActivity, OfficialTvCircleActivity.class);
            mIntent.putExtra("id", mGoroup.getId());
            mIntent.putExtra("name", mGoroup.getName());
            mIntent.putExtra("img", mGoroup.getImg());
            mActivity.startActivity(mIntent);
        }
    };

	@Override
	public void onResume() {
		super.onResume();
		mActivity.searchRequested();
	}
	
	public void addGroupList(CircleSearch mCircleSearch) {
		if(mCircleSearch != null && mCircleSearch.getGroups() != null && mCircleSearch.getGroups().size() > 0) {
			mAdapter = new SearchCircleAdapter(getActivity(), mCircleSearch.getGroups());
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
		
		mApi.requestPostData(circleSearch, CircleSearch.class, headers, new Response.Listener<CircleSearch>() {
			@Override
			public void onResponse(CircleSearch response) {
				if(mAdapter != null && response != null && response.getGroups() != null) {
					mAdapter.addGroupItem(response.getGroups());
                    if (ListViewLastItemVisibleUtil.isLastItemVisible(page, count, response.getTotal())) {
//                        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        isLastItemVisible = true;
                    }
				}
			}
		}, this);
	}
}
