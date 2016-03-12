package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-2-12.
 */
public class ShakeSurplusTime {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private int retainAccessTimes;
    @Expose
    private String notice;

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

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
