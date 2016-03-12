package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.home.entity.Entry;


import java.util.List;

/**
 *
 * 加入的组
 *
 *
 */
public class JoinResponse extends Response{
    private List<Entry> entries;
    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
