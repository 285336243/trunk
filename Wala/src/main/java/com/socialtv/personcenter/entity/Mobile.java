package com.socialtv.personcenter.entity;

import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-8-14.
 */
public class Mobile {
    private String mobile;
    private com.socialtv.publicentity.User user;
    private int isFollow;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }
}
