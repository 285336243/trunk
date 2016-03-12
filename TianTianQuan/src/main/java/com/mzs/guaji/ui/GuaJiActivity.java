package com.mzs.guaji.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.GuaJiApplication;
import com.mzs.guaji.R;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.util.AppManager;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 所有activity的基类
 * @author lenovo
 *
 */
public class GuaJiActivity extends FragmentActivity implements Response.ErrorListener, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener  {

	public ImageLoader mImageLoader;
	public DisplayImageOptions options;
	public GuaJiApplication application;
	public GuaJiAPI mApi;
	public static final String DOMAIN = "http://social.api.ttq2014.com/";
    public static final String DOMAINS = "https://social.api.ttq2014.com/";
	public long page = 1;
	public long count = 10;
    private Context context = GuaJiActivity.this;
    protected boolean isFootShow = false;
    protected boolean isLastItemVisible = false;
    protected CacheRepository mRepository;
    protected Activity mRootView;
    protected PullToRefreshListView mRefreshListView;
    protected RelativeLayout mLoadingLayout;
    protected ViewStub mStub;
    protected LinearLayout mFailedLoadingLayout;
    protected TextView mFailedText;
    protected String mFailedTipsText;
    private boolean isInflate = false;
    private View v = null;

    public View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(this, 0);
        AppManager.getAppManager().addActivity(this);
		mApi = GuaJiAPI.newInstance(this);
        mRepository = CacheRepository.getInstance().fromContext(context);
        if(mRootView != null) {
            mLoadingLayout = (RelativeLayout) mRootView.findViewById(R.id.loading);
            mStub = (ViewStub) mRootView.findViewById(R.id.loading_failed);
        }
        if(mRefreshListView != null) {
            mRefreshListView.setOnRefreshListener(this);
            mRefreshListView.setOnLastItemVisibleListener(this);
        }
        onInitialization();
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Intent intent = new Intent();
		intent.setAction("com.mzs.guaji.lowmemory");
		sendBroadcast(intent);
	}

    /**
     * 第一次初始化数据时调用
     */
    protected void onInitialization() {

    }

    /**
     * 数据请求错误时调用
     */
    protected void onFailed() {
        if(mLoadingLayout != null) {
            mLoadingLayout.setVisibility(View.GONE);
        }
        if(!isInflate && mStub != null) {
            v = mStub.inflate();
            mFailedLoadingLayout = (LinearLayout) v.findViewById(R.id.loading_failed_layout);
            mFailedLoadingLayout.setOnClickListener(mFailedLoadingClickListener);
            mFailedText = (TextView) v.findViewById(R.id.loading_failed_text);
            mFailedText.setText(mFailedTipsText);
            isInflate = true;
            v.setVisibility(View.VISIBLE);
        }else {
            if(v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    View.OnClickListener mFailedLoadingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setOnFailedLayoutGone();
            onInitialization();
        }
    };

    protected void setOnLaodingGone() {
        if(mLoadingLayout != null) {
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    protected void setOnFailedLayoutGone() {
        if(mFailedLoadingLayout != null) {
            mFailedLoadingLayout.setVisibility(View.GONE);
        }
    }
	
//	@Override
//	protected void onStart() {
////		mImageLoader.resume();
//		super.onStart();
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
        if(mRefreshListView != null && mImageLoader != null) {
            mRefreshListView.setOnScrollListener(new PauseOnScrollListener(mImageLoader, true, true));
        }
        MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
//		mImageLoader.pause();
		super.onPause();
        MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
//		mImageLoader.stop();
//		mImageLoader.clearMemoryCache();
        AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError) {
            mFailedTipsText = "网络超时";
        } else if (error instanceof NetworkError) {
            mFailedTipsText = "网络连接错误";
        } else if (error instanceof NoConnectionError) {
            mFailedTipsText = "没有网络连接";
        } else if (error instanceof ParseError) {
            mFailedTipsText = "解析数据错误";
        } else if (error instanceof AuthFailureError) {
            mFailedTipsText = "AuthFailureError";
        }else {
            ToastUtil.showToast(context, "未知错误");
        }
        onFailed();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    @Override
    public void onLastItemVisible() {

    }
}
