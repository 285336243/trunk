package com.socialtv.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiGridActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.program.entity.News;
import com.socialtv.program.entity.ProgramNews;
import com.socialtv.util.IConstant;

import roboguice.inject.InjectExtra;

/**
 * Created by wlanjie on 14-9-19.
 * 新闻的页面
 */
public class NewsActivity extends MultiGridActivity<ProgramNews> {

    @Inject
    private HomeServices services;

    @Inject
    private NewsActivityAdapter adapter;

    @Inject
    private Activity activity;

    @InjectExtra(value = IConstant.TYPE, optional = false)
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridView.setNumColumns(2);
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if ("NEWS".equals(type)) {
            getSupportActionBar().setTitle("新闻");
        } else if ("VIDEO".equals(type)) {
            getSupportActionBar().setTitle("视频");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected MultiTypeAdapter createAdapter() {
        return adapter;
    }

    @Override
    protected Request<ProgramNews> createRequest() {
        return services.createNewsRequest(type, requestPage, requestCount);
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
    public void onLoadFinished(Loader<ProgramNews> loader, ProgramNews data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getNewses() != null && !data.getNewses().isEmpty()) {
                    if (isRefresh) {
                        adapter.clear();
                        isRefresh = false;
                    }
                    this.items = data.getNewses();
                    adapter.addItem(data.getNewses());
                } else {
                    hasMore = true;
                }
            } else {
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    public void onGridItemClick(android.widget.GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);
        News news = (News) g.getItemAtPosition(position);
        if (news != null) {
            if (news.getRefer() != null && !TextUtils.isEmpty(news.getRefer().getUrl())) {
                if (news.getRefer().getUrl().endsWith(".mp4")) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(news.getRefer().getUrl());
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                    mediaIntent.setDataAndType(Uri.parse(news.getRefer().getUrl()), mimeType);
                    startActivity(mediaIntent);
                } else {
                    activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, news.getRefer().getUrl())
                            .add(IConstant.TITLE, news.getRefer().getTitle())
                            .add(IConstant.HIDE_TITLE, news.getRefer().getHideTitle())
                            .add(IConstant.HIDE_STATUS, news.getRefer().getHideStatus()).toIntent());
                }
            }
        }
    }
}
