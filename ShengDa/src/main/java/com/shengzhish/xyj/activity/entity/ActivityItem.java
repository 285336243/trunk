package com.shengzhish.xyj.activity.entity;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityItem {
    private String id;
    private String title;
    private String subTitle;
    private String coverImg;
    private String time;
    private String joinsCnt;
    private int isOver;

    private int interval;

    private Render render;
    private int isJoined;
    private String rule;
    private String type;
    private String img;

    /**
     * 分享
     */
    private ShareTemplete shareTemplete;

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }

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

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setJoinsCnt(String joinsCnt) {
        this.joinsCnt = joinsCnt;
    }

    public String getJoinsCnt() {
        return joinsCnt;
    }

    public void setIsOver(int isOver) {
        this.isOver = isOver;
    }

    public int getIsOver() {
        return isOver;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(int isJoined) {
        this.isJoined = isJoined;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Render getRender() {
        return render;
    }

    public void setRender(Render render) {
        this.render = render;
    }
}
