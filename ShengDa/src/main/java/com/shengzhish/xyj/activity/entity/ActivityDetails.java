package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityDetails extends Response {

    private ActivityItem activity;
    private List<ActivityDetailsItem> details;
    public void setActivity(ActivityItem activity) {
        this.activity = activity;
    }
    public ActivityItem getActivity() {
        return activity;
    }
    public void setDetails(List<ActivityDetailsItem> details) {
        this.details = details;
    }
    public List<ActivityDetailsItem> getDetails() {
        return details;
    }
}
