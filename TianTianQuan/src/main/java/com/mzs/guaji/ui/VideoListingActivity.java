package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.VideoListingAdapter;
import com.mzs.guaji.entity.GroupVideo;
import com.mzs.guaji.entity.VideoList;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.ToastUtil;

import java.util.List;

/**
 * 视频列表 activity
 * @author lenovo
 *
 */
public class VideoListingActivity extends GuaJiActivity {

	private Context context = VideoListingActivity.this;
	private VideoListingAdapter mAdapter;
	private long tvCircleId;
    private VideoList mVideoList;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.video_listing_layout);
        tvCircleId = getIntent().getLongExtra("id", -1);
        mRootView = this;
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.video_list);
        super.onCreate(bundle);
		LinearLayout backLayout = (LinearLayout) findViewById(R.id.video_list_back);
		backLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        mApi.requestGetData(getRequestUrl(tvCircleId, page, count), VideoList.class, new Response.Listener<VideoList>() {
            @Override
            public void onResponse(VideoList response) {
                setOnLaodingGone();
                mVideoList = response;
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        if(response.getVideos() != null && response.getVideos().size() > 0) {
                            mAdapter = new VideoListingAdapter(context, response.getVideos());
                            SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                            mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                            mRefreshListView.setAdapter(mAnimationAdapter);
                            setListViewItemClickListener(response.getVideos());
                        }else {
                            mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.videos_empty));
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, this);
    }

    /**
	 * listview item 点击事件
	 * @param mGroupVideos
	 */
	private void setListViewItemClickListener(final List<GroupVideo> mGroupVideos) {
		mRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				GroupVideo mGroupVideo = mGroupVideos.get((int) id);
				if("WEB_VIEW".equals(mGroupVideo.getOpenType())) {
					Intent mIntent = new Intent(context, WebPlayerVideoActivity.class);
					mIntent.putExtra("playerUrl", mGroupVideo.getVideoUrl());
					startActivity(mIntent);
				}else {
					Intent mIntent = new Intent(Intent.ACTION_VIEW);
					mIntent.setDataAndType(Uri.parse(mGroupVideo.getVideoUrl()), "video/*");
					startActivity(mIntent);
				}
 			}
		});
	}
	
	/**
	 * 组拼URL
	 * @param gid 电视圈ID
	 * @param page 请求页数
	 * @param count 请求数量
	 * @return 请求的URL
	 */
	private String getRequestUrl(long gid, long page, long count) {
		return DOMAIN + "group/videos_list.json" + "?gid="+ gid + "&p=" + page +"&cnt=" + count + "&platform=ANDROID";
	}

	/**
	 * 刷新
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
		page = 1;
		mApi.requestGetData(getRequestUrl(tvCircleId, page, count), VideoList.class, new Response.Listener<VideoList>() {
			@Override
			public void onResponse(VideoList response) {
            if(mAdapter != null && response != null && response.getVideos() != null) {
                mAdapter.clear();
                mAdapter.addGroupVideoItem(response.getVideos());
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
        if(mVideoList != null) {
            if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mVideoList.getTotal())) {
                if(mVideoList.getTotal() > count) {
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
		mApi.requestGetData(getRequestUrl(tvCircleId, page, count), VideoList.class, new Response.Listener<VideoList>() {
			@Override
			public void onResponse(VideoList response) {
				if(mAdapter != null && response != null && response.getVideos() != null) {
					mAdapter.addGroupVideoItem(response.getVideos());
				}
			}
		}, this);
	}
}
