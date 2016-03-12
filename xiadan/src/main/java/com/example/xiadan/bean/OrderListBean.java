package com.example.xiadan.bean;

import java.util.List;

/**
 * 我的订单列表
 */
public class OrderListBean {
    private int size;
    private int isForbid;
    /**
     * 新订单数量
     */
    private int newSize;
    /**
     * 禁止抢单原因(isForbid为0时，该字段为“”)
     */
    private String forbidMsg;
    private int totalNum;
    /**
     * 抢单状态
     */
    private int state;
    private List<OrderItem> list;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<OrderItem> getList() {
        return list;
    }

    public void setList(List<OrderItem> list) {
        this.list = list;
    }

    public int getIsForbid() {
        return isForbid;
    }

    public void setIsForbid(int isForbid) {
        this.isForbid = isForbid;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
    public String getForbidMsg() {
        return forbidMsg;
    }

    public void setForbidMsg(String forbidMsg) {
        this.forbidMsg = forbidMsg;
    }
    public int getNewSize() {
        return newSize;
    }

    public void setNewSize(int newSize) {
        this.newSize = newSize;
    }
}
