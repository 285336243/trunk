package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class UploadPic {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private Pic pic;
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
	public Pic getPic() {
		return pic;
	}
	public void setPic(Pic pic) {
		this.pic = pic;
	}
	
}
