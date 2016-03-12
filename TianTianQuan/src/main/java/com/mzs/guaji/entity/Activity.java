package com.mzs.guaji.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

public class Activity {
	@Expose
	private long id;
	@Expose
	private String name;
	@Expose
	private String bannerImg;
    @Expose
    private String coverImg;
    @Expose
    private String type;
    @Expose
    private JsonElement param;
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
	public String getBannerImg() {
		return bannerImg;
	}
	public void setBannerImg(String bannerImg) {
		this.bannerImg = bannerImg;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getParam() {
        return param;
    }

    public void setParam(JsonElement param) {
        this.param = param;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getCoverImg() {
        return coverImg;
    }
}
