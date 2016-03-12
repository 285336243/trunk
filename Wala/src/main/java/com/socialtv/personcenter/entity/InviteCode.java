package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

import java.util.List;

/**
 * Created by wlanjie on 14-8-13.
 */
public class InviteCode extends Response {
    private String notice;
    private List<com.socialtv.publicentity.User> users;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
