package com.socialtv.topic.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-1.
 */
public class Topic extends Response {
    private List<TopicPosts> posts;

    public List<TopicPosts> getPosts() {
        return posts;
    }

    public void setPosts(List<TopicPosts> posts) {
        this.posts = posts;
    }
}
