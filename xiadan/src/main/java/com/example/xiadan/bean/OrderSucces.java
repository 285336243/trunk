package com.example.xiadan.bean;

/**
 * Created by 51wanh on 2015/3/2.
 */
public class OrderSucces {
    /**
     * 订单id
     */
    private int id;
    /**
     * 订单编号
     */
    private String orderNum;
    /**
     * 是否需要跳转(1:是;0:否),当支付方式为“网上支付时，该字段返回1，
     }
     */
    private int isredirect;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public int getIsredirect() {
        return isredirect;
    }

    public void setIsredirect(int isredirect) {
        this.isredirect = isredirect;
    }
}