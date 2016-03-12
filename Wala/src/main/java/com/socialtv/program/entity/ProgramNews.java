package com.socialtv.program.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-25.
 */
public class ProgramNews extends Response {
    private List<News> newses;

    public List<News> getNewses() {
        return newses;
    }

    public void setNewses(List<News> newses) {
        this.newses = newses;
    }
}
