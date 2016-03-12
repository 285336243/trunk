package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.User;

import java.util.List;

public class Login extends Response {

    private User user;
    private List<ExternalAccounts> externalAccount;
    private String render;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ExternalAccounts> getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccounts(List<ExternalAccounts> externalAccount) {
        this.externalAccount = externalAccount;
    }

    public String getRender() {
        return render;
    }

    public void setRender(String render) {
        this.render = render;
    }
}
