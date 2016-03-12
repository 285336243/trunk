package com.shengzhish.xyj.news.entity;

import com.shengzhish.xyj.gallery.entity.Post;

import java.util.List;

/**
 * Created by wlanjie on 14-6-13.
 */
public class NewsPost {

    private long responseCode;
    private String responseMessage;
    private String cusorId;
    private List<Post> posts;

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

    public String getCusorId() {
        return cusorId;
    }

    public void setCusorId(String cusorId) {
        this.cusorId = cusorId;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
