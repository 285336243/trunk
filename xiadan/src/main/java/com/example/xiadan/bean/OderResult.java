package com.example.xiadan.bean;

/**
 * Created by 51wanh on 2015/3/1.
 */
public class OderResult {
    /**
     * 订单id
     */
    private int id;
    /**
     * 订单编号
     */
    private String orderNum;
    /**
     * 价格(包含晚间服务费)
     */
    private String  price;
    /**
     * 晚间服务费
     */
    private String nightPrice;
    /**
     * 重量
     */
    private String weight;
    /**
     * 距离
     */
    private String distance;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNightPrice() {
        return nightPrice;
    }

    public void setNightPrice(String nightPrice) {
        this.nightPrice = nightPrice;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
