package com.shengzhish.xyj.activity.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 颜色渲染
 */
public class Render implements Serializable{
    private int duration;
    private List<Showseq> showseq;
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Showseq> getShowseq() {
        return showseq;
    }

    public void setShowseq(List<Showseq> showseq) {
        this.showseq = showseq;
    }
}
