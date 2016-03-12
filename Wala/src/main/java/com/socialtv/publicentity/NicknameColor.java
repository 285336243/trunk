package com.socialtv.publicentity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/10/29.
 */
public class NicknameColor implements Serializable {
    private static final long serialVersionUID = -3445135186169346430L;
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
