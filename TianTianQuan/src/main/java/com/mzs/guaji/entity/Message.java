package com.mzs.guaji.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-3-6.
 */
public class Message extends GuaJiResponse {

    private List<MessageList> messages;

    public List<MessageList> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageList> messages) {
        this.messages = messages;
    }
}
