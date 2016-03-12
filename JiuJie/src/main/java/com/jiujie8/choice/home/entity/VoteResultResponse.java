package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.Response;

import java.util.List;

/**
 * Created by wlanjie on 15/1/13.
 */
public class VoteResultResponse extends Response {

    private List<VoteResultItem> rs;

    public List<VoteResultItem> getRs() {
        return rs;
    }

    public void setRs(List<VoteResultItem> rs) {
        this.rs = rs;
    }
}
