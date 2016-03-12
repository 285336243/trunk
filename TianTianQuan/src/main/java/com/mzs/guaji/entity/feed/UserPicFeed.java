package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;

public class UserPicFeed extends Feed{

	@Expose
	private UserPic target;

	public UserPic getTarget() {
		return target;
	}

	public void setTarget(UserPic target) {
		this.target = target;
	}
	
}
