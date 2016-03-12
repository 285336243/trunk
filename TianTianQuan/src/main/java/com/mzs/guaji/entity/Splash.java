package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class Splash {

    @Expose
	private long responseCode;
    @Expose
	private String responseMessage;
    @Expose
    private String link;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
