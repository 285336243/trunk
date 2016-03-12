package com.mzs.guaji.fragment;

import android.content.Intent;
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
import com.mzs.guaji.adapter.SearchToPicAdapter;
import com.mzs.guaji.entity.ToPicSearch;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.ui.SearchActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;

import java.util.HashMap;
import java.util.Map;

public class SearchToPicFragment extends GuaJiFragment implements OnLastItemVisibleListener {
	private SearchToPicAdapter mAdapter;
	private SearchActivity mActivity;
	private RelativeLayout mEmptyLayout;
	private Map<String, String> headers;
	private String topicSearch = DOMAIN+"topic/search.json";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity = (SearchActivity) getActivity();
		headers = new HashMap<String, String>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_topic_list, null);
        mRootView = v;
        mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.search_topic_listview);
        super.onCreateView(inflater, container, savedInstanceState);
		mEmptyLayout = (RelativeLayout) v.findViewById(R.id.search_topic_empty);
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
            Intent mIntent = new Intent(getActivity(), TopicDetailsActivity.class);
            if(mActivity.mToPicSearch != null && mActivity.mToPicSearch.getTopics() != null) {
                Topic mTopic = mActivity.mToPicSearch.getTopics().get((int) id);
                if(mTopic != null) {
                    mIntent.putExtra("topicId", mTopic.getId());
                }
            }
            startActivity(mIntent);
        }
    };

    @Override
    protected void onInitialization() {
        super.onInitialization();
        if(mActivity != null) {
            setOnLaodingGone();
            if(mActivity.mToPicSearch != null && mActivity.mToPicSearch.getTopics() != null && mActivity.mToPicSearch.getTopics().size() > 0) {
                mAdapter = new SearchToPicAdapter(getActivity(), mActivity.mToPicSearch.getTopics());
                mRefreshListView.setAdapter(mAdapter);
            }else {
//                mEmptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		mActivity.searchRequested();
	}

	public void addToPicList(ToPicSearch mToPicSearch) {
		if(mToPicSearch != null && mToPicSearch.getTopics() != null && mToPicSearch.getTopics().size() > 0) {
			mAdapter = new SearchToPicAdapter(getActivity(), mToPicSearch.getTopics());
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

		mApi.requestPostData(topicSearch, ToPicSearch.class, headers, new Response.Listener<ToPicSearch>() {
			@Override
			public void onResponse(ToPicSearch response) {
				if(mAdapter != null && response != null && response.getTopics() != null) {
					mAdapter.addToPicItem(response.getTopics());
                    if (ListViewLastItemVisibleUtil.isLastItemVisible(page, count, response.getTotal())) {
//                        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        isLastItemVisible = true;
                    }
				}
			}
		}, this);
	}
}
