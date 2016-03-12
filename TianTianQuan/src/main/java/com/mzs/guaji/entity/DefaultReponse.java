package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class DefaultReponse {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
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

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }
}
