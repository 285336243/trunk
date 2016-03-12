package com.socialtv.mzs.entity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-9-2.
 */
public class GamePayments implements Serializable {
    private static final long serialVersionUID = 6111902835412456209L;
    private long id;
    private String price;
    private String showPrice;
    private String payType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(String showPrice) {
        this.showPrice = showPrice;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
