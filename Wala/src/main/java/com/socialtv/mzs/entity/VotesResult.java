package com.socialtv.mzs.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-9-9.
 */
public class VotesResult extends Response {
    private String title;
    private String notice;

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
}
