package com.shengzhish.xyj.gallery.entity;

import com.shengzhish.xyj.gallery.Color;

/**
 * Created by wlanjie on 14-6-4.
 */
public class Post {
    private String id;
    private String message;
    private String createTime;
    private Color color;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getCreateTime() {
        return createTime;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
