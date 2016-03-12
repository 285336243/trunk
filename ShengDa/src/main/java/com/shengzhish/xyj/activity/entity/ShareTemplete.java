package com.shengzhish.xyj.activity.entity;

/**
 * 分享字段
 */
public class ShareTemplete {

    /**
     * 分享文字
     */
    private String shareText;
    /**
     * 分享图片URL
     */
    private String shareImg;
    /**
     * 微信分享
     */
    private SharePage sharePage;

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getShareImg() {
        return shareImg;
    }

    public void setShareImg(String shareImg) {
        this.shareImg = shareImg;
    }

    public SharePage getSharePage() {
        return sharePage;
    }

    public void setSharePage(SharePage sharePage) {
        this.sharePage = sharePage;
    }

}
