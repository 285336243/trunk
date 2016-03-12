package com.mzs.guaji.topic.entity;

public class TopicPost {

	private long id;
	private String type;
	private String message;
	private long userId;
	private String userNickname;
	private String userAvatar;
	private String createTime;
	private Topic topic;
    private long supportsCnt;
    private long isSupported;
    private String audio;
    private String audioTime;
    private String userRenderTo;
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
    public void setAudio(String audio) {
        this.audio = audio;
    }
    public String getAudio() {
        return audio;
    }
    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }
    public String getAudioTime() {
        return audioTime;
    }
    public void setUserRenderTo(String userRenderTo) {
        this.userRenderTo = userRenderTo;
    }
    public String getUserRenderTo() {
        return userRenderTo;
    }
}
