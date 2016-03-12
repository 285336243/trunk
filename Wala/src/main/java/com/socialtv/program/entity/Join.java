package com.socialtv.program.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-7-8.
 */
public class Join extends Response {
    private int followCnt;

    public int getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(int followCnt) {
        this.followCnt = followCnt;
    }
}
