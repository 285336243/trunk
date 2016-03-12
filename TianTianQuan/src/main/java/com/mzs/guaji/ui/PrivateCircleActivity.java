package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.PrivateCircleAdapter;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.entity.PrivateCircleDetail;
import com.mzs.guaji.util.ToastUtil;

/**
 * 私人圈子 activity
 * @author lenovo
 *
 */
public class PrivateCircleActivity extends GuaJiActivity {

	private Context context = PrivateCircleActivity.this;
	private PullToRefreshListView mRefreshListView;
	private PrivateCircleAdapter mAdapter;
	private PrivateCircleDetail mCircleDetail;
    private ImageView mHeaderImage;
    private TextView mNameText;
    private TextView mMemersCountText;
    private TextView mTopicsText;
    private Button mJoinCircleButton;
	private long tvCircleId;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.private_circle_layout);
		Intent mIntent = getIntent();
		tvCircleId = mIntent.getLongExtra("id", -1);
        mRootView = this;
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.private_circle_listview);

		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.private_circle_back);
		mBackLayout.setOnClickListener(mBackClickListener);

		//listview 头部view
		final View headerView = View.inflate(context, R.layout.private_circle_header_layout, null);
		mHeaderImage = (ImageView) headerView.findViewById(R.id.private_circle_header_image);
		mNameText = (TextView) headerView.findViewById(R.id.private_circle_header_name);
		mMemersCountText = (TextView) headerView.findViewById(R.id.private_circle_header_members);
		mTopicsText = (TextView) headerView.findViewById(R.id.private_circle_header_topics);
		final RelativeLayout headerContentLayout = (RelativeLayout) headerView.findViewById(R.id.private_circle_header_content);
		headerContentLayout.setOnClickListener(headerContentListener);
		final Button mCircleMemberButton = (Button) headerView.findViewById(R.id.private_circle_header_circle_member);
		mCircleMemberButton.setOnClickListener(mCircleMemberListener);
		mJoinCircleButton = (Button) headerView.findViewById(R.id.private_circle_header_join_circle);
		mJoinCircleButton.setOnClickListener(mJoinCircleListener);
		final Button mPublishToPicButton = (Button) headerView.findViewById(R.id.private_circle_header_publish_topic);
		mPublishToPicButton.setOnClickListener(mPublishToPicListener);
		mRefreshListView.getRefreshableView().addHeaderView(headerView);
        super.onCreate(bundle);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        //请求数据, 请求成功隐藏progressbar
        mApi.requestGetData(getRequestUrl(tvCircleId, page, count), PrivateCircleDetail.class, new Response.Listener<PrivateCircleDetail>() {
            @Override
            public void onResponse(PrivateCircleDetail response) {
                mLoadingLayout.setVisibility(View.GONE);
                if(response != null) {
                    if(response.getIsJoined() == 0) {
                        mJoinCircleButton.setVisibility(View.VISIBLE);
                    }else {
                        mJoinCircleButton.setVisibility(View.GONE);
                    }
                }
                if(response != null && response.getGroup() != null) {
                    Group mGroup = response.getGroup();
                    mNameText.setText(mGroup.getName());
                    mMemersCountText.setText(mGroup.getMembersCnt()+"");
                    mTopicsText.setText(mGroup.getTopicsCnt()+"");
                    mImageLoader.displayImage(mGroup.getImg(), mHeaderImage, options);
                }
                if(response != null && response.getTopics() != null && response.getTopics().size() > 0) {
                    mCircleDetail = response;
                    mAdapter = new PrivateCircleAdapter(context, response.getTopics());
                    SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                    mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                    mRefreshListView.setAdapter(mAnimationAdapter);
                }else {
                    mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.topic_empty));
                }
            }
        }, this);
    }

    /**
	 * 组拼请求URL
	 * @param id 电视圈id
	 * @param page 请求页数
	 * @param count 请求数量
	 * @return 请求的URL
	 */
	private String getRequestUrl(long id, long page, long count) {
		return DOMAIN + "group/detail.json?id=" + id +"&p=" + page + "&cnt=" + count;
	}
	
	/**
	 * 头部点击事件,进入圈子简介
	 */
	View.OnClickListener headerContentListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mCircleIntroIntent = new Intent(context, CircleIntroActivity.class);
			startActivity(mCircleIntroIntent);
		}
	};
	
	/**
	 * 圈子成员点击事件
	 */
	View.OnClickListener mCircleMemberListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	/**
	 * 加入圈子点击事件
	 */
	View.OnClickListener mJoinCircleListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};

	/**
	 * 发表话题点击事件
	 */
	View.OnClickListener mPublishToPicListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	/**
	 * 刷新
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
		page = 1;
		mApi.requestGetData(getRequestUrl(tvCircleId, page, count), PrivateCircleDetail.class, new Response.Listener<PrivateCircleDetail>() {
			@Override
			public void onResponse(PrivateCircleDetail response) {
				mRefreshListView.onRefreshComplete();
				if(mAdapter != null && response != null && response.getTopics() != null && response.getTopics().size() > 0) {
					mAdapter.clear();
					mAdapter.addToPicItem(response.getTopics());
				}
			}
		}, this);
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLastItemVisible() {
        super.onLastItemVisible();
		page = page + 1;
		if(mCircleDetail != null) {
			if(page * count > mCircleDetail.getTotal()) {
				ToastUtil.showToast(context, R.string.toast_last_page_tip);
				return;
			}
		}
		mApi.requestGetData(getRequestUrl(tvCircleId, page, count), PrivateCircleDetail.class, new Response.Listener<PrivateCircleDetail>() {
			@Override
			public void onResponse(PrivateCircleDetail response) {
				if(mAdapter != null && response != null && response.getTopics() != null) {
					mAdapter.addToPicItem(response.getTopics());
				}
			}
		}, this);
	}
}
