package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

/**
 * 摇一摇活动bean
 */
public class ShakeResponse extends Response {
    private int isHit;
    private String congratulation;

    public int getIsHit() {
        return isHit;
    }

    public void setIsHit(int isHit) {
        this.isHit = isHit;
    }

    public String getCongratulation() {
        return congratulation;
    }

    public void setCongratulation(String congratulation) {
        this.congratulation = congratulation;
    }
}
