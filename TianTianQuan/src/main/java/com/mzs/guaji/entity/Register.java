package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class Register {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private long userId;
	@Expose
	private String nickname;
	@Expose
	private String avatar;
    @Expose
    private Score givenScore;
	public long getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(long responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
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

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }
}
