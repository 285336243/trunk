package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * 热门推荐,最新动态
 */
public class RecommendResponse extends Response {
    private List<Status> statuses;

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

}
