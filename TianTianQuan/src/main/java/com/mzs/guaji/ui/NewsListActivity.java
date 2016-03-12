package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.NewsListAdapter;
import com.mzs.guaji.entity.GroupNews;
import com.mzs.guaji.entity.GroupNewsList;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;

/**
 * 资讯列表
 * @author lenovo
 *
 */
public class NewsListActivity extends GuaJiActivity {

	private Context context = NewsListActivity.this;
	private NewsListAdapter mAdapter;
	private long tvCircleId;
    private GroupNewsList mNewsList;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.news_list_layout);
		tvCircleId = getIntent().getLongExtra("id", -1);
        mRootView = this;
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.news_list);
        super.onCreate(bundle);
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.news_list_back);
		mBackLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        //请求数据
        mApi.requestGetData( getRequestUrl(tvCircleId, page, count), GroupNewsList.class, new Response.Listener<GroupNewsList>() {
            @Override
            public void onResponse(GroupNewsList response) {
                setOnLaodingGone();
                mNewsList = response;
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getNews() != null) {
                            mAdapter = new NewsListAdapter(context, response.getNews());
                            SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                            mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                            mRefreshListView.setAdapter(mAnimationAdapter);
                            setListOnItemClickListener(response);
                        }else {
                            mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.news_empty));
                        }
                    }
                }
            }
        }, this);
    }

    /**
	 * list item 点击事件,点击跳转到资讯详情  NewsDetailsActivity 
	 * 传递参数资讯ID, list view position
	 * @param mGroupNewsList
	 */
	private void setListOnItemClickListener(final GroupNewsList mGroupNewsList) {
		mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GroupNews mGroupNews = mGroupNewsList.getNews().get((int)id);
				Intent mIntent = new Intent(context, NewsDetailsActivity.class);
				mIntent.putExtra("id", mGroupNews.getId());
				mIntent.putExtra("position", (int)id);
				mIntent.putExtra("tvcircleId", tvCircleId);
				startActivity(mIntent);
			}
		});
	}
	
	/**
	 * 组拼URL
	 * @param gid 电视圈ID
	 * @param page 请求页数
	 * @param count 请求数量
	 * @return 请求URL
	 */
	private String getRequestUrl(long gid, long page, long count) {
		return DOMAIN +"group/news_list.json" + "?gid="+gid+"&p="+page+"&cnt="+count;
	}

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        page = 1;
        mApi.requestGetData(getRequestUrl(tvCircleId, page, count), GroupNewsList.class, new Response.Listener<GroupNewsList>() {
            @Override
            public void onResponse(GroupNewsList response) {
            if(mAdapter != null && response != null && response.getNews() != null && response.getNews().size() > 0) {
                mRefreshListView.onRefreshComplete();
                mAdapter.clear();
                mAdapter.addGroupNewsItem(response.getNews());
            }
            }
        }, this);
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(mNewsList != null) {
            if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mNewsList.getTotal())) {
                if(mNewsList.getTotal() > count) {
                    if (!isFootShow) {
                        View v = View.inflate(context, R.layout.list_foot_layout, null);
                        mRefreshListView.getRefreshableView().addFooterView(v);
                        isFootShow = true;
                    }
                }
                return;
            }
        }
        page = page + 1;
        mApi.requestGetData(getRequestUrl(tvCircleId, page, count), GroupNewsList.class, new Response.Listener<GroupNewsList>() {
            @Override
            public void onResponse(GroupNewsList response) {
            if(mAdapter != null && response != null && response.getNews() != null && response.getNews().size() > 0) {
                mAdapter.addMoreGroupNewsItem(response.getNews());
            }
            }
        }, this);
    }
}
