package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.topic.entity.Topic;

public class TopicPost {

	@Expose
	private long id;
	@Expose
	private String type;
	@Expose
	private String message;
	@Expose
	private long userId;
	@Expose
	private String userNickname;
	@Expose
	private String userAvatar;
	@Expose
	private String createTime;
	@Expose
	private Topic topic;
    @Expose
    private long supportsCnt;
    @Expose
    private long isSupported;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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

    public long getSupportsCnt() {
        return supportsCnt;
    }

    public void setSupportsCnt(long supportsCnt) {
        this.supportsCnt = supportsCnt;
    }

    public long getIsSupported() {
        return isSupported;
    }

    public void setIsSupported(long isSupported) {
        this.isSupported = isSupported;
    }
}
