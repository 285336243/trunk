package com.mzs.guaji.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.SearchCircleAdapter;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 点击标签搜索的页面
 * @author lenovo
 *
 */
public class SearchCircleLabelActivity extends GuaJiActivity {
	private Context context = SearchCircleLabelActivity.this;
	private String entity = "group/search_label.json";
	private String requestUrl = "";
	private PullToRefreshListView mRefreshListView;
	private Map<String, String> headers;
	private SearchCircleAdapter mAdapter;
	private CircleSearch mCircleSearch;
    private String content;

	@Override
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.search_circle_label_layout);
        mRootView = this;
        content = getIntent().getStringExtra("key");
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.search_circle_label_listview);
        super.onCreate(bundle);

		headers = new HashMap<String, String>();
//		mRefreshListView.mOnRefreshListener = null;
//		mRefreshListView.isShowHeaderLayout(false);
        mRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        mRefreshListView.getLoadingLayoutProxy().setPullLabel("");
        mRefreshListView.getLoadingLayoutProxy().setReleaseLabel("");
		TextView mTitleText = (TextView) findViewById(R.id.search_circle_label_title);
		mTitleText.setText(content);
		
		LinearLayout mCancelLayout = (LinearLayout) findViewById(R.id.search_circle_label_cancel);
		mCancelLayout.setOnClickListener(mBackClickListener);
	}

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        mRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                page = page + 1;
                if(mCircleSearch != null) {
                    if(page * count > mCircleSearch.getTotal()) {
                        ToastUtil.showToast(context, R.string.toast_last_page_tip);
                        return;
                    }
                }

                headers.put("label", content);
                headers.put("p", page+"");
                headers.put("cnt", count+"");
                mApi.requestPostData(requestUrl, CircleSearch.class, headers, new Response.Listener<CircleSearch>() {
                    @Override
                    public void onResponse(CircleSearch response) {
                        if(mAdapter != null && response != null && response.getGroups() != null) {
                            mAdapter.addGroupItem(response.getGroups());
                        }
                    }
                }, SearchCircleLabelActivity.this);
            }
        });
    }

    @Override
    protected void onInitialization() {
        super.onInitialization();
        requestUrl = DOMAIN + entity;
        headers.put("label", content);
        headers.put("p", page+"");
        headers.put("cnt", count+"");

        mApi.requestPostData(requestUrl, CircleSearch.class, headers, new Response.Listener<CircleSearch>() {
            @Override
            public void onResponse(CircleSearch response) {
                if(response != null && response.getGroups() != null && response.getGroups().size() > 0) {
                    setOnLaodingGone();
                    mCircleSearch = response;
                    mAdapter = new SearchCircleAdapter(context, response.getGroups());
                    mRefreshListView.setAdapter(mAdapter);
                }
            }
        }, this);
    }
}
