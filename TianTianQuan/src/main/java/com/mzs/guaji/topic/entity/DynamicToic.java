package com.mzs.guaji.topic.entity;

import com.mzs.guaji.entity.GuaJiResponse;

import java.util.List;

/**
 * Created by wlanjie on 14-5-20.
 */
public class DynamicToic extends GuaJiResponse {

    private List<Feed> feeds;

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }
}
