package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

import java.util.List;

/**
 * Created by wlanjie on 14-7-7.
 */
public class FollowResponse extends Response {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
