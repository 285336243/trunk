package com.socialtv.personcenter.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-8-14.
 */
public class InviteMobile extends Response {
    private List<Mobile> matches;
    private List<String> unmatches;

    public List<Mobile> getMatches() {
        return matches;
    }

    public void setMatches(List<Mobile> matches) {
        this.matches = matches;
    }

    public List<String> getUnmatches() {
        return unmatches;
    }

    public void setUnmatches(List<String> unmatches) {
        this.unmatches = unmatches;
    }
}
