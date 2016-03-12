package com.socialtv.feed.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.Topic;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 */
public class Feed extends Response {
    private String cusorId;
    private List<Topic> list;

    public String getCusorId() {
        return cusorId;
    }

    public void setCusorId(String cusorId) {
        this.cusorId = cusorId;
    }

    public List<Topic> getList() {
        return list;
    }

    public void setList(List<Topic> list) {
        this.list = list;
    }
}
