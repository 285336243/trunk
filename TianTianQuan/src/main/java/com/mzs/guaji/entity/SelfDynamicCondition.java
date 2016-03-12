package com.mzs.guaji.entity;

import com.mzs.guaji.topic.entity.Feed;

import java.util.List;

public class SelfDynamicCondition extends GuaJiResponse {

//	@Expose
//	private JsonArray feeds;
//
//	public JsonArray getFeeds() {
//		return feeds;
//	}
//
//	public void setFeeds(JsonArray feeds) {
//		this.feeds = feeds;
//	}

    private List<Feed> feeds;

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }
}
