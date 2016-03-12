package com.mzs.guaji.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-4-9.
 */
public class ActivityTopic extends GuaJiResponse {

    private List<ActivityTopicItem> topics;

    public void setTopics(List<ActivityTopicItem> topics) {
        this.topics = topics;
    }

    public List<ActivityTopicItem> getTopics() {
        return topics;
    }
}
