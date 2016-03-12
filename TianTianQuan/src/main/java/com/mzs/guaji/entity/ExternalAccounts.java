package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-1-3.
 */
public class ExternalAccounts {
    @Expose
    private long id;
    @Expose
    private String type;
    @Expose
    private String token;
    @Expose
    private String uid;
    @Expose
    private String nickname;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
