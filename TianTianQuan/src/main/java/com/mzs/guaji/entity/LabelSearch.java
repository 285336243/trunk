package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class LabelSearch {
	
	@Expose
	private Long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private List<Label> labels;

	public Long getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Long responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
	
}
