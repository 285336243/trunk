package com.socialtv.star.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-6-27.
 */
public class StarHeader extends Response {

    private StarHeaderItem celebrity;

    public StarHeaderItem getCelebrity() {
        return celebrity;
    }

    public void setCelebrity(StarHeaderItem celebrity) {
        this.celebrity = celebrity;
    }
}
