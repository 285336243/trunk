package com.socialtv.publicentity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/10/29.
 */
public class UserBadge implements Serializable {
    private static final long serialVersionUID = 578754553453720717L;
    private String name;
    private String id;
    private String img;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
