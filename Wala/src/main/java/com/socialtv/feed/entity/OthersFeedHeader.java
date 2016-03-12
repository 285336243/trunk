package com.socialtv.feed.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-7-4.
 */
public class OthersFeedHeader extends Response {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
