package com.shengzhish.xyj.persionalcore.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-18.
 */
public class MessageNotification extends Response {
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
