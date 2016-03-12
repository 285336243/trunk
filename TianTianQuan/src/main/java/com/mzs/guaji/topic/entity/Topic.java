package com.mzs.guaji.topic.entity;

public class Topic {
	
	private long id;
    private String celebrityUserId;
    private String celebrityUserNickname;
	private long groupId;
    private long activityId;
    private String activityName;
	private String groupName;
	private String groupImg;
    private String groupType;
	private String title;
	private String desc;
	private String img;
	private long postsCnt;
	private long supportsCnt;
	private String createTime;
	private long userId;
	private String userNickname;
	private String userAvatar;
    private String userRenderTo;
	private int isLiked;
    private String activityImg;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
    public String getCelebrityUserId() {
        return celebrityUserId;
    }
    public void setCelebrityUserId(String celebrityUserId) {
        this.celebrityUserId = celebrityUserId;
    }
    public String getCelebrityUserNickname() {
        return celebrityUserNickname;
    }
    public void setCelebrityUserNickname(String celebrityUserNickname) {
        this.celebrityUserNickname = celebrityUserNickname;
    }
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }
    public long getActivityId() {
        return activityId;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getActivityName() {
        return activityName;
    }
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupImg() {
		return groupImg;
	}
	public void setGroupImg(String groupImg) {
		this.groupImg = groupImg;
	}
    public String getGroupType() {
        return groupType;
    }
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public long getPostsCnt() {
		return postsCnt;
	}
	public void setPostsCnt(long postsCnt) {
		this.postsCnt = postsCnt;
	}
	public long getSupportsCnt() {
		return supportsCnt;
	}
	public void setSupportsCnt(long supportsCnt) {
		this.supportsCnt = supportsCnt;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
	public int getIsLiked() {
		return isLiked;
	}
	public void setIsLiked(int isLiked) {
		this.isLiked = isLiked;
	}

    public String getActivityImg() {
        return activityImg;
    }

    public void setActivityImg(String activityImg) {
        this.activityImg = activityImg;
    }
}
