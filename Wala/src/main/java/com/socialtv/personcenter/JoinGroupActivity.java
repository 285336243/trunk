package com.socialtv.personcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.MultiListActivity;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;

import com.socialtv.home.entity.Entry;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.JoinResponse;
import com.socialtv.program.ProgramActivity;
import com.socialtv.star.StarActivity;
import com.socialtv.util.IConstant;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * 加入的组
 */
public class JoinGroupActivity extends MultiListActivity<JoinResponse> {
    @Inject
    private PersonalUrlService service;

    private boolean isShowRecommend = false;

    @Inject
    private JoinGrounpAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("加入的组");
        actionBar.setDisplayHomeAsUpEnabled(true);
        getListView().setDividerHeight(Utils.dip2px(this, 1));
    }

    @Override
    protected MultiTypeAdapter createAdapter() {
        return adapter;
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    @Override
    protected View adapterHeaderView() {
        return null;
    }

    @Override
    protected Request<JoinResponse> createRequest() {
        return service.createJoinGroupRequest();
    }

    @Override
    protected int getContentView() {
        return R.layout.peraon_join_group_listlayout;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.follow_erro;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.follow_loading;
    }

    @Override
    public void onLoadFinished(Loader<JoinResponse> loader, JoinResponse data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                if (data.getEntries() != null && !data.getEntries().isEmpty()) {
                    if (isShowRecommend) {
                        adapter.clear();
                        isShowRecommend = false;
                    }
                    if (isRefresh) {
                        adapter.clear();
                        isRefresh = false;
                    }
                    List<Entry> entries = data.getEntries();
                    this.items = entries;
                    adapter.setItems(data.getEntries(), "join");
                    if (entries.size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    loadingIndicator.setVisible(false);
                    if (adapter.getCount() <= 0) {
                        showRecommend();
                        isShowRecommend = true;
                    }
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
    }

    private void showRecommend() {
        //明星推荐
        new AbstractRoboAsyncTask<JoinResponse>(this){
            @Override
            protected JoinResponse run(Object data) throws Exception {
                return (JoinResponse) HttpUtils.doRequest(service.createCelebrityRecommendRequest()).result;
            }

            @Override
            protected void onSuccess(JoinResponse joinResponse) throws Exception {
                super.onSuccess(joinResponse);
                if (joinResponse != null) {
                    refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    if (joinResponse.getResponseCode() == 0) {
                        if (joinResponse.getEntries() != null && !joinResponse.getEntries().isEmpty()) {
                            adapter.setItems(joinResponse.getEntries(), "recommend");
                        }
                    }
                }
            }
        }.execute();

        //节目组推荐
        new AbstractRoboAsyncTask<JoinResponse>(this){
            @Override
            protected JoinResponse run(Object data) throws Exception {
                return (JoinResponse) HttpUtils.doRequest(service.createGroupRecommendRequest()).result;
            }

            @Override
            protected void onSuccess(JoinResponse joinResponse) throws Exception {
                super.onSuccess(joinResponse);
                if (joinResponse != null) {
                    if (joinResponse.getResponseCode() == 0) {
                        if (joinResponse.getEntries() != null && !joinResponse.getEntries().isEmpty()) {
                            adapter.setItems(joinResponse.getEntries(), "recommend");
                        }
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Entry entry = (Entry) l.getItemAtPosition(position);
        if (entry != null) {
            if (entry.getType().equals("CELEBRITY")) {
                startActivityForResult(new Intent(this, StarActivity.class).putExtra(IConstant.STAR_ID, String.valueOf(entry.getRefer().getId())), 1);
            }
            if (entry.getType().equals("GROUP")) {
                startActivityForResult(new Intent(this, ProgramActivity.class).putExtra(IConstant.PROGRAM_ID, String.valueOf(entry.getRefer().getId())), 1);
            }
        }
    }

    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isRefresh = true;
            hasMore = false;
            requestPage = 1;
            adapter.setAddRecommend(false);
            refresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
