package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.persionalcore.entity.User;

/**
 * 动态
 */
public class Status {
    private String id;
    private String message;
    private String img;
    private String favoriteCnt;
    private String createTime;
    private int isFavor;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFavoriteCnt() {
        return favoriteCnt;
    }

    public void setFavoriteCnt(String favoriteCnt) {
        this.favoriteCnt = favoriteCnt;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsFavor() {
        return isFavor;
    }

    public void setIsFavor(int isFavor) {
        this.isFavor = isFavor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
