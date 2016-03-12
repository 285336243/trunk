package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.SearchToPicAdapter;
import com.mzs.guaji.entity.ToPicSearch;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;

import java.util.List;

public class PersonalTopicActivity extends GuaJiActivity {

	private Context context = PersonalTopicActivity.this;
	private SearchToPicAdapter mAdapter;
    private boolean isSelf;
    private long userId;
    private ToPicSearch mSelfTopic;
    private ToPicSearch mOthersTopic;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.personal_topic_layout);
        userId = getIntent().getLongExtra("userId", -1);
        isSelf = getIntent().getBooleanExtra("isSelf", true);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.personal_topic_listview);
        mRootView = this;
        super.onCreate(bundle);

//        mRefreshListView.mOnRefreshListener = null;
//        mRefreshListView.isShowHeaderLayout(false);
        mRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        mRefreshListView.getLoadingLayoutProxy().setPullLabel("");
        mRefreshListView.getLoadingLayoutProxy().setReleaseLabel("");
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.personal_topic_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        //如果isSelf为false,查看的是别人的话题,否则查看的是自己发布的话题
        if(isSelf) {
            mApi.requestGetData(getSelfTopicRequest(page, count), ToPicSearch.class, new Response.Listener<ToPicSearch>() {
                @Override
                public void onResponse(ToPicSearch response) {
                    setOnLaodingGone();
                    mSelfTopic = response;
                    if(response != null && response.getTopics() != null) {
                        mAdapter = new SearchToPicAdapter(context, response.getTopics());
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.setOnItemClickListener(new TopicItemClickListener(response.getTopics()));
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.personal_topic_empty));
                    }
                }
            }, this);
        }else {
            mApi.requestGetData(getOthersTopicRequest(userId, page, count), ToPicSearch.class, new Response.Listener<ToPicSearch>() {
                @Override
                public void onResponse(ToPicSearch response) {
                    setOnLaodingGone();
                    mOthersTopic = response;
                    if(response != null && response.getTopics() != null) {
                        mAdapter = new SearchToPicAdapter(context, response.getTopics());
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.setOnItemClickListener(new TopicItemClickListener(response.getTopics()));
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.personal_topic_empty));
                    }
                }
            }, this);
        }

    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(isSelf) {
            if(mSelfTopic != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mSelfTopic.getTotal())) {
                    if(mSelfTopic.getTotal() > count) {
                        if (!isFootShow) {
                            View v = View.inflate(context, R.layout.list_foot_layout, null);
                            mRefreshListView.getRefreshableView().addFooterView(v);
                            isFootShow = true;
                        }
                    }
                    return;
                }
                page = page + 1;
                mApi.requestGetData(getSelfTopicRequest(page, count), ToPicSearch.class, new Response.Listener<ToPicSearch>() {
                    @Override
                    public void onResponse(ToPicSearch response) {
                        if(response != null && response.getTopics() != null && mAdapter != null) {
                            mAdapter.addToPicItem(response.getTopics());
                        }
                    }
                }, this);
            }
        }else {
            if(mOthersTopic != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mOthersTopic.getTotal())) {
                    if(mOthersTopic.getTotal() > count) {
                        if (!isFootShow) {
                            View v = View.inflate(context, R.layout.list_foot_layout, null);
                            mRefreshListView.getRefreshableView().addFooterView(v);
                            isFootShow = true;
                        }
                    }
                    return;
                }
                page = page + 1;
                mApi.requestGetData(getSelfTopicRequest(page, count), ToPicSearch.class, new Response.Listener<ToPicSearch>() {
                    @Override
                    public void onResponse(ToPicSearch response) {
                        if(response != null && response.getTopics() != null && mAdapter != null) {
                            mAdapter.addToPicItem(response.getTopics());
                        }
                    }
                }, this);
            }
        }
    }

    /**
     * list item 点击事件
     */
    private class TopicItemClickListener implements AdapterView.OnItemClickListener {

        private List<Topic> mTopic;
        public TopicItemClickListener(List<Topic> mTopics) {
            this.mTopic = mTopics;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent mIntent = new Intent(context, TopicDetailsActivity.class);
            mIntent.putExtra("topicId", mTopic.get((int) id).getId());
            startActivity(mIntent);
        }
    }

    /**
     * 查看自己发布的话题
     * @param page
     * @param count
     * @return
     */
	private String getSelfTopicRequest(long page , long count) {
		return DOMAIN + "user/self_topic.json" + "?p=" + page + "&cnt=" + count;
	}

    /**
     * 查看别人发布的话题
     * @param userId 用户ID
     * @param page 当前页数
     * @param count 请求数量
     * @return
     */
    private String getOthersTopicRequest(long userId, long page, long count) {
        return DOMAIN + "user/read_topic.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }
}
