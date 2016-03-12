package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class ExchangeShop {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private String okMsg;
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
	public String getOkMsg() {
		return okMsg;
	}
	public void setOkMsg(String okMsg) {
		this.okMsg = okMsg;
	}
	
}
