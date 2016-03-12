package com.socialtv.message.entity;

import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-7-3.
 */
public class PrivateLetterDetailList {
    private String id;
    private User user;
    private String post;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
