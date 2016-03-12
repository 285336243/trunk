package com.socialtv.feed;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.feed.entity.Feed;
import com.socialtv.publicentity.Topic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wlanjie on 14/10/22.
 *
 * 动态中热门的Fragment
 */
public class FeedHotFragment extends FeedItemFragment {

    private FeedAdapter adapter;

    private String cusorId = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FeedAdapter(this);
        topics = new ArrayList<Topic>();
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return adapter;
    }

    @Override
    protected Request<Feed> createRequest() {
        return services.createHotRequest(requestCount, cusorId);
    }

    @Override
    public void refreshList(String topicId) {
        Iterator<Topic> iterator = topics.iterator();
        while (iterator.hasNext()) {
            Topic topic = iterator.next();
            if (topicId.equals(topic.getId())) {
                iterator.remove();
            }
        }
        if (!topics.isEmpty()) {
            adapter.setItems(topics, "topics");
        } else {
            adapter.setItems(topics, "empty");
        }
    }

    @Override
    public void onLoadFinished(Loader<Feed> loader, Feed data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                cusorId = data.getCusorId();

                //下拉刷新的时候在这里清除老的数据 是为了不出现一段时间的空白期
                if (isRefresh) {
                    topics.clear();

                    //点击刷新按钮的时候,要回退到第一条数据
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(0);
                        }
                    });
                }
                if (data.getList() != null && !data.getList().isEmpty()) {
//                    topics.clear();
                    if (data.getList().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                    topics.addAll(data.getList());
                    adapter.setItems(topics, "topics");
                } else {
                    hasMore = true;
                    if (!topics.isEmpty()) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        //数据为空
                        loadingIndicator.setVisible(false);
                        Topic topic = new Topic();
                        topic.setMessage("暂无动态");
                        List<Topic> topics = new ArrayList<Topic>();
                        topics.add(topic);
                        adapter.setItems(topics, "empty");
                    }
                }
            }
        }
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        topics.clear();
        cusorId = "0";
        super.onRefresh(refreshView);

    }

    @Override
    public void refreshResult() {
        cusorId = "0";
        topics.clear();
        isRefresh = true;
        refresh();
    }
}
