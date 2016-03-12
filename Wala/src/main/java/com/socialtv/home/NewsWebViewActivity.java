package com.socialtv.home;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.SingGridActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.program.ProgramNewsAdapter;
import com.socialtv.program.entity.News;
import com.socialtv.program.entity.ProgramNews;
import com.socialtv.util.IConstant;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectExtra;

/**
 * Created by wlanjie on 14-10-17.
 */
public class NewsWebViewActivity extends SingGridActivity<ProgramNews> {

    @InjectExtra(value = IConstant.ID, optional = true)
    private String id;

    @Inject
    private ProgramNewsAdapter adapter;

    @Inject
    private HomeServices services;

    private List<News> newsItems = new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridView.setNumColumns(2);
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("资讯");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return adapter;
    }

    @Override
    protected Request<ProgramNews> createRequest() {
        return services.createNewsWebViewRequest(id, requestPage, requestCount);
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
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<android.widget.GridView> refreshView) {
        super.onRefresh(refreshView);
        newsItems.clear();
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPage = 1;
        newsItems.clear();
        newsItems = null;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.news_loading;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.news_error;
    }

    @Override
    public void onGridItemClick(android.widget.GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);
        News news = (News) g.getItemAtPosition(position);
        if (news != null) {
            if (news.getRefer() != null && !TextUtils.isEmpty(news.getRefer().getUrl()))
                startActivity(new Intents(this, WebViewActivity.class).add(IConstant.URL, news.getRefer().getUrl())
                        .add(IConstant.TITLE, news.getRefer().getTitle())
                        .add(IConstant.HIDE_TITLE, news.getRefer().getHideTitle())
                        .add(IConstant.HIDE_STATUS, news.getRefer().getHideStatus()).toIntent());
        }
    }
}
