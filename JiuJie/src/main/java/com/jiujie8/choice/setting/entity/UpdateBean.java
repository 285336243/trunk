package com.jiujie8.choice.setting.entity;

import com.jiujie8.choice.Response;

/**
 * 更新bean
 */
public class UpdateBean extends Response{
    public ClientModel getEntity() {
        return entity;
    }

    public void setEntity(ClientModel entity) {
        this.entity = entity;
    }

    private ClientModel entity;

}
