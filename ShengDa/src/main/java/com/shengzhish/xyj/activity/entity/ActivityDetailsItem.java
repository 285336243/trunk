package com.shengzhish.xyj.activity.entity;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityDetailsItem {
    private String id;
    private String title;
    private String showTime;
    private String location;
    private long time;
    private String about;
    private String img;
    private int isStart;
    private int isItemGone;
    private int isAboutGone;
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
    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }
    public String getShowTime() {
        return showTime;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getTime() {
        return time;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public String getAbout() {
        return about;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }
    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }
    public int getIsStart() {
        return isStart;
    }

    public int getIsAboutGone() {
        return isAboutGone;
    }

    public void setIsAboutGone(int isAboutGone) {
        this.isAboutGone = isAboutGone;
    }

    public int getIsItemGone() {
        return isItemGone;
    }

    public void setIsItemGone(int isItemGone) {
        this.isItemGone = isItemGone;
    }
}
