package com.shengzhish.xyj;

/**
 * Created by wlanjie on 14-5-30.
 */
public class Response {
    private long page;
    private long total;
    private long responseCode;
    private String responseMessage;

    public void setPage(long page) {
        this.page = page;
    }
    public long getPage() {
        return page;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public long getTotal() {
        return total;
    }
    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }
    public long getResponseCode() {
        return responseCode;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
}
