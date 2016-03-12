package com.socialtv.mzs.entity;

/**
 * Created by wlanjie on 14-9-3.
 */
public class BuyPropReceipt {
    private String status;
    private String transactionId;
    private String purchaseInfo;
    private String notice;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPurchaseInfo() {
        return purchaseInfo;
    }

    public void setPurchaseInfo(String purchaseInfo) {
        this.purchaseInfo = purchaseInfo;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
