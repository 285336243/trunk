package com.example.xiadan.bean;

/**
 * 优惠卷
 */
public class ConponBean {
    /**
     * 优惠券id
     */
    private int id;
    /**
     * 优惠码
     */
    private String code;
    /**
     * 状态(可使用；已绑定订单；已使用；已过期)
     */
    private String state;
    /**
     * 优惠券抵用金额
     */
    private String price;
    /**
     * 优惠券使用限额(消费金额>=该字段才可以使用)
     */
    private String usePrice;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 过期时间
     */
    private String endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsePrice() {
        return usePrice;
    }

    public void setUsePrice(String usePrice) {
        this.usePrice = usePrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
