package com.example.xiadan.bean;

import java.util.List;

/**
 * 优惠券列表
 */
public class ConponBeanList {
    /**
     * 记录条数
     */
    private int size;
    /**
     * 优惠卷列表
     */
    private List<ConponBean> list;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<ConponBean> getList() {
        return list;
    }

    public void setList(List<ConponBean> list) {
        this.list = list;
    }
}
