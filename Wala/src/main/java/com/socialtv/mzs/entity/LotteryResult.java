package com.socialtv.mzs.entity;

/**
 * Created by wlanjie on 14-9-3.
 */
public class LotteryResult {
    private String title;
    private String notice;
    private long prizeLevel;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public long getPrizeLevel() {
        return prizeLevel;
    }

    public void setPrizeLevel(long prizeLevel) {
        this.prizeLevel = prizeLevel;
    }
}
