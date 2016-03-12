package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-1-9.
 */
public class LinkTopic {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private long supportsCnt;

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

    public long getSupportsCnt() {
        return supportsCnt;
    }

    public void setSupportsCnt(long supportsCnt) {
        this.supportsCnt = supportsCnt;
    }
}
