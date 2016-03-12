package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class ＤefiniteShopDetail {

	@Expose
	private long responseCode;
	@Expose
	private String responseMessage;
	@Expose
	private DetailShop shop;
	@Expose
	private int exchangeMode;
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
	public DetailShop getShop() {
		return shop;
	}
	public void setShop(DetailShop shop) {
		this.shop = shop;
	}
	public int getExchangeMode() {
		return exchangeMode;
	}
	public void setExchangeMode(int exchangeMode) {
		this.exchangeMode = exchangeMode;
	}
	
}
