package com.socialtv.program.entity;

import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-7-8.
 */
public class GroupChatItem {
    private String id;
    private String msg;
    private User createUser;
    private String createTime;
    private User replyPost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public User getReplyPost() {
        return replyPost;
    }

    public void setReplyPost(User replyPost) {
        this.replyPost = replyPost;
    }
}
