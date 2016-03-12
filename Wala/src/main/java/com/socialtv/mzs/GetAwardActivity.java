package com.socialtv.mzs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.MultiListActivity;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.mzs.entity.GetAward;
import com.socialtv.mzs.entity.GetAwardResult;
import com.socialtv.util.IConstant;

import java.util.ArrayList;

import roboguice.inject.InjectExtra;

/**
 * Created by wlanjie on 14-9-9.
 * 领取奖励页面
 */
public class GetAwardActivity extends MultiListActivity<GetAward> {

    @Inject
    private Activity activity;

    @Inject
    private MZSService service;

    @InjectExtra(IConstant.LUCKY_DIP_ID)
    private String resultId;

    private TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("领取奖励");
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        hasMore = true;
        loadingIndicator.setVisible(false);
        listView.setDividerHeight(Utils.dip2px(activity, 4));
    }

    @Override
    protected int getContentView() {
        return R.layout.get_award;
    }

    @Override
    protected MultiTypeAdapter createAdapter() {
        return new GetAwardAdapter(this, resultId, service);
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    @Override
    protected View adapterHeaderView() {
        headerTextView = new TextView(activity);
        headerTextView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
        headerTextView.setTextColor(getResources().getColor(R.color.cambridge_blue));
        headerTextView.setTextSize(14);
        return headerTextView;
    }

    @Override
    protected Request<GetAward> createRequest() {
        return service.createGetAwardRequest(resultId);
    }

    @Override
    public void onLoadFinished(Loader<GetAward> loader, GetAward data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                this.items = data.getResult();
                headerTextView.setText(data.getTitle());
                GetAwardAdapter adapter = (GetAwardAdapter) getListAdapter().getWrappedAdapter();
                if (!data.getResult().isEmpty()) {
                    adapter.addItems(data.getResult(), "items");
                } else {
                    GetAwardResult result = new GetAwardResult();
                    result.setMsg("还没有获得任何奖励");
                    ArrayList<GetAwardResult> results = new ArrayList<GetAwardResult>();
                    results.add(result);
                    this.items = results;
                    adapter.addItems(results, "empty");
                }

            } else {
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.loading;
    }

    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
