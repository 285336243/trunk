package com.mzs.guaji.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-2-21.
 */
public class GameList {

    @Expose
    private long id;
    @Expose
    private String name;
    @Expose
    private String type;
    @Expose
    private JsonElement param;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParam(JsonElement param) {
        this.param = param;
    }

    public JsonElement getParam() {
        return param;
    }
}
