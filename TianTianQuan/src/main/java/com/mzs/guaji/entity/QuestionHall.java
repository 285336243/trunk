package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by sunjian on 13-12-25.
 */
public class QuestionHall {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private int retainAccessTimes;
    @Expose
    private int coins;
    @Expose
    private List<QuestionRank> followRank;
    @Expose
    private List<QuestionRank> totalRank;

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

    public int getRetainAccessTimes() {
        return retainAccessTimes;
    }

    public void setRetainAccessTimes(int retainAccessTimes) {
        this.retainAccessTimes = retainAccessTimes;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public List<QuestionRank> getFollowRank() {
        return followRank;
    }

    public void setFollowRank(List<QuestionRank> followRank) {
        this.followRank = followRank;
    }

    public List<QuestionRank> getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(List<QuestionRank> totalRank) {
        this.totalRank = totalRank;
    }
}
