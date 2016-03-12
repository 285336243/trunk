package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.persionalcore.entity.User;

/**
 * 评论列表
 *
 *
 *
 */
public class Post {
    private String message;
    private String  id;
    private String createTime;
    private User user;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



}
