package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by sunjian on 13-12-25.
 */
public class QuestionRank {
    @Expose
    private long userId;
    @Expose
    private String userNickname;
    @Expose
    private String userAvatar;
    @Expose
    private int coins;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
