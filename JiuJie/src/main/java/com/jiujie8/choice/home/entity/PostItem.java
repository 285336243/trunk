package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.publicentity.User;

/**
 * Created by wlanjie on 14/12/8.
 */
public class PostItem {
    private int id;
    private User user;
    private String message;
    private Agree agree;
    private String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Agree getAgree() {
        return agree;
    }

    public void setAgree(Agree agree) {
        this.agree = agree;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
