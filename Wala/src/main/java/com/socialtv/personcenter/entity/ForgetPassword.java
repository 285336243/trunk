package com.socialtv.personcenter.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14/10/28.
 */
public class ForgetPassword extends Response {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
