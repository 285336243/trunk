package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Login {

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
    private String mobile;
    @Expose
    private List<ExternalAccounts> externalAccounts;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<ExternalAccounts> getExternalAccounts() {
        return externalAccounts;
    }

    public void setExternalAccounts(List<ExternalAccounts> externalAccounts) {
        this.externalAccounts = externalAccounts;
    }
}
