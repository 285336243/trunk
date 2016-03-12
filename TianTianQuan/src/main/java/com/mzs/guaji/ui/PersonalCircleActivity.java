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
import com.mzs.guaji.adapter.SearchCircleAdapter;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;

import java.util.List;

public class PersonalCircleActivity extends GuaJiActivity {

	private Context context = PersonalCircleActivity.this;
	private SearchCircleAdapter mAdapter;
    private long userId;
    private boolean isSelf;
    private CircleSearch mSelfCircle;
    private CircleSearch mOthersCircle;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.personal_circle_layout);
        userId = getIntent().getLongExtra("userId", -1);
        isSelf = getIntent().getBooleanExtra("isSelf", true);
        mRootView = this;
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.personal_circle_listview);
        super.onCreate(bundle);
//        mRefreshListView.mOnRefreshListener = null;
//        mRefreshListView.isShowHeaderLayout(false);
        mRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        mRefreshListView.getLoadingLayoutProxy().setPullLabel("");
        mRefreshListView.getLoadingLayoutProxy().setReleaseLabel("");
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.personal_circle_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        //如果isSelf为false,为查看别的圈子,否则查看的是自己进入的圈子
        if(isSelf) {
            mApi.requestGetData(getSlefJoinCircleRequest(page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    setOnLaodingGone();
                    if(response != null && response.getGroups() != null) {
                        mAdapter = new SearchCircleAdapter(context, response.getGroups());
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.setOnItemClickListener(new PersonalCircleItemClickListener(response.getGroups()));
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.join_circle_empty));
                    }
                }
            }, this);
        }else {
            mApi.requestGetData(getOthersJoinCircleRequest(userId, page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    setOnLaodingGone();
                    if(response != null && response.getGroups() != null) {
                        mAdapter = new SearchCircleAdapter(context, response.getGroups());
                        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                        mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                        mRefreshListView.setAdapter(mAnimationAdapter);
                        mRefreshListView.setOnItemClickListener(new PersonalCircleItemClickListener(response.getGroups()));
                    }else {
                        mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.join_circle_empty));
                    }
                }
            }, this);
        }

    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(isSelf) {
            if(mSelfCircle != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mSelfCircle.getTotal())) {
                    if(mSelfCircle.getTotal() > count) {
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
            mApi.requestGetData(getSlefJoinCircleRequest(page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null && response.getGroups() != null && mAdapter != null) {
                        mAdapter.addGroupItem(response.getGroups());
                    }
                }
            }, this);
        }else {
            if(mOthersCircle != null) {
                if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mOthersCircle.getTotal())) {
                    if(mOthersCircle.getTotal() > count) {
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
            mApi.requestGetData(getOthersJoinCircleRequest(userId, page, count), CircleSearch.class, new Response.Listener<CircleSearch>() {
                @Override
                public void onResponse(CircleSearch response) {
                    if(response != null && response.getGroups() != null && mAdapter != null) {
                        mAdapter.addGroupItem(response.getGroups());
                    }
                }
            }, this);
        }
    }

    /**
     * list item 点击事件
     */
    private class PersonalCircleItemClickListener implements AdapterView.OnItemClickListener {

        private List<Group> mGroups;
        private PersonalCircleItemClickListener(List<Group> mGroups) {
            this.mGroups = mGroups;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Group mGroup = mGroups.get((int) id);
            if(mGroup != null) {
                if("OFFICIAL".equals(mGroup.getType())) {
                    //进入官方圈子
                    Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
                    mIntent.putExtra("img", mGroup.getImg());
                    mIntent.putExtra("id", mGroup.getId());
                    mIntent.putExtra("name", mGroup.getName());
                    startActivity(mIntent);
                }else if("INDIVIDUAL".equals(mGroup.getType())) {
                    //进入私人圈子
                    Intent mIntent = new Intent(context, PrivateCircleActivity.class);
                    mIntent.putExtra("id", mGroup.getId());
                    startActivity(mIntent);
                }
            }
        }
    }

    /**
     * 查看自己加入的圈子
     * @param page
     * @param count
     * @return
     */
	private String getSlefJoinCircleRequest(long page, long count) {
		return DOMAIN + "user/self_join_group.json" + "?p=" + page + "&cnt=" + count;
	}

    /**
     * 查看别人加入的圈子
     * @return
     */
    private String getOthersJoinCircleRequest(long userId, long page, long count) {
        return  DOMAIN + "user/read_join_group.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }
}
