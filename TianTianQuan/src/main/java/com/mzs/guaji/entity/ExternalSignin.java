package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ExternalSignin {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private String nickname;
	@Expose
	private Long userId;
	@Expose
	private String avatar;
	@Expose
	private Integer needAccount;
	@Expose
	private String welcomeText;
	@Expose
	private Long externalAccountId;
    @Expose
    private String mobile;
    @Expose
    private List<ExternalAccounts> externalAccounts;

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getNeedAccount() {
		return needAccount;
	}
	public void setNeedAccount(Integer needAccount) {
		this.needAccount = needAccount;
	}
	public String getWelcomeText() {
		return welcomeText;
	}
	public void setWelcomeText(String welcomeText) {
		this.welcomeText = welcomeText;
	}
	public Long getExternalAccountId() {
		return externalAccountId;
	}
	public void setExternalAccountId(Long externalAccountId) {
		this.externalAccountId = externalAccountId;
	}
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
