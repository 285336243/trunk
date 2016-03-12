package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-2-12.
 */
public class ShakeResult {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private Shake shake;

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

    public Shake getShake() {
        return shake;
    }

    public void setShake(Shake shake) {
        this.shake = shake;
    }
}
