package com.socialtv.topic.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-7-1.
 */
public class TopicHeader extends Response {
    private com.socialtv.publicentity.Topic topic;

    public com.socialtv.publicentity.Topic getTopic() {
        return topic;
    }

    public void setTopic(com.socialtv.publicentity.Topic topic) {
        this.topic = topic;
    }
}
