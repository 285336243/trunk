package com.socialtv.personcenter.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-8-12.
 */
public class AuthCode extends Response {
    private long toVerify;

    public long getToVerify() {
        return toVerify;
    }

    public void setToVerify(long toVerify) {
        this.toVerify = toVerify;
    }
}
