package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-31.
 */
public class BindingAccount {
    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private long externalAccountId;
    @Expose
    private String externalNickname;

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

    public long getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(long externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getExternalNickname() {
        return externalNickname;
    }

    public void setExternalNickname(String externalNickname) {
        this.externalNickname = externalNickname;
    }
}
