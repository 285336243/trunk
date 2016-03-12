package com.mzs.guaji.entity;

import com.mzs.guaji.topic.entity.Post;

/**
 * Created by wlanjie on 14-4-16.
 */
public class MessageActivityTopicPost {
    private long id;
    private String type;
    private long userId;
    private String userNickname;
    private String userAvatar;
    private String message;
    private long supportsCnt;
    private Post post;
    private int isSupported;
    private String createTime;
    private ActivityTopicItem topic;
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
    public ActivityTopicItem getTopic() {
        return topic;
    }
    public void setTopic(ActivityTopicItem topic) {
        this.topic = topic;
    }

}
