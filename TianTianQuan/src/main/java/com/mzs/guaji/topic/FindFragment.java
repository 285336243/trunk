package com.mzs.guaji.topic;

import android.content.Context;
import android.content.Intent;
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
import com.mzs.guaji.topic.entity.FindTopic;
import com.mzs.guaji.topic.entity.FindTopicItem;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ToastUtil;

/**
 * Created by wlanjie on 14-5-20.
 */
public class FindFragment extends MultiTypeRefreshListFragment<FindTopic> {

    @Inject
    Context context;

    @Inject TopicService service;

    private String token = "";

    private boolean hasMore = false;

    private Gson gson;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("暂无发现");
        listView.setDivider(null);
        gson = GsonUtils.createGson();
    }

    /**
     * Create ResourcePager
     *
     * @return
     */
    @Override
    protected ResourcePager<FindTopic> createPager() {
        return new ResourcePager<FindTopic>() {
            @Override
            protected Object getId(FindTopic resource) {
                return null;
            }

            @Override
            public PageIterator<FindTopic> createIterator(int page, int count) {
                requestCount=3;
                return service.pageFind(requestPage, requestCount, token);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<FindTopic> listLoader, FindTopic data) {
        super.onLoadFinished(listLoader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                token = data.getToken();
                if (data.getTopics() != null && data.getTopics().size() > 0) {
                    this.items = data.getTopics();
                    FindAdapter adapter = (FindAdapter) getListAdapter().getWrappedAdapter();
                    for (FindTopicItem item : data.getTopics()) {
                        adapter.addItem(item);
                    }
                    if (data.getTopics().size() < 20) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty())
                        loadingIndicator.loadingAllFinish();
                    else
                        loadingIndicator.setVisible(false);
                }
            } else {
                ToastUtil.showToast(context, data.getResponseMessage());
            }
        }
        showList();
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
        return new FindAdapter(getActivity());
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.find_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.loading_find;
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        forceRefresh();
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
        forceRefresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FindTopicItem topicItem = (FindTopicItem) l.getItemAtPosition(position);
        Topic topic = gson.fromJson(topicItem.getTopic(), Topic.class);
        Intent intent = null;
        if ("TOPIC".equals(topicItem.getType())) {
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topic.getId()).toIntent();
        } else if ("ACTIVITY_TOPIC".equals(topicItem.getType())) {
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topic.getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_ACTIVITY).toIntent();
        } else if ("CELEBRITY_TOPIC".equals(topicItem.getType())) {
            intent = new Intents(getActivity(), TopicDetailsActivity.class).add(IConstant.TOPIC_ID, topic.getId()).add(IConstant.TOPIC_TYPE, IConstant.TOPIC_CELEBRITY).toIntent();
        }
        if (intent != null)
            startActivity(intent);
    }
}
