package com.shengzhish.xyj.activity.entity;

import java.io.Serializable;

/**
 * 摇动颜色值
 */
public class Color implements Serializable {
    private int r;
    private int g;
    private int b;
    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
