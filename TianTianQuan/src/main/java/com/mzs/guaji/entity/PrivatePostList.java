package com.mzs.guaji.entity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14-3-3.
 */
public class PrivatePostList implements Serializable {
    private String id;
    private long contactUserId;
    private String contactUserNickname;
    private String contactUserAvatar;
    private PrivatePost privatePost;
    private long status;
    private boolean isChecked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getContactUserNickname() {
        return contactUserNickname;
    }

    public void setContactUserNickname(String contactUserNickname) {
        this.contactUserNickname = contactUserNickname;
    }

    public String getContactUserAvatar() {
        return contactUserAvatar;
    }

    public void setContactUserAvatar(String contactUserAvatar) {
        this.contactUserAvatar = contactUserAvatar;
    }

    public PrivatePost getPrivatePost() {
        return privatePost;
    }

    public void setPrivatePost(PrivatePost privatePost) {
        this.privatePost = privatePost;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
