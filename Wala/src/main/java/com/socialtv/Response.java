package com.socialtv;

/**
 * Created by wlanjie on 14-6-23.
 */
public class Response {

    private long page;
    private long total;
    private long responseCode;
    private String responseMessage;
    private Score givenSore;
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

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }

    public Score getGivenSore() {
        return givenSore;
    }

    public void setGivenSore(Score givenSore) {
        this.givenSore = givenSore;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
