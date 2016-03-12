package com.jiujie8.choice.setting.entity;

import com.jiujie8.choice.Response;

/**
 * 用户资料
 */
public class UserDetails extends Response {
    private UserModel entity;

    public UserModel getEntity() {
        return entity;
    }

    public void setEntity(UserModel entity) {
        this.entity = entity;
    }

}
