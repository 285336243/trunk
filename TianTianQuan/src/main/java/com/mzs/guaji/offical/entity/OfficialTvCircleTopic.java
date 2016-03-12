package com.mzs.guaji.offical.entity;

import com.google.gson.annotations.Expose;
import com.mzs.guaji.entity.GuaJiResponse;
import com.mzs.guaji.topic.entity.Topic;

import java.util.List;

/**
 * Created by wlanjie on 13-12-30.
 */
public class OfficialTvCircleTopic extends GuaJiResponse {

    @Expose
    private List<Topic> topics;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
