package com.socialtv.message.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 */
public class PrivateLetterDetail extends Response {
    private String cusorId;

    private List<PrivateLetterDetailList> list;

    public String getCusorId() {
        return cusorId;
    }

    public void setCusorId(String cusorId) {
        this.cusorId = cusorId;
    }

    public List<PrivateLetterDetailList> getList() {
        return list;
    }

    public void setList(List<PrivateLetterDetailList> list) {
        this.list = list;
    }
}
