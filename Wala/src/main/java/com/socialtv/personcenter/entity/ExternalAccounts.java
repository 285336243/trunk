package com.socialtv.personcenter.entity;

import com.socialtv.Response;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-1-3.
 */
public class ExternalAccounts extends Response implements Serializable {

    private static final long serialVersionUID = 2912082816657624113L;
    private String id;
    private String type;
    private String nickname;
    private String token;
    private String uid;
    private String externalAccountId;
    private String externalNickname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getExternalNickname() {
        return externalNickname;
    }

    public void setExternalNickname(String externalNickname) {
        this.externalNickname = externalNickname;
    }
}
