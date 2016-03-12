package com.socialtv.home.entity;

import com.socialtv.Response;

import java.util.Map;

/**
 * Created by wlanjie on 14-3-10.
 */
public class Badges extends Response {

    private Map<String, Integer> bages;

    public Map<String, Integer> getBages() {
        return bages;
    }

    public void setBages(Map<String, Integer> bages) {
        this.bages = bages;
    }
}
