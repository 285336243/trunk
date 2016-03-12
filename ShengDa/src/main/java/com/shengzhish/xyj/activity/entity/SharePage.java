package com.shengzhish.xyj.activity.entity;

/**
 * 微信分享
 */
public class SharePage {
    /**
     * 分享标题
     */
    private String title;
    /**
     * 分享连接
     */
    private String url;
    /**
     *分享图标
     */
    private String icon;
    /**
     *分享描述
     */
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
