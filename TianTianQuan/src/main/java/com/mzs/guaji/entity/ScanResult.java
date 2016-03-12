package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-23.
 */
public class ScanResult {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private Score givenScore;
    @Expose
    private Scan scan;

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

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }
}
