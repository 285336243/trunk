package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GroupNewsList extends GuaJiResponse {

	@Expose
	private List<GroupNews> news;

	public List<GroupNews> getNews() {
		return news;
	}

	public void setNews(List<GroupNews> news) {
		this.news = news;
	}
	
}
