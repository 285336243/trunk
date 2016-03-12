package com.mzs.guaji.topic.entity;

import com.mzs.guaji.entity.GuaJiResponse;

import java.util.List;

/**
 * Created by wlanjie on 14-3-12.
 */
public class StarDetail extends GuaJiResponse {

    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
