package com.jiujie8.choice.share;

/**
 * Created by sunjian on 13-12-26.
 */
public class OAuthToken {
    private final String token;
    private final String uid;
    private final long expiredTime;

    public OAuthToken(String token, String uid, long expiredTime) {
        this.token = token;
        this.uid = uid;
        this.expiredTime = expiredTime;
    }

    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    public long getExpiredTime() {
        return expiredTime;
    }
}
