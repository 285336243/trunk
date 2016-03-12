package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.publicentity.User;

import java.util.List;

/**
 * Created by wlanjie on 15/1/13.
 */
public class VoteResultItem {
    private String label;
    private List<User> user;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }
}
