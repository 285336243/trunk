package com.socialtv.mzs.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-9-3.
 */
public class BuyPropSuccess extends Response {
    private BuyPropReceipt receipt;

    public BuyPropReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(BuyPropReceipt receipt) {
        this.receipt = receipt;
    }
}
