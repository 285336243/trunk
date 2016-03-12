package com.socialtv.mzs.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-9-3.
 */
public class Lottery extends Response {
    private LotteryResult result;

    public LotteryResult getResult() {
        return result;
    }

    public void setResult(LotteryResult result) {
        this.result = result;
    }
}
