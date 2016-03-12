package com.mzs.guaji.entity.feed;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-20.
 */
public class TopicActionFeed extends Feed {
    @Expose
    private TopicAction target;

    public TopicAction getTarget() {
        return target;
    }

    public void setTarget(TopicAction target) {
        this.target = target;
    }
}
