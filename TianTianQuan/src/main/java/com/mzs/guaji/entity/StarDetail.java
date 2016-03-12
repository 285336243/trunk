package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by wlanjie on 14-3-12.
 */
public class StarDetail extends GuaJiResponse {

    @Expose
    private List<CelebrityPost> posts;

    public List<CelebrityPost> getPosts() {
        return posts;
    }

    public void setPosts(List<CelebrityPost> posts) {
        this.posts = posts;
    }
}
