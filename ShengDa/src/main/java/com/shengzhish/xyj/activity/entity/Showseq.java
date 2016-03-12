package com.shengzhish.xyj.activity.entity;


import java.io.Serializable;

/**
 * 显示序列
 */
public class Showseq implements Serializable {
    private int s;
    private int e;
    private Color color;

    public boolean compare(int second) {
        if (second >= s && second <= e) {
            return true;
        } else {
            return false;
        }
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
