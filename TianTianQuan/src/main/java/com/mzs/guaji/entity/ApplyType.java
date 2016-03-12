package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by wlanjie on 13-12-16.
 */
public class ApplyType {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private List<EntryForm> entryForms;

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

    public List<EntryForm> getEntryForms() {
        return entryForms;
    }

    public void setEntryForms(List<EntryForm> entryForms) {
        this.entryForms = entryForms;
    }
}
