package com.socialtv.program.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14/11/3.
 */
public class UpScreen extends Response {
    private String cursorId;
    private List<String> msg;

    public String getCursorId() {
        return cursorId;
    }

    public void setCursorId(String cursorId) {
        this.cursorId = cursorId;
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }
}
