package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * 投票list
 */
public class VoteListResponse extends Response {
    private String mode;
    private List<VoteList> list;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<VoteList> getList() {
        return list;
    }

    public void setList(List<VoteList> list) {
        this.list = list;
    }
}
