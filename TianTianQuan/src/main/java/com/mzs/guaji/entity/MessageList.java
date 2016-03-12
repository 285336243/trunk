package com.mzs.guaji.entity;

import com.google.gson.JsonElement;

/**
 * Created by wlanjie on 14-3-6.
 */
public class MessageList {

    private long id;
    private String type;
    private JsonElement message;
    private long status;
    private boolean isChecked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getMessage() {
        return message;
    }

    public void setMessage(JsonElement message) {
        this.message = message;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public boolean equals(Object o) {
        return  ((MessageList) o).getId() == id;
    }
}
