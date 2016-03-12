package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.topic.entity.Topic;

import java.util.List;

public class PrivateCircleDetail extends GuaJiResponse {

	@Expose
	private Group group;
	@Expose
	private List<Topic> topics;
	@Expose
	private int isJoined;
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public List<Topic> getTopics() {
		return topics;
	}
	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}
	public int getIsJoined() {
		return isJoined;
	}
	public void setIsJoined(int isJoined) {
		this.isJoined = isJoined;
	}
	
}
