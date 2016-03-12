package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class DeletePhoto {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
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
}
