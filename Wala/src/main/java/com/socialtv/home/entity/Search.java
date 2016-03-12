package com.socialtv.home.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

import java.util.List;

/**
 * Created by wlanjie on 14-10-20.
 */
public class Search extends Response {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
