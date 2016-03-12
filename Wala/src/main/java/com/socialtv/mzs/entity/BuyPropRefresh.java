package com.socialtv.mzs.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-9-3.
 */
public class BuyPropRefresh extends Response {
    private List<GameTools> tools;

    public List<GameTools> getTools() {
        return tools;
    }

    public void setTools(List<GameTools> tools) {
        this.tools = tools;
    }
}
