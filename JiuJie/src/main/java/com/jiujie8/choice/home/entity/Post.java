package com.jiujie8.choice.home.entity;

/**
 * Created by wlanjie on 14/12/10.
 */
public class Post {
    private PostItem post;
    private Agree agree;
    private String value;

    public PostItem getPost() {
        return post;
    }

    public void setPost(PostItem post) {
        this.post = post;
    }

    public Agree getAgree() {
        return agree;
    }

    public void setAgree(Agree agree) {
        this.agree = agree;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
