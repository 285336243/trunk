package com.shengzhish.xyj.activity.entity;

import com.shengzhish.xyj.Response;


import java.util.List;

/**
 *     动态评论列表
 *
 *
 */
public class CommentListResponse   extends Response{

    private String cusorId;
    private List<Post> posts;

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
