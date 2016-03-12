package com.jiujie8.choice;

/**
 * Created by wlanjie on 14-6-23.
 */
public class Response {

    private long page;
    private long total;
    private String code;
    private String message;
    private Score givenSore;
    private ShareTemplete shareTemplete;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String responseMessage) {
        this.message = responseMessage;
    }

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }

    public Score getGivenSore() {
        return givenSore;
    }

    public void setGivenSore(Score givenSore) {
        this.givenSore = givenSore;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
