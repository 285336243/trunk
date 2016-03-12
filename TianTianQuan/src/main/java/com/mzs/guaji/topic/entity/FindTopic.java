package com.mzs.guaji.topic.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-5-20.
 */
public class FindTopic {

    private long responseCode;
    private String responseMessage;
    private long total;
    private String token;
    private List<FindTopicItem> topics;

    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }
    public long getResponseCode() {
        return responseCode;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public long getTotal() {
        return total;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
    public void setEntryForms(List<FindTopicItem> topics) {
        this.topics = topics;
    }
    public List<FindTopicItem> getTopics() {
        return topics;
    }
}
