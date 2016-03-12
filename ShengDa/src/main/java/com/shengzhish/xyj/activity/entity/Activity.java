package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-5-30.
 */
public class Activity extends Response {

    private List<ActivityItem> activities;

    public void setActivities(List<ActivityItem> activities) {
        this.activities = activities;
    }

    public List<ActivityItem> getActivities() {
        return activities;
    }
}
