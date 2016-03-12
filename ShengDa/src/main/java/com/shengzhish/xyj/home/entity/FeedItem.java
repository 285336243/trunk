package com.shengzhish.xyj.home.entity;

/**
 * Created by wlanjie on 14-5-30.
 */
public class FeedItem {
    private String id;
    private String title;
    private String tag;
    private String img;
    private String type;
    private String referId;
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
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setReferId(String referId) {
        this.referId = referId;
    }
    public String getReferId() {
        return referId;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getCreateTime() {
        return createTime;
    }
}
