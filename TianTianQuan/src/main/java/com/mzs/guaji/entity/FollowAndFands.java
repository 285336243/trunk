package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class FollowAndFands extends GuaJiResponse {

	@Expose
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
