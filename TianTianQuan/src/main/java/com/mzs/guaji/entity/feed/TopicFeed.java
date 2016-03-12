package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.topic.entity.Topic;

public class TopicFeed extends Feed {

	@Expose
	private Topic target;

	public Topic getTarget() {
		return target;
	}
	public void setTarget(Topic target) {
		this.target = target;
	}
}
