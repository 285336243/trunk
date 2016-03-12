package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class GroupNewsDetails {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private GroupNews news;
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
	public GroupNews getNews() {
		return news;
	}
	public void setNews(GroupNews news) {
		this.news = news;
	}
	
}
