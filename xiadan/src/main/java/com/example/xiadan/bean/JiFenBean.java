package com.example.xiadan.bean;

/**
 * 我的积分
 */
public class JiFenBean {
    /**
     * 记录id
     */
    private int id;
    /**
     * 订单编号
     */
    private String orderNum;
    /**
     * 快递员姓名
     */
    private String empName;
    /**
     * 积分
     */
    private String integral;
    /**
     * 记录时间
     */
    private String publishTime;

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

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
