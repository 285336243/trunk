package com.mzs.guaji.entity;

import com.mzs.guaji.topic.entity.Topic;

/**
 * Created by wlanjie on 14-4-9.
 */
public class ActivityTopicItem extends Topic {

    private String emptyText;

    public ActivityTopicItem setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        return this;
    }

    public String getEmptyText() {
        return emptyText;
    }
}
