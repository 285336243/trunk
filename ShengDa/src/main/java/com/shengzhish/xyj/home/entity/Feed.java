package com.shengzhish.xyj.home.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-5-30.
 */
public class Feed extends Response {

    private List<FeedItem> banners;
    private List<FeedItem> feeds;

    public void setBanners(List<FeedItem> banners) {
        this.banners = banners;
    }
    public List<FeedItem> getBanners() {
        return banners;
    }
    public void setFeeds(List<FeedItem> feeds) {
        this.feeds = feeds;
    }
    public List<FeedItem> getFeeds() {
        return feeds;
    }
}
