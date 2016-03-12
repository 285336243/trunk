package com.socialtv.topic.entity;

import com.socialtv.publicentity.User;
import com.socialtv.topic.ReplyPost;

/**
 * Created by wlanjie on 14-7-1.
 */
public class TopicPosts {
    private String id;
    private String msg;
    private User createUser;
    private String createTime;
    private ReplyPost replyPost;

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

    public ReplyPost getReplyPost() {
        return replyPost;
    }

    public void setReplyPost(ReplyPost replyPost) {
        this.replyPost = replyPost;
    }
}
