package com.shengzhish.xyj.persionalcore.entity;

/**
 * do not read message total amount
 */
public class MessageAmount {

    private long responseCode;
    private String responseMessage;
    private String total;

    public long getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
