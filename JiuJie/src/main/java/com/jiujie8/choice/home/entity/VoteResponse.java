package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.Response;

/**
 * Created by wlanjie on 14/12/31.
 */
public class VoteResponse extends Response {
    private ChoiceMode entity;

    public ChoiceMode getEntity() {
        return entity;
    }

    public void setEntity(ChoiceMode entity) {
        this.entity = entity;
    }
}
