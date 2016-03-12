package com.socialtv.program;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.PullToRefreshItemGridFragment;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.home.WebViewActivity;
import com.socialtv.program.entity.News;
import com.socialtv.program.entity.ProgramNews;
import com.socialtv.util.IConstant;
import com.socialtv.view.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14-7-25.
 * 节目组中新闻的Fragment
 */
public class ProgramNewsFragment extends PullToRefreshItemGridFragment<ProgramNews> {

    private String programId;

    private BannerView bannerView;

    @Inject
    private ProgramServices services;

    @Inject
    private ProgramNewsAdapter adapter;

    private final List<News> newsItems = new ArrayList<News>();

    private View emptyView;

    public void setBannerView(final BannerView bannerView) {
        this.bannerView = bannerView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            programId = bundle.getString(IConstant.PROGRAM_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        setEmptyText("暂无资讯");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (bannerView != null && ProgramNewsFragment.this.getUserVisibleHint()) {
                    int mMinHeaderTranslation = -bannerView.getMeasuredHeight() + Utils.dip2px(getActivity(), 52);
                    int y = getScrollY();
                    bannerView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
                }
            }
        });
    }

    private int getScrollY() {
        View v = gridView.getChildAt(0);
        if (v == null)
            return 0;
        int positon = gridView.getFirstVisiblePosition();
        int top = v.getTop();
        int y = 0;
        if (positon >= 1) {
            y = bannerView.getHeight();
        }
        return y + (-top + (positon) * v.getHeight());
    }

    @TargetApi(11)
    public void scrollView(final int y) {
        if (gridView != null && bannerView != null){
            int maxHeight = gridView.getMeasuredHeight();
            int showHeight = 0;
            int firstPosition = gridView.getFirstVisiblePosition();
            int lastPostion = gridView.getLastVisiblePosition();
            int count = lastPostion-firstPosition+1;
            for(int i=0;i<count;i=i+2){
                View v = gridView.getChildAt(i);
                if(v != null){
                    showHeight +=v.getMeasuredHeight();
                }
            }
            if(adapter.isEmpty()){
                bannerView.setTranslationValue(0, y);
            }else if(showHeight>=maxHeight) {
                bannerView.setTranslationValue(0, y);
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    gridView.smoothScrollBy(-y, 0);
                } else {
                    gridView.smoothScrollToPositionFromTop(0, y);
                }
            }else{
                bannerView.setTranslationValue(0,0);
            }
        }

    }

    @TargetApi(11)
    public void refreshList() {
        hasMore = false;
        requestPage = 1;
        newsItems.clear();
        if (bannerView != null)
            bannerView.setTranslationValue(0, 0);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            gridView.setSelection(0);
        } else {
            gridView.smoothScrollToPositionFromTop(0, 0);
        }
        refresh();
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    @Override
    protected View adapterHeaderView() {
        emptyView = new View(getActivity());
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        emptyView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, metrics.widthPixels / 2 + Utils.dip2px(getActivity(), 52)));
        emptyView.setBackgroundColor(getResources().getColor(android.R.color.white));
        return emptyView;
    }

    @Override
    public void onDestroyView() {
        gridView.removeHeaderView(emptyView);
        newsItems.clear();
        super.onDestroyView();
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<ProgramNews> createRequest() {
        return services.createNewsRequest(programId, requestPage, requestCount);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.news_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.news_loading;
    }

    @Override
    public void onLoadFinished(Loader<ProgramNews> loader, ProgramNews data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getNewses() != null && !data.getNewses().isEmpty()) {
                    this.items = data.getNewses();
                    newsItems.addAll(data.getNewses());
                    adapter.setItems(newsItems);
                } else {
                    hasMore = true;
                }

            } else {
                ToastUtils.show(getActivity(), data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return adapter;
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);
        News news = (News) g.getItemAtPosition(position);
        if (news != null) {
            if (news.getRefer() != null && !TextUtils.isEmpty(news.getRefer().getUrl()))
                activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, news.getRefer().getUrl())
                        .add(IConstant.TITLE, news.getRefer().getTitle())
                        .add(IConstant.HIDE_TITLE, news.getRefer().getHideTitle())
                        .add(IConstant.HIDE_STATUS, news.getRefer().getHideStatus()).toIntent());
        }
    }
}
