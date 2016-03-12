package com.socialtv.message.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-2.
 */
public class Message extends Response {
    private String cusorId;
    private List<MessageItem> messages;

    public String getCusorId() {
        return cusorId;
    }

    public void setCusorId(String cusorId) {
        this.cusorId = cusorId;
    }

    public List<MessageItem> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageItem> messages) {
        this.messages = messages;
    }
}
