package com.jiujie8.choice.publicentity;

/**
 * Created by wlanjie on 14/12/1.
 */
public class Token {
    private String value;
    private long expireTime;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
