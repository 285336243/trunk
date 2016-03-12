package com.shengzhish.xyj.news.entity;

import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.activity.entity.ShareTemplete;

/**
 * 资讯分享
 */
public class ShareResponse extends Response {
    private ShareTemplete shareTemplete;

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }


}
