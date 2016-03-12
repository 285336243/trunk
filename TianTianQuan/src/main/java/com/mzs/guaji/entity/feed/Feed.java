package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;

public abstract class Feed {

	@Expose
	private long id;
	@Expose
	private String action;
	@Expose
	private String targetType;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
