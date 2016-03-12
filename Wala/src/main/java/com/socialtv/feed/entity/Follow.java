package com.socialtv.feed.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-7-7.
 */
public class Follow extends Response {
    private int fansCnt;

    public int getFansCnt() {
        return fansCnt;
    }

    public void setFansCnt(int fansCnt) {
        this.fansCnt = fansCnt;
    }
}
