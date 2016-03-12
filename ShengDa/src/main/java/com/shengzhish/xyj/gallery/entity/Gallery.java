package com.shengzhish.xyj.gallery.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-3.
 */
public class Gallery extends Response {
    private List<Pic> pics;
    public void setPics(List<Pic> pics) {
        this.pics = pics;
    }
    public List<Pic> getPics() {
        return pics;
    }
}
