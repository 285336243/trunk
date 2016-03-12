package com.example.xiadan.bean;

/**
 * 客户信息
 */
public class CustomBean {
    /**
     * 客户名称
     */
    private String user;
    /**
     *   客户积分(飞豆)
     */
    private int integral;
    /**
     * 是否可以下单(0:可以；1：不可以)
     */
    private int isOff;
    /**
     * 不可下单原因(当isOff=0时，该字段为“”)
     */
    private String offmsg;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getIsOff() {
        return isOff;
    }

    public void setIsOff(int isOff) {
        this.isOff = isOff;
    }

    public String getOffmsg() {
        return offmsg;
    }

    public void setOffmsg(String offmsg) {
        this.offmsg = offmsg;
    }

}
