package com.mzs.guaji.topic.entity;

import com.mzs.guaji.entity.GuaJiResponse;

import java.util.List;

/**
 * 名人话题列表，
 */
public class StarTopicList extends GuaJiResponse {


    private List<Topic> topics;


    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }






}
