package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * 扫描所得图片集
 */
public class ScannerResponse {


    private long responseCode;
    private String responseMessage;
    private String total;
    private String gotTotal;
    private CodeItem result;
    private List<CodeItem> pics;

    public long getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGotTotal() {
        return gotTotal;
    }

    public void setGotTotal(String gotTotal) {
        this.gotTotal = gotTotal;
    }

    public CodeItem getResult() {
        return result;
    }

    public void setResult(CodeItem result) {
        this.result = result;
    }

    public List<CodeItem> getPics() {
        return pics;
    }

    public void setPics(List<CodeItem> pics) {
        this.pics = pics;
    }
}
