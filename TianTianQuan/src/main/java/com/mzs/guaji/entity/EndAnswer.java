package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-24.
 */
public class EndAnswer {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private int totalRightQuestionAmount;
    @Expose
    private int totalWrongQuestionAmount;
    @Expose
    private int totalCoinsAmout;
    @Expose
    private int totalAnswerTime;
    @Expose
    private String message;
    @Expose
    private ShareTemplete shareTemplete;

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

    public int getTotalRightQuestionAmount() {
        return totalRightQuestionAmount;
    }

    public void setTotalRightQuestionAmount(int totalRightQuestionAmount) {
        this.totalRightQuestionAmount = totalRightQuestionAmount;
    }

    public int getTotalWrongQuestionAmount() {
        return totalWrongQuestionAmount;
    }

    public void setTotalWrongQuestionAmount(int totalWrongQuestionAmount) {
        this.totalWrongQuestionAmount = totalWrongQuestionAmount;
    }

    public int getTotalCoinsAmout() {
        return totalCoinsAmout;
    }

    public void setTotalCoinsAmout(int totalCoinsAmout) {
        this.totalCoinsAmout = totalCoinsAmout;
    }

    public int getTotalAnswerTime() {
        return totalAnswerTime;
    }

    public void setTotalAnswerTime(int totalAnswerTime) {
        this.totalAnswerTime = totalAnswerTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }
}
