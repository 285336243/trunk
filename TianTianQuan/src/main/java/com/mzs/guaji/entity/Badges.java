package com.mzs.guaji.entity;

import java.util.Map;

/**
 * Created by wlanjie on 14-3-10.
 */
public class Badges extends GuaJiResponse {

    private Map<String, Integer> bages;

    public Map<String, Integer> getBages() {
        return bages;
    }

    public void setBages(Map<String, Integer> bages) {
        this.bages = bages;
    }
}
