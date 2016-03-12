package com.jiujie8.choice.setting.entity;

import com.jiujie8.choice.Response;

/**
 * 告诉小伙伴
 */
public class ShareBean extends Response {
    private ShareFriendModel entity;

    public ShareFriendModel getEntity() {
        return entity;
    }

    public void setEntity(ShareFriendModel entity) {
        this.entity = entity;
    }
}
