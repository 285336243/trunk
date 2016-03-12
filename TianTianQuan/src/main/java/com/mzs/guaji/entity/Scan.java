package com.mzs.guaji.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-23.
 */
public class Scan {
    @Expose
    private String type;
    @Expose
    private JsonElement param;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getParam() {
        return param;
    }

    public void setParam(JsonElement param) {
        this.param = param;
    }
}
