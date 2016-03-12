package com.mzs.guaji.topic.entity;

/**
 * Created by wlanjie on 13-12-20.
 */
public class TopicAction {
    private long userId;
    private String userNickname;
    private String userAvatar;
    private String userRenderTo;
    private Topic topic;
    private String createTime;
    private String img;

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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUserRenderTo(String userRenderTo) {
        this.userRenderTo = userRenderTo;
    }

    public String getUserRenderTo() {
        return userRenderTo;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
