package com.jiujie8.choice.persioncenter.entity;

import com.jiujie8.choice.Response;

/**
 * 个人详情
 */
public class PersonDetails extends Response {
    private UserDetailsModel entity;


    public UserDetailsModel getEntity() {
        return entity;
    }

    public void setEntity(UserDetailsModel entity) {
        this.entity = entity;
    }
}
