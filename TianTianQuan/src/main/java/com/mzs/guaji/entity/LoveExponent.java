package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-2-21.
 */
public class LoveExponent extends GuaJiResponse {

    @Expose
    private int index;
    @Expose
    private String predictition;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPredictition() {
        return predictition;
    }

    public void setPredictition(String predictition) {
        this.predictition = predictition;
    }
}
