package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class PersonCenterInfo {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private long userId;
	@Expose
	private String nickname;
	@Expose
	private String signature;
	@Expose
	private String avatar;
	@Expose
	private String bgImg;
	@Expose
	private String gender;
	@Expose
	private long groupsCnt;
	@Expose
	private long followsCnt;
	@Expose
	private long topicsCnt;
	@Expose
	private long score;
	@Expose
	private List<Pic> pics;
	@Expose
	private String mobile;
    @Expose
    private Score givenScore;
    @Expose
    private List<ExternalAccount> externalAccounts;
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
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getBgImg() {
		return bgImg;
	}
	public void setBgImg(String bgImg) {
		this.bgImg = bgImg;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public long getGroupsCnt() {
		return groupsCnt;
	}
	public void setGroupsCnt(long groupsCnt) {
		this.groupsCnt = groupsCnt;
	}
	public long getFollowsCnt() {
		return followsCnt;
	}
	public void setFollowsCnt(long followsCnt) {
		this.followsCnt = followsCnt;
	}
	public long getTopicsCnt() {
		return topicsCnt;
	}
	public void setTopicsCnt(long topicsCnt) {
		this.topicsCnt = topicsCnt;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public List<Pic> getPics() {
		return pics;
	}
	public void setPics(List<Pic> pics) {
		this.pics = pics;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }

    public List<ExternalAccount> getExternalAccounts() {
        return externalAccounts;
    }

    public void setExternalAccounts(List<ExternalAccount> externalAccounts) {
        this.externalAccounts = externalAccounts;
    }
}
