package com.jiujie8.choice.publicentity;

import com.jiujie8.choice.Response;

/**
 * Created by wlanjie on 14/12/1.
 */
public class Login extends Response {
    private User user;
    private Token token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
