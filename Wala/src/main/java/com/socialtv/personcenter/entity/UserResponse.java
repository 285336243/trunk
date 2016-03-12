package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

import java.util.List;

/**
 *
 *
 *
 *
 */
public class UserResponse extends Response{
    private User user;
    private List<ExternalAccounts> externalAccount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ExternalAccounts> getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccount(List<ExternalAccounts> externalAccount) {
        this.externalAccount = externalAccount;
    }
}
