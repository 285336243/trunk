package com.socialtv.shop.entity;

import com.socialtv.Response;

/**
 * 获得用户信息
 */
public class GetShipResponse extends Response {
    private Shipping shipping;

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }


}
