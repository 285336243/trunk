package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class GuaJiResponse {

	@Expose
	private long page;
	@Expose
	private long total;
	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
    @Expose
    private Score givenScore;
	public long getPage() {
		return page;
	}
	public void setPage(long page) {
		this.page = page;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
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

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }
}
