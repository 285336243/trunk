package com.mzs.guaji.entity;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Shops {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private List<SimpleShop> shops;
    @Expose
    private long total;
    @Expose
    private long page;
	public long getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(long responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public List<SimpleShop> getShops() {
		return shops;
	}
	public void setShops(List<SimpleShop> shops) {
		this.shops = shops;
	}

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public long getPage() {
        return page;
    }
}
