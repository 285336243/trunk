package com.socialtv.activity.entitiy;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-6-25.
 */
public class ActivityDetail extends Response {

    private ActivityDetailItem activity;

    public ActivityDetailItem getActivity() {
        return activity;
    }

    public void setActivity(ActivityDetailItem activity) {
        this.activity = activity;
    }
}
