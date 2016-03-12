package com.socialtv.message.entity;

import com.socialtv.publicentity.User;
import com.socialtv.topic.ReplyPost;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-7-2.
 */
public class Post implements Serializable {
    private static final long serialVersionUID = 8683541196185692996L;
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
