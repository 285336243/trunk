package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class DetailShop {

	@Expose
	private long id;
	@Expose
	private String name;
	@Expose
	private String coverImg;
	@Expose
	private String price;
	@Expose
	private String img;
	@Expose
	private String about;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
	
}
