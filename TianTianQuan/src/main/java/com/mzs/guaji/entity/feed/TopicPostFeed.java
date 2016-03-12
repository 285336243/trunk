package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;

public class TopicPostFeed extends Feed {
	
	@Expose
	private TopicPost target;

	public TopicPost getTarget() {
		return target;
	}

	public void setTarget(TopicPost target) {
		this.target = target;
	}
	
}
