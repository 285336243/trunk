package com.shengzhish.xyj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.Activity;
import com.shengzhish.xyj.activity.entity.ActivityItem;
import com.shengzhish.xyj.activity.gpscheck.GpsCheckActivity;
import com.shengzhish.xyj.activity.precioushouse.PreciousActivity;
import com.shengzhish.xyj.activity.shaking.ShakeActivity;
import com.shengzhish.xyj.activity.vote.VoteActivity;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.SingleTypeRefreshListFragment;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.CacheDataKeeper;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;
import com.shengzhish.xyj.util.NetWorkConnect;

/**
 * Created by wlanjie on 14-5-30.
 */
public class ActivityFragment extends SingleTypeRefreshListFragment<Activity> {

    @Inject
    private ActivityServices services;

    private boolean hasMore = false;
    private boolean isCatchData;
    private Gson gson;
    private boolean isFirstData = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleView.setText("活动");
        setEmptyText("暂无活动");
        getListView().setDivider(null);
        getListView().setDividerHeight(Utils.dip2px(getActivity(), 4));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = Utils.dip2px(getActivity(), 4);
        params.rightMargin = Utils.dip2px(getActivity(), 4);
        getListView().setLayoutParams(params);
        getListView().setVerticalScrollBarEnabled(false);
        gson = new Gson();
        isCatchData = CacheDataKeeper.isCatchActivityResponse(getActivity());
        if (isCatchData) {
            String json = CacheDataKeeper.getActivityResponse(getActivity());
//            com.shengzhish.xyj.core.Log.v("person", "主类 ==" + json);


            Activity data = gson.fromJson(json, Activity.class);
            setDataContent(data);
        }
    }

    /**
     * Create ResourcePager
     *
     * @return
     */
    @Override
    protected Request<Activity> createRequest() {
  /*      if (isCatchData&&isFirstData)
            return null;
        else*/
        return services.createActivityRequest(getActivity(), requestPage, requestCount);
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new ActivityAdapter(getActivity());
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Activity> listLoader, Activity data) {
        super.onLoadFinished(listLoader, data);
        if (data != null && isFirstData) {
            if (CacheDataKeeper.isCatchActivityResponse(getActivity())) {
                CacheDataKeeper.removeActivityCacheData(getActivity());
            }
            String json = gson.toJson(data);
//            com.shengzhish.xyj.core.Log.v("person", "主类 ==" + json);
            CacheDataKeeper.saveActivityResponse(getActivity(), json);
        }
        if (NetWorkConnect.isConnect(getActivity()) && data != null) {
            setDataContent(data);
        }
    }

    private void setDataContent(Activity data) {
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getActivities() != null && data.getActivities().size() > 0) {
                    this.items = data.getActivities();
                    getListAdapter().getWrappedAdapter().setItems(data.getActivities());
                    if (data.getActivities().size() < requestCount)
                        loadingIndicator.setVisible(false);
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty())
                        loadingIndicator.loadingAllFinish();
                    else
                        loadingIndicator.setVisible(false);
                }
            }
        }
        showList();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        if (isCatchData)
            return IConstant.CACHE;
        else
            return R.string.activity_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.activity_loading;
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        isFirstData = false;
        refresh();
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isFirstData = true;
        refresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
/*        if (!NetWorkConnect.isConnect(getActivity())) {
            Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_LONG).show();
            return;
        }*/
        ActivityItem activityItem = (ActivityItem) l.getItemAtPosition(position);
        if ("BILI".equals(activityItem.getType())) {
            startActivity(new Intents(getActivity(), BarrageActivity.class).add(IConstant.BARRAGE_BACKGROUND, activityItem.getImg()).add(IConstant.BILI_id, activityItem.getId()).toIntent());
        } else if ("SHOW_TIME".equals(activityItem.getType())) {
            startActivity(new Intents(getActivity(), ActivityDetailsActivity.class).add(IConstant.ACTIVITY_ID, activityItem.getId()).toIntent());
        } else if ("SHOW_PIC".equals(activityItem.getType())) {
            startActivity(new Intent(getActivity(), SpecialNewsActivity.class).putExtra(IConstant.SPECIALNEWS_ID, activityItem.getId()));
        } else if ("QRCODE".equals(activityItem.getType())) {
            if (!LoginUtilSh.isLogin(getActivity())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
//                startActivity(new Intent(getActivity(), PreciousHouseActivity.class).putExtra(IConstant.ACTIVITY_ID, activityItem.getId()));
                startActivity(new Intent(getActivity(), PreciousActivity.class).putExtra(IConstant.ACTIVITY_ID, activityItem.getId()));
            }
        } else if ("SHAKE".equals(activityItem.getType())) {
            if (!LoginUtilSh.isLogin(getActivity())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ShakeActivity.class).putExtra(IConstant.ACTIVITY_ID, activityItem.getId()));
            }
        } else if ("GPS".equals(activityItem.getType())) {
            if (!LoginUtilSh.isLogin(getActivity())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(getActivity(), GpsCheckActivity.class).putExtra(IConstant.ACTIVITY_ID, activityItem.getId()).
                        putExtra(IConstant.ACTIVITY_ISJOINED, activityItem.getIsJoined()));
            }
        } else if ("VOTE".equals(activityItem.getType())) {
            if (!LoginUtilSh.isLogin(getActivity())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(getActivity(), VoteActivity.class).putExtra(IConstant.ACTIVITY_ID, activityItem.getId()));
            }
        }
    }
}
