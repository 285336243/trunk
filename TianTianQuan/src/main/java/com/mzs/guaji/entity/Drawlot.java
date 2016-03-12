package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-2-12.
 */
public class Drawlot {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private ShareTemplete shareTemplete;
    @Expose
    private DrawlotPrize prize;
    @Expose
    private Stimulate stimulate;

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

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }

    public DrawlotPrize getPrize() {
        return prize;
    }

    public void setPrize(DrawlotPrize prize) {
        this.prize = prize;
    }

    public Stimulate getStimulate() {
        return stimulate;
    }

    public void setStimulate(Stimulate stimulate) {
        this.stimulate = stimulate;
    }
}
