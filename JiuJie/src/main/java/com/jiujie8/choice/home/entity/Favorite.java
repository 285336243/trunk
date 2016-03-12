package com.jiujie8.choice.home.entity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/12/4.
 */
public class Favorite implements Serializable {
    private static final long serialVersionUID = -4768000372597390874L;
    private long id;
    private String createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
