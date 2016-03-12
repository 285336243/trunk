package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class VideoList extends GuaJiResponse{

	@Expose
	private List<GroupVideo> videos;

	public List<GroupVideo> getVideos() {
		return videos;
	}

	public void setVideos(List<GroupVideo> videos) {
		this.videos = videos;
	}
	
}
