package com.socialtv.mzs.entity;

/**
 * Created by wlanjie on 14-9-9.
 */
public class GetAwardResult {
    private int status;
    private String title;
    private String msg;
    private String vid;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }
}

