package com.shengzhish.xyj.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.ActivityItem;
import com.shengzhish.xyj.core.FragmentProvider;
import com.shengzhish.xyj.core.MultiListActivity;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.NetWorkConnect;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityDetailsActivity extends MultiListActivity<ActivityDetails> {

    @Inject
    private ActivityServices services;

    private String activityId;

    @InjectView(R.id.tv_title)
    private TextView titleVieww;

    private TextView titleText;

    private TextView subTitleText;

    private TextView timeText;

    private boolean dialogIsShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityId = getStringExtra(IConstant.ACTIVITY_ID);
        if (TextUtils.isEmpty(activityId)) {
            activityId = getSharedPreferences(IConstant.ALARM_CLOCK, Context.MODE_PRIVATE).getString(IConstant.ACTIVITY_ID, "");
        }
        refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        titleVieww.setText("活动");
        setEmptyText(R.string.activity_details_empty);
        getListView().setDivider(null);
        getListView().setDividerHeight(Utils.dip2px(this, -11));
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<ActivityDetails> createRequest() {
        return services.createActivityDetailRequest(activityId);
    }

    @Override
    public void onLoadFinished(Loader<ActivityDetails> loader, ActivityDetails data) {
        super.onLoadFinished(loader, data);
        if (!NetWorkConnect.isConnect(this)) {
            Toast.makeText(this, "网络不给力", Toast.LENGTH_SHORT).show();
            return;
        }
        if (data.getActivity() != null && dialogIsShowing) {
            if (!TextUtils.isEmpty(data.getActivity().getRule())) {
                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.activity_rule_dialog);
                TextView textView = (TextView) dialog.findViewById(R.id.activity_rule_text);
                View closeView = dialog.findViewById(R.id.close_activity_rule_dialog);
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                textView.setText(data.getActivity().getRule());
                if (!dialog.isShowing()) {
                    dialog.show();
//                    dialogIsShowing = true;
                }
            }
        }

        loadingIndicator.setVisible(false);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                ActivityItem item = data.getActivity();
                if (data.getDetails() != null && data.getDetails().size() > 0) {
                    if (item != null) {
                        titleText.setText(item.getTitle());
                        subTitleText.setText(item.getSubTitle());
                        timeText.setText(item.getTime());
                    }
                    this.items = data.getDetails();
                    ActivityDetailsAdapter adapter = (ActivityDetailsAdapter) getListAdapter().getWrappedAdapter();
                    adapter.addItem(data.getDetails());
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new ActivityDetailsAdapter(this);
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        View v = getLayoutInflater().inflate(R.layout.activity_header, null);
        titleText = (TextView) v.findViewById(R.id.activity_header_title);
        subTitleText = (TextView) v.findViewById(R.id.activity_header_sub_title);
        timeText = (TextView) v.findViewById(R.id.activity_header_time);
        return v;
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.activity_details_loading_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.activity_details_loading;
    }

    /**
     * Get provider of the currently selected fragment
     *
     * @return fragment provider
     */
    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        refresh();
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
    }

    public String getActivityId() {
        return activityId;
    }
}
