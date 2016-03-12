package com.mzs.guaji.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.GsonUtils;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class GuaJiFragment extends Fragment implements Response.ErrorListener, PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {
    protected static final Gson mGson  = GsonUtils.createGson();
	public ImageLoader mImageLoader;
	public DisplayImageOptions options;
	public GuaJiAPI mApi;
	public static final String DOMAIN = "http://social.api.ttq2014.com/";
	public int page = 1;
	public int count = 20;
    protected boolean isFootShow = false;
    protected boolean isLastItemVisible = false;
    protected View mRootView;
    protected PullToRefreshListView mRefreshListView;
    protected RelativeLayout mLoadingLayout;
    protected ViewStub mStub;
    protected LinearLayout mFailedLoadingLayout;
    protected TextView mFailedText;
    protected String mFailedTipsText;
    private boolean isInflate = false;
    private View v = null;
    protected CacheRepository mRepository;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(getActivity(), 0);
		mApi = GuaJiAPI.newInstance(getActivity());
        mRepository = CacheRepository.getInstance().fromContext(getActivity());
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView != null) {
            mLoadingLayout = (RelativeLayout) mRootView.findViewById(R.id.loading);
            mStub = (ViewStub) mRootView.findViewById(R.id.loading_failed);
        }
        if(mRefreshListView != null) {
            mRefreshListView.setOnRefreshListener(this);
            mRefreshListView.setOnLastItemVisibleListener(this);
        }
        onInitialization();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    View.OnClickListener mFailedLoadingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setOnFailedLayoutGone();
            onInitialization();
        }
    };

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

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Intent intent = new Intent();
		intent.setAction("com.mzs.guaji.lowmemory");
		getActivity().sendBroadcast(intent);
	}
	
	@Override
	public void onPause() {
//		mImageLoader.pause();
		super.onPause();
	}
	
	@Override
	public void onStart() {
//		mImageLoader.resume();
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
        if(mRefreshListView != null && mImageLoader != null) {
            mRefreshListView.setOnScrollListener(new PauseOnScrollListener(mImageLoader, true, true));
        }
	}
	
	@Override
	public void onStop() {
//		mImageLoader.stop();
//		mImageLoader.clearMemoryCache();
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

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
        }
        onFailed();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    @Override
    public void onLastItemVisible() {

    }
}
