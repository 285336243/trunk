package com.mzs.guaji.entity;

import com.mzs.guaji.topic.entity.Post;
import com.mzs.guaji.topic.entity.Topic;

/**
 * Created by wlanjie on 14-8-27.
 */
public class MessageCelebrityTopicPost {
    private long id;
    private String type;
    private long userId;
    private String userNickname;
    private String userAvatar;
    private String userRenderTo;
    private String message;
    private long supportsCnt;
    private Post post;
    private int isSupported;
    private String createTime;
    private Topic topic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserRenderTo() {
        return userRenderTo;
    }

    public void setUserRenderTo(String userRenderTo) {
        this.userRenderTo = userRenderTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSupportsCnt() {
        return supportsCnt;
    }

    public void setSupportsCnt(long supportsCnt) {
        this.supportsCnt = supportsCnt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getIsSupported() {
        return isSupported;
    }

    public void setIsSupported(int isSupported) {
        this.isSupported = isSupported;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
