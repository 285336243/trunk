package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Epg {

	@Expose
	private long programId;
	@Expose
	private int epgId;
	@Expose
	private String epgName;
	@Expose
	private int startTime;
	@Expose
	private int endTime;
	@Expose
	private String title;
	@Expose
	private List<String> imgs;
	@Expose
	private Group group;
	
	public long getProgramId() {
		return programId;
	}
	public void setProgramId(long programId) {
		this.programId = programId;
	}
	public int getEpgId() {
		return epgId;
	}
	public void setEpgId(int epgId) {
		this.epgId = epgId;
	}
	public String getEpgName() {
		return epgName;
	}
	public void setEpgName(String epgName) {
		this.epgName = epgName;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getImgs() {
		return imgs;
	}
	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	
}
