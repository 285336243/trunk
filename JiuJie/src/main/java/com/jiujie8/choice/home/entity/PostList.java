package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.Response;

import java.util.List;

/**
 * Created by wlanjie on 14/12/8.
 */
public class PostList extends Response {
    private List<Post> rs;

    public List<Post> getRs() {
        return rs;
    }

    public void setRs(List<Post> rs) {
        this.rs = rs;
    }
}
