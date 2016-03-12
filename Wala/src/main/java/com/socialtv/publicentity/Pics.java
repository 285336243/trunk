package com.socialtv.publicentity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-6-28.
 */
public class Pics implements Serializable {
    private static final long serialVersionUID = 2635369886462161058L;
    private String id;
    private String img;
    private boolean isChecked;

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
