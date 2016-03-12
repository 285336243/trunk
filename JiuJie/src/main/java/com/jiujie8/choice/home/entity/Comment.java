package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.Response;

/**
 * Created by wlanjie on 14/12/15.
 */
public class Comment extends Response {

    private Post entity;

    public Post getEntity() {
        return entity;
    }

    public void setEntity(Post entity) {
        this.entity = entity;
    }
}
