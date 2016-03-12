package com.mzs.guaji.offical.entity;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.entity.Score;

public class OfficialModules {

	@Expose
	private long responseCode;
	
	@Expose
	private String responseMessage;

    @Expose
    private Score givenScore;

    @Expose
    private List<ModuleSpecs> moduleSpecs;
    @Expose
    private long isJoined;

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

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }

    public List<ModuleSpecs> getModuleSpecs() {
        return moduleSpecs;
    }

    public void setModuleSpecs(List<ModuleSpecs> moduleSpecs) {
        this.moduleSpecs = moduleSpecs;
    }

    public long getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(long isJoined) {
        this.isJoined = isJoined;
    }
}
