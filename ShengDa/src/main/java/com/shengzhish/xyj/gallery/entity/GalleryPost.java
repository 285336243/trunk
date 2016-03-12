package com.shengzhish.xyj.gallery.entity;

import com.shengzhish.xyj.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-4.
 */
public class GalleryPost extends Response {
    private String cusorId;
    private List<Post> posts;

    public void setCusorId(String cusorId) {
        this.cusorId = cusorId;
    }
    public String getCusorId() {
        return cusorId;
    }
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
    public List<Post> getPosts() {
        return posts;
    }
}
