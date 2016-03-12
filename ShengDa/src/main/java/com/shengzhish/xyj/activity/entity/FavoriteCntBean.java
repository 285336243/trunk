package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

/**
 *    喜欢数
 *
 *
 */
public class FavoriteCntBean extends Response{
    private  String favoriteCnt;
    public String getFavoriteCnt() {
        return favoriteCnt;
    }

    public void setFavoriteCnt(String favoriteCnt) {
        this.favoriteCnt = favoriteCnt;
    }
}
