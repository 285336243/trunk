package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class Group {
	@Expose
	private long id;
	@Expose
	private String type;
	@Expose
	private String name;
	@Expose
	private String coverImg;
	@Expose
	private String img;
	@Expose
	private String about;
	@Expose
	private long membersCnt;
	@Expose
	private long topicsCnt;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCoverImg() {
		return coverImg;
	}
	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public long getMembersCnt() {
		return membersCnt;
	}
	public void setMembersCnt(long membersCnt) {
		this.membersCnt = membersCnt;
	}
	public long getTopicsCnt() {
		return topicsCnt;
	}
	public void setTopicsCnt(long topicsCnt) {
		this.topicsCnt = topicsCnt;
	}
	
}
