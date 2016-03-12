package com.shengzhish.xyj.gallery.entity;

/**
 * Created by wlanjie on 14-6-3.
 */
public class Pic {
    private String id;
    private String title;
    private String img;
    private String shareText;
    private String createTime;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getCreateTime() {
        return createTime;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }
}
