package com.socialtv.mzs.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-9-4.
 */
public class Marquee extends Response {
    private List<MarqueeResult> result;

    public List<MarqueeResult> getResult() {
        return result;
    }

    public void setResult(List<MarqueeResult> result) {
        this.result = result;
    }
}
