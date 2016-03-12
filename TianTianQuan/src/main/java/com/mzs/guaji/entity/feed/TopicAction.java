package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.topic.entity.Topic;

/**
 * Created by wlanjie on 13-12-20.
 */
public class TopicAction {
    @Expose
    private long userId;
    @Expose
    private String userNickname;
    @Expose
    private String userAvatar;
    @Expose
    private Topic topic;
    @Expose
    private String createTime;

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
}
