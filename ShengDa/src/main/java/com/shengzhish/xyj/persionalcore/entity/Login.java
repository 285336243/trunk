package com.shengzhish.xyj.persionalcore.entity;

import java.util.List;

public class Login {

    private long responseCode;
    private String responseMessage;
    private User user;
    private List<ExternalAccounts> externalAccounts;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ExternalAccounts> getExternalAccounts() {
        return externalAccounts;
    }

    public void setExternalAccounts(List<ExternalAccounts> externalAccounts) {
        this.externalAccounts = externalAccounts;
    }
}
