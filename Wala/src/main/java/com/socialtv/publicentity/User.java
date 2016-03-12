package com.socialtv.publicentity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wlanjie on 14-6-25.
 */
public class User implements Serializable {
    private static final long serialVersionUID = -5896820140820521226L;
    private String userId;
    private String nickname;
    private String gender;
    private String avatar;
    private String score;
    private String bgImg;
    private String signature;
    private long fansCnt;
    private long followCnt;
    private long picsCnt;
    private int isFollow;
    private String mobile;
    private List<Pics> pics;
    private List<UserBadge> badges;
    private NicknameColor nicknameColor;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getFansCnt() {
        return fansCnt;
    }

    public void setFansCnt(long fansCnt) {
        this.fansCnt = fansCnt;
    }

    public long getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(long followCnt) {
        this.followCnt = followCnt;
    }

    public long getPicsCnt() {
        return picsCnt;
    }

    public void setPicsCnt(long picsCnt) {
        this.picsCnt = picsCnt;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }

    public List<UserBadge> getBadges() {
        return badges;
    }

    public void setBadges(List<UserBadge> badges) {
        this.badges = badges;
    }

    public NicknameColor getNicknameColor() {
        return nicknameColor;
    }

    public void setNicknameColor(NicknameColor nicknameColor) {
        this.nicknameColor = nicknameColor;
    }
}
