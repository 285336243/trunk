package com.socialtv.mzs.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-9-9.
 */
public class GetAward extends Response {
    private String title;
    private List<GetAwardResult> result;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<GetAwardResult> getResult() {
        return result;
    }

    public void setResult(List<GetAwardResult> result) {
        this.result = result;
    }
}
