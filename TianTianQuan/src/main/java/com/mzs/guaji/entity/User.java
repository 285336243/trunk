package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class User {

	@Expose
	private long userId;
	@Expose
	private String nickname;
	@Expose
	private String avatar;
	@Expose
	private int isFollowed;
    @Expose
    private String renderTo;
    @Expose
    private String title;
    @Expose
    private String coverImg;
    @Expose
    private String signature;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getIsFollowed() {
		return isFollowed;
	}
	public void setIsFollowed(int isFollowed) {
		this.isFollowed = isFollowed;
	}
    public void setRenderTo(String renderTo) {
        this.renderTo = renderTo;
    }
    public String getRenderTo() {
        return renderTo;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
	public String getCoverImg() {
        return coverImg;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getSignature() {
        return signature;
    }
}
