package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by wlanjie on 14-3-3.
 */
public class PrivateLetter {
    private long responseCode;
    private String responseMessage;
    private long total;
    private long page;
    private List<PrivatePostList> list;

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

    public List<PrivatePostList> getList() {
        return list;
    }

    public void setList(List<PrivatePostList> list) {
        this.list = list;
    }
}
