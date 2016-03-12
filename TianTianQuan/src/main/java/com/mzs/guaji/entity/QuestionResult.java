package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-24.
 */
public class QuestionResult {
    @Expose
    private long responseCod;
    @Expose
    private String responseMessage;
    @Expose
    private int coins;
    @Expose
    private String message;

    public long getResponseCod() {
        return responseCod;
    }

    public void setResponseCod(long responseCod) {
        this.responseCod = responseCod;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
