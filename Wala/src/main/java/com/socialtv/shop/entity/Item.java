package com.socialtv.shop.entity;


import com.socialtv.home.entity.Refer;

/**
 *单个商品信息
 *
 *
 */
public class Item {
    private String name;
    private  String img;
    private String type;
    private Refer refer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Refer getRefer() {
        return refer;
    }

    public void setRefer(Refer refer) {
        this.refer = refer;
    }
}
