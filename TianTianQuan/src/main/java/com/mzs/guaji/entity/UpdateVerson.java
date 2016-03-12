package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class UpdateVerson {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private String upgradeMsg;
	@Expose
	private String versionNo;
	@Expose
	private String upgradeUrl;
    @Expose
    private int versionCode;
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
	public String getUpgradeMsg() {
		return upgradeMsg;
	}
	public void setUpgradeMsg(String upgradeMsg) {
		this.upgradeMsg = upgradeMsg;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getUpgradeUrl() {
		return upgradeUrl;
	}
	public void setUpgradeUrl(String upgradeUrl) {
		this.upgradeUrl = upgradeUrl;
	}
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        return versionCode;
    }
	
}
