package com.socialtv.activity.entitiy;

import com.socialtv.Response;
import com.socialtv.publicentity.Topic;

import java.util.List;

/**
 * Created by wlanjie on 14-6-25.
 */
public class Activity extends Response {
    private List<Topic> topics;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
