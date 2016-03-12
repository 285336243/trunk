package com.mzs.guaji.topic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.android.volley.GsonUtils;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.MultiTypeRefreshListFragment;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.topic.entity.DynamicToic;
import com.mzs.guaji.topic.entity.Feed;
import com.mzs.guaji.topic.entity.TopicAction;
import com.mzs.guaji.topic.entity.TopicPost;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.IConstant;

/**
 * Created by wlanjie on 14-5-20.
 */
public class DynamicFragment extends MultiTypeRefreshListFragment<DynamicToic> {

    @Inject TopicService service;

    private boolean hasMore = false;

    private Gson gson;

    /**
     * Create ResourcePager
     *
     * @return
     */
    @Override
    protected ResourcePager<DynamicToic> createPager() {
        return new ResourcePager<DynamicToic>() {
            @Override
            protected Object getId(DynamicToic resource) {
                return null;
            }

            @Override
            public PageIterator<DynamicToic> createIterator(int page, int count) {
                return service.pageDynamic(requestPage, requestCount);
            }
        };
    }

    BroadcastReceiver deleteTopicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(BroadcastActionUtil.DELETE_TOPIC)) {
                forceRefresh();
            }
        }
    };

    @Override
    public void onLoadFinished(Loader<DynamicToic> listLoader, DynamicToic es) {
        super.onLoadFinished(listLoader, es);
        refreshListView.onRefreshComplete();
        if (es != null) {
            if (es.getFeeds() != null && es.getFeeds().size() > 0) {
                this.items = es.getFeeds();
                DynamicAdapter adapter = (DynamicAdapter) getListAdapter().getWrappedAdapter();
                for (Feed feed : es.getFeeds()) {
                    adapter.addItem(feed);
                }
                if (es.getFeeds().size() < 20) {
                    loadingIndicator.setVisible(false);
                }
            } else {
                hasMore = true;
                if (!this.items.isEmpty())
                    loadingIndicator.loadingAllFinish();
                else
                    loadingIndicator.setVisible(false);
            }
        }
        showList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setDivider(null);
        setEmptyText("暂无动态");
        gson = GsonUtils.createGson();
        IntentFilter filter = new IntentFilter(BroadcastActionUtil.DELETE_TOPIC);
        getActivity().registerReceiver(deleteTopicReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(deleteTopicReceiver);
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

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new DynamicAdapter(getActivity());
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.topic_dynamic_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.topic_dynamic_loading;
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore) {
            return;
        }
        if (getLoaderManager().hasRunningLoaders()) {
            return;
        }
        requestPage++;
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
        super.onRefresh(refreshView);
        requestPage = 1;
        refresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Feed feed = (Feed) l.getItemAtPosition(position);
        Intent intent = null;
        if ("TOPIC_ACTION".equals(feed.getTargetType())) {
            TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).toIntent();
        } else if ("ACTIVITY_TOPIC_ACTION".equals(feed.getTargetType())) {
            TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
        } else if ("CELEBRITY_TOPIC_ACTION".equals(feed.getTargetType())) {
            TopicAction topicAction =  gson.fromJson(feed.getTarget(), TopicAction.class);
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicAction.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
        } else if ("TOPIC_POST".equals(feed.getTargetType())) {
            TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).toIntent();
        } else if ("ACTIVITY_TOPIC_POST".equals(feed.getTargetType())) {
            TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
        } else if ("CELEBRITY_TOPIC_POST".equals(feed.getTargetType())) {
            TopicPost topicPost = gson.fromJson(feed.getTarget(), TopicPost.class);
            if ("TOPIC".equals(topicPost.getType())) {
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).toIntent();
            } else if ("CELEBRITY_TOPIC".equals(topicPost.getType())) {
                intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topicPost.getTopic().getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
            }
        }
        if (intent != null)
            startActivity(intent);
    }
}
