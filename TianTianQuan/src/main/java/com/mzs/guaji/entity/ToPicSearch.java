package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.topic.entity.Topic;

import java.util.List;

public class ToPicSearch extends GuaJiResponse {

	@Expose
	private List<Topic> topics;

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}
	
}
