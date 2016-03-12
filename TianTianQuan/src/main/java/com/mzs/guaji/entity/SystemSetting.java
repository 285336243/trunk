package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

public class SystemSetting extends DefaultReponse {

	@Expose
	private String bindMobileMode;

	public String getBindMobileMode() {
		return bindMobileMode;
	}

	public void setBindMobileMode(String bindMobileMode) {
		this.bindMobileMode = bindMobileMode;
	}
	
}
