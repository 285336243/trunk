package com.mzs.guaji.entity;

import com.mzs.guaji.topic.entity.Topic;

/**
 * Created by wlanjie on 14-3-12.
 *
 */
import com.mzs.guaji.topic.entity.Topic;
public class CelebrityPost {

    private long id;
    private long celebrityId;
    private String celebrityName;
    private String celebrityAvatar;
    private String message;
    private String video;
    private int videoTime;
    private String createTime;
    private Topic topic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCelebrityId() {
        return celebrityId;
    }

    public void setCelebrityId(long celebrityId) {
        this.celebrityId = celebrityId;
    }

    public String getCelebrityName() {
        return celebrityName;
    }

    public void setCelebrityName(String celebrityName) {
        this.celebrityName = celebrityName;
    }

    public String getCelebrityAvatar() {
        return celebrityAvatar;
    }

    public void setCelebrityAvatar(String celebrityAvatar) {
        this.celebrityAvatar = celebrityAvatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(int videoTime) {
        this.videoTime = videoTime;
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
