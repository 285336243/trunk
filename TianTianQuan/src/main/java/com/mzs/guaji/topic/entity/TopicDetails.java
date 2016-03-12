package com.mzs.guaji.topic.entity;

import com.mzs.guaji.entity.GuaJiResponse;

/**
 * Created by wlanjie on 13-12-25.
 */
public class TopicDetails extends GuaJiResponse {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
