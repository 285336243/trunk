package com.socialtv.message.entity;

import com.socialtv.publicentity.User;

/**
 * Created by wlanjie on 14-7-3.
 */
public class PrivateLetterItem {
    private String id;
    private User contactUser;
    private PrivatePost privatePost;
    private int status;
    private String emptyText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getContactUser() {
        return contactUser;
    }

    public void setContactUser(User contactUser) {
        this.contactUser = contactUser;
    }

    public PrivatePost getPrivatePost() {
        return privatePost;
    }

    public void setPrivatePost(PrivatePost privatePost) {
        this.privatePost = privatePost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }
}
