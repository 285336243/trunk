package com.mzs.guaji.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-3-4.
 */
public class PrivateLetterDetail {

    private long responseCode;
    private String responseMessage;
    private long total;
    private long page;
    private List<PrivatePost> list;

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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public List<PrivatePost> getList() {
        return list;
    }

    public void setList(List<PrivatePost> list) {
        this.list = list;
    }
}
