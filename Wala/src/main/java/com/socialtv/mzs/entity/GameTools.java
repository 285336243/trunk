package com.socialtv.mzs.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-9-2.
 */
public class GameTools {
    private long id;
    private String title;
    private String icon;
    private String img;
    private String type;
    private String desc;
    private long amount;
    private List<GamePayments> payments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public List<GamePayments> getPayments() {
        return payments;
    }

    public void setPayments(List<GamePayments> payments) {
        this.payments = payments;
    }
}
