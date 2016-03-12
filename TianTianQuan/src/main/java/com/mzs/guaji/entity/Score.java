package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-17.
 */
public class Score {
    @Expose
    private long amout;
    @Expose
    private String message;

    public long getAmout() {
        return amout;
    }

    public void setAmout(long amout) {
        this.amout = amout;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
