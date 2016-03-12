package com.mzs.guaji.topic.entity;

/**
 * Created by wlanjie on 14-5-29.
 */
public class StatisticsRequest {
    private String userId;
    private String itemId;
    private String itemType;
    private String actionType;
    private long during;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public String getItemType() {
        return itemType;
    }
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    public String getActionType() {
        return actionType;
    }
    public void setDuring(long during) {
        this.during = during;
    }
    public long getDuring() {
        return during;
    }
}
