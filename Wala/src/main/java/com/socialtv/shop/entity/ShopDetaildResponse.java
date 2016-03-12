package com.socialtv.shop.entity;

import com.socialtv.Response;

/**
 * 商品详情
 */
public class ShopDetaildResponse extends Response {

    private Shop shop;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

}
