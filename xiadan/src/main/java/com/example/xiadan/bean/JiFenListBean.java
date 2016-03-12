package com.example.xiadan.bean;

import java.util.List;

/**
 *积分列表
 */
public class JiFenListBean {
    /**
     * 记录条数
     */
    private int size;
    /**
     * 我的积分总数
     */
    private int integral;
    /**
     * 积分
     */
    private List<JiFenBean> list;
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<JiFenBean> getList() {
        return list;
    }

    public void setList(List<JiFenBean> list) {
        this.list = list;
    }
    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }
}
