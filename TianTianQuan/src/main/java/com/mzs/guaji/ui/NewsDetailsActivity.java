package com.mzs.guaji.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.NewsDetailsPagerAdapter;
import com.mzs.guaji.entity.GroupNews;
import com.mzs.guaji.entity.GroupNewsDetails;
import com.mzs.guaji.entity.GroupNewsList;

/**
 * 资讯详情
 * @author lenovo
 *
 */
public class NewsDetailsActivity extends GuaJiActivity {

	private Context context = NewsDetailsActivity.this;
	/**
	 * 头部图像
	 */
	private ImageView mImageView;
	/**
	 * 头部图像上的文字
	 */
	private TextView mBannerText;
	/**
	 * 内容来自
	 */
	private TextView mSourceText;
	/**
	 * 时间
	 */
	private TextView mCreateTimeText;
	/**
	 * 具体内容
	 */
	private TextView mContentText;
	private LinearLayout mPreviousLayout;
	private LinearLayout mNextLayout;
	private LinearLayout mSupportsLayout;
	
	private String listRequestUrl = DOMAIN +"group/news_list.json";
	private long id;
	private long tvcircleId;
	private int position;
	private ViewPager mViewPager;
	private List<View> mViews;
	private TextView mSupportCountText;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.news_details_layout);
		id = getIntent().getLongExtra("id", -1);
		position = getIntent().getIntExtra("position", -1);
		tvcircleId = getIntent().getLongExtra("tvcircleId", -1);
        super.onCreate(bundle);

		mViews = new ArrayList<View>();
		mViewPager = (ViewPager) findViewById(R.id.news_details_viewpager);
		mPreviousLayout = (LinearLayout) findViewById(R.id.news_details_previous_layout);
		mNextLayout = (LinearLayout) findViewById(R.id.news_details_next_layout);
		mSupportsLayout = (LinearLayout) findViewById(R.id.news_details_supports_layout);
		mSupportCountText = (TextView) findViewById(R.id.news_details_supports_count);
		
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.news_details_back);
		mBackLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        listRequestUrl = getListRequestUrl(tvcircleId, page, count);
        mApi.requestGetData(listRequestUrl, GroupNewsList.class, new Response.Listener<GroupNewsList>() {
            @Override
            public void onResponse(GroupNewsList response) {
                setOnLaodingGone();
                if(response != null && response.getNews() != null && response.getNews().size() > 0) {
                    for (int i = 0; i < response.getNews().size(); i++) {
                        View v = View.inflate(context, R.layout.news_details_viewpager_item, null);
                        mViews.add(v);
                    }

                    initView(mViews.get(position));
                    mApi.requestGetData(getRequestUrl(id), GroupNewsDetails.class, new Response.Listener<GroupNewsDetails>() {
                        @Override
                        public void onResponse(GroupNewsDetails response) {
                            if(response != null && response.getNews() != null) {
                                GroupNews mGroupNews = response.getNews();
                                mBannerText.setText(mGroupNews.getTitle());
                                mImageLoader.displayImage(mGroupNews.getImg(), mImageView, options);
                                mSourceText.setText(mGroupNews.getSource());
                                mCreateTimeText.setText(mGroupNews.getCreateTime());
                                mContentText.setText(mGroupNews.getContent());
                                mSupportCountText.setText(mGroupNews.getSupportsCnt()+"");
                            }
                        }
                    }, NewsDetailsActivity.this);

                    if(position == 0) {
                        mPreviousLayout.setVisibility(View.GONE);
                    }else {
                        mPreviousLayout.setVisibility(View.VISIBLE);
                    }

                    if(position == response.getNews().size() - 1) {
                        mNextLayout.setVisibility(View.GONE);
                    }else {
                        mNextLayout.setVisibility(View.VISIBLE);
                    }
                    NewsDetailsPagerAdapter mAdapter = new NewsDetailsPagerAdapter(mViews);
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.setOnPageChangeListener(new NewsDetailsPagerChageListener(response.getNews()));
                    mViewPager.setCurrentItem(position);
                    /**赞的点击事件*/
                    setSupportsOnClickListener();
                    /**下一篇的点击事件*/
                    setNextOnClickListener(response.getNews());
                    /**上一篇的点击事件*/
                    setPreviousOnClickListener(response.getNews());
                }
            }
        }, NewsDetailsActivity.this);
    }

    private String getListRequestUrl(long gid, long page, long count) {
		return listRequestUrl + "?gid="+gid+"&p="+page+"&cnt="+count;
	}

	private void initView(View v) {
		mImageView = (ImageView) v.findViewById(R.id.news_details_image);
		mBannerText = (TextView) v.findViewById(R.id.news_details_banner_title);
		mSourceText = (TextView) v.findViewById(R.id.news_details_source);
		mCreateTimeText = (TextView) v.findViewById(R.id.news_details_create_time);
		mContentText = (TextView) v.findViewById(R.id.news_details_content);
	}

	private void setSupportsOnClickListener() {
		mSupportsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	private void setNextOnClickListener(final List<GroupNews> mGroupNews) {
		mNextLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				position = position + 1;
				mViewPager.setCurrentItem(position);
			}
		});
	}

	private void setPreviousOnClickListener(final List<GroupNews> mGroupNews) {
		mPreviousLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				position = position - 1;
				mViewPager.setCurrentItem(position);
			}
		});
	}
	
	private void setData(final List<GroupNews> mGroupNews, int position) {
		View v = mViews.get(position);
		GroupNews mNews = mGroupNews.get(position);
		initView(v);
		mApi.requestGetData(getRequestUrl(mNews.getId()), GroupNewsDetails.class, new Response.Listener<GroupNewsDetails>() {
			@Override
			public void onResponse(GroupNewsDetails response) {
				if(response != null && response.getNews() != null) {
					GroupNews mGroupNews = response.getNews();
					mImageLoader.displayImage(mGroupNews.getImg(), mImageView, options);
					mBannerText.setText(mGroupNews.getTitle());
					mSourceText.setText(mGroupNews.getSource());
					mCreateTimeText.setText(mGroupNews.getCreateTime());
					mContentText.setText(mGroupNews.getContent());
					mSupportCountText.setText(mGroupNews.getSupportsCnt()+"");
				}
			}
		}, this);
	}
	
	private String getRequestUrl(long id) {
		return DOMAIN + "group/news.json" + "?id=" + id;
	}
	
	private class NewsDetailsPagerChageListener implements OnPageChangeListener {

		private List<GroupNews> mGroupNews;
		public NewsDetailsPagerChageListener(List<GroupNews> mGroupNews) {
			this.mGroupNews = mGroupNews;
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			if(position == 0) {
				mPreviousLayout.setVisibility(View.GONE);
			}else {
				mPreviousLayout.setVisibility(View.VISIBLE);
			}
			
			if(position == mGroupNews.size() - 1){
				mNextLayout.setVisibility(View.GONE);
			}else {
				mNextLayout.setVisibility(View.VISIBLE);
			}
			NewsDetailsActivity.this.position = position;
			setData(mGroupNews, position);
		}
		
	}
}
