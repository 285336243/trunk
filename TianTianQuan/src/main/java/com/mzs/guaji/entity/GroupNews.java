package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class GroupNews {

	@Expose
	private long id;
	@Expose
	private String title;
	@Expose
	private String img;
	@Expose
	private String source;
	@Expose
	private String content;
	@Expose
	private long supportsCnt;
	@Expose
	private String createTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getSupportsCnt() {
		return supportsCnt;
	}
	public void setSupportsCnt(long supportsCnt) {
		this.supportsCnt = supportsCnt;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
