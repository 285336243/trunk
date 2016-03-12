package com.socialtv.message.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 */
public class PrivateLetter extends Response {

    private List<PrivateLetterItem> aggregations;

    public List<PrivateLetterItem> getAggregations() {
        return aggregations;
    }

    public void setAggregations(List<PrivateLetterItem> aggregations) {
        this.aggregations = aggregations;
    }
}
