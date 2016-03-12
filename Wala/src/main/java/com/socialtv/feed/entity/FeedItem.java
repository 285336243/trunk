package com.socialtv.feed.entity;

import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-7-3.
 */
public class FeedItem {
    private User user;
    private String type;
    private String action;
    private String actionTime;
    private Refer refer;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public Refer getRefer() {
        return refer;
    }

    public void setRefer(Refer refer) {
        this.refer = refer;
    }
}
