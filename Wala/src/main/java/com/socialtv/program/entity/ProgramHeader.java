package com.socialtv.program.entity;

import com.socialtv.Response;

/**
 * Created by wlanjie on 14-7-8.
 */
public class ProgramHeader extends Response {
    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
