package com.mzs.guaji.topic.entity;

import com.google.gson.JsonElement;

/**
 * Created by wlanjie on 14-5-27.
 */
public class FindTopicItem {
    private String type;
    private JsonElement topic;

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setTopic(JsonElement topic) {
        this.topic = topic;
    }
    public JsonElement getTopic() {
        return topic;
    }
}
