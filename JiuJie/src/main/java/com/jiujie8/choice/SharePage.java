package com.jiujie8.choice;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-7-30.
 */
public class SharePage implements Serializable {
    private static final long serialVersionUID = -9217917458881122982L;
    private String title;
    private String desc;
    private String icon;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
