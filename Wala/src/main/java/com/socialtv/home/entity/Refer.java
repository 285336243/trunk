package com.socialtv.home.entity;

/**
 * Created by wlanjie on 14-6-23.
 */
public class Refer {
    private String id;
    private String price;
    private String url;
    private String openType;
    private String name;
    private String type;
    private long hideTitle;
    private String title;
    private long hideStatus;
    private long requireLogin;
    private int followCnt;
    private int topicCnt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(long hideTitle) {
        this.hideTitle = hideTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getHideStatus() {
        return hideStatus;
    }

    public void setHideStatus(long hideStatus) {
        this.hideStatus = hideStatus;
    }

    public long getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(long requireLogin) {
        this.requireLogin = requireLogin;
    }

    public int getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(int followCnt) {
        this.followCnt = followCnt;
    }

    public int getTopicCnt() {
        return topicCnt;
    }

    public void setTopicCnt(int topicCnt) {
        this.topicCnt = topicCnt;
    }
}
