package com.socialtv.shop.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * 商品信息
 */
public class Goods extends Response {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
