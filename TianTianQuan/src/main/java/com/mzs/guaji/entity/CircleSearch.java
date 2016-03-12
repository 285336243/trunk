package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class CircleSearch extends GuaJiResponse{
	
	@Expose
	private List<Group> groups;

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
}
