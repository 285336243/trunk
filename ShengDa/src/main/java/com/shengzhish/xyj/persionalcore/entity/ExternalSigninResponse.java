package com.shengzhish.xyj.persionalcore.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-17.
 */
public class ExternalSigninResponse extends Response {
    private User user;
    private List<ExternalAccounts> externalAccounts;

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
