package com.mzs.guaji.entity;

/**
 * Created by wlanjie on 14-4-11.
 */
public class ActivityTopicDetail extends GuaJiResponse {
    private ActivityTopicItem topic;

    public void setTopic(ActivityTopicItem topic) {
        this.topic = topic;
    }

    public ActivityTopicItem getTopic() {
        return topic;
    }
}
