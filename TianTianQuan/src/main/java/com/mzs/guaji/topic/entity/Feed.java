package com.mzs.guaji.topic.entity;

import com.google.gson.JsonElement;

/**
 * Created by wlanjie on 14-5-20.
 */
public class Feed  {

    private long id;
    private String action;
    private String targetType;
    private JsonElement target;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getTargetType() {
        return targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setTarget(JsonElement target) {
        this.target = target;
    }

    public JsonElement getTarget() {
        return target;
    }
}
